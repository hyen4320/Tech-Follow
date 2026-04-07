package tech.be.crawl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tech.be.model.Sources;
import tech.be.model.Trends;
import tech.be.repository.SourcesRepository;
import tech.be.repository.TrendsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CrawlScheduler {

    private final List<CrawlParser> parsers;         // Spring 이 모든 구현체 자동 주입
    private final TrendsRepository trendsRepository;
    private final SourcesRepository sourcesRepository;
    private final ExecutorService crawlExecutor;     // CrawlConfig Bean — 재사용

    // 1단계: 매일 오전 9시 — 목록 수집 (전 사이트 병렬)
    @Scheduled(cron = "0 0 9 * * *")
    public void collectList() {
        Map<String, Sources> sourceMap = sourcesRepository.findAll().stream()
                .collect(Collectors.toMap(Sources::getBaseUrl, Function.identity()));
        // 최근 30일 이내 URL 만 로드 — 그 이전 글이 다시 피드에 올라올 가능성 없음
        Set<String> existingUrls = ConcurrentHashMap.newKeySet();
        existingUrls.addAll(trendsRepository.findRecentOriginUrls(LocalDateTime.now().minusDays(30)));

        List<Trends> toSave = parsers.stream()
                .map(parser -> CompletableFuture.supplyAsync(() -> {
                    Sources source = sourceMap.get(parser.baseUrl());
                    if (source == null) {
                        System.err.printf("[CrawlScheduler] Sources 에 없는 URL: %s%n", parser.baseUrl());
                        return List.<Trends>of();
                    }
                    return parser.parse(source);
                }, crawlExecutor))
                .toList()
                .stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .filter(t -> t.getOriginUrl() != null && existingUrls.add(t.getOriginUrl()))
                .toList();

        trendsRepository.saveAll(toSave);
        System.out.printf("[CrawlScheduler] 1단계 완료 — 신규 저장: %d건%n", toSave.size());
    }

    // 2단계: 매일 오전 10시 — 본문 수집 (병렬)
    @Scheduled(cron = "0 0 10 * * *")
    public void collectContent() {
        List<Trends> targets = trendsRepository.findByContentIsNull();
        if (targets.isEmpty()) {
            System.out.println("[CrawlScheduler] 2단계 — 수집 대상 없음");
            return;
        }

        Map<String, CrawlParser> parserMap = parsers.stream()
                .collect(Collectors.toMap(CrawlParser::baseUrl, Function.identity()));

        List<CompletableFuture<Void>> futures = targets.stream()
                .map(trend -> CompletableFuture.runAsync(() -> {
                    CrawlParser parser = parserMap.get(trend.getSources().getBaseUrl());
                    if (parser == null) return;
                    String content = parser.parseContent(trend.getOriginUrl());
                    if (content != null) trend.setContent(content);
                }, crawlExecutor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<Trends> filled = targets.stream()
                .filter(t -> t.getContent() != null)
                .toList();

        trendsRepository.saveAll(filled);
        System.out.printf("[CrawlScheduler] 2단계 완료 — 처리: %d건 / 저장: %d건%n",
                targets.size(), filled.size());
    }
}
