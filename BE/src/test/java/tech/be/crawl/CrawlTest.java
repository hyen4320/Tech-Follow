package tech.be.crawl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tech.be.crawl.ai.DeepMindParser;
import tech.be.crawl.ai.GoogleAIParser;
import tech.be.crawl.ai.HuggingFaceParser;
import tech.be.crawl.be.AmazonWebServicesBlogParser;
import tech.be.crawl.be.InfoQParser;
import tech.be.crawl.be.MartinFowlerBlogParser;
import tech.be.crawl.be.NetflixTechBlogParser;
import tech.be.crawl.be.UberEngineeringParser;
import tech.be.crawl.security.CISAParser;
import tech.be.crawl.security.TheHackerNewsParser;
import tech.be.model.Sources;
import tech.be.model.Trends;
import tech.be.repository.SourcesRepository;
import tech.be.repository.TrendsRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DB에 저장된 Sources 를 기반으로 파서/스케줄러를 실제 실행하는 통합 테스트
 * - data.sql 로 Sources 초기 데이터가 들어있어야 정상 동작
 * - 실제 HTTP 요청 + 실제 DB 저장/검증
 */
@SpringBootTest
public class CrawlTest {

    @Autowired SourcesRepository sourcesRepository;
    @Autowired TrendsRepository  trendsRepository;
    @Autowired CrawlScheduler    crawlScheduler;

    // 파서 직접 주입 (CrawlPack 제거)
    @Autowired DeepMindParser              deepMindParser;
    @Autowired GoogleAIParser              googleAIParser;
    @Autowired HuggingFaceParser           huggingFaceParser;
    @Autowired CISAParser                  cisaParser;
    @Autowired TheHackerNewsParser         theHackerNewsParser;
    @Autowired NetflixTechBlogParser       netflixTechBlogParser;
    @Autowired UberEngineeringParser       uberEngineeringParser;
    @Autowired AmazonWebServicesBlogParser amazonWebServicesBlogParser;
    @Autowired InfoQParser                 infoQParser;
    @Autowired MartinFowlerBlogParser      martinFowlerBlogParser;

    // 전체 파서 목록 — 통합 테스트용
    @Autowired List<CrawlParser> parsers;

    // ────────────────────────────────────────────────────────────
    // DB에서 특정 사이트의 Source 조회 헬퍼
    // ────────────────────────────────────────────────────────────
    private Sources findSource(String baseUrl) {
        return sourcesRepository.findAll().stream()
                .filter(s -> s.getBaseUrl().equals(baseUrl))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Sources 에 없음: " + baseUrl));
    }

    // 파서 baseUrl → Sources 매핑 한 번에 로드
    private Map<String, Sources> sourceMap() {
        return sourcesRepository.findAll().stream()
                .collect(Collectors.toMap(Sources::getBaseUrl, Function.identity()));
    }


    // ════════════════════════════════════════════════════════════
    // 1. 파서 개별 테스트 — DB Source 사용, 실제 HTTP 요청
    // ════════════════════════════════════════════════════════════

    @Test @DisplayName("DeepMind - 목록 수집")
    void deepMind_parse() {
        assertParseResult("DeepMind", deepMindParser.parse(findSource(deepMindParser.baseUrl())));
    }

    @Test @DisplayName("DeepMind - 본문 수집")
    void deepMind_parseContent() {
        List<Trends> list = deepMindParser.parse(findSource(deepMindParser.baseUrl()));
        if (list.isEmpty()) return;
        String content = deepMindParser.parseContent(list.get(0).getOriginUrl());
        assertThat(content).isNotBlank();
        System.out.println("[DeepMind 본문 미리보기] " + content.substring(0, Math.min(200, content.length())));
    }

    @Test @DisplayName("GoogleAI - 목록 수집")
    void googleAI_parse() {
        assertParseResult("GoogleAI", googleAIParser.parse(findSource(googleAIParser.baseUrl())));
    }

    @Test @DisplayName("HuggingFace - 목록 수집")
    void huggingFace_parse() {
        assertParseResult("HuggingFace", huggingFaceParser.parse(findSource(huggingFaceParser.baseUrl())));
    }

    @Test @DisplayName("CISA - 목록 수집")
    void cisa_parse() {
        assertParseResult("CISA", cisaParser.parse(findSource(cisaParser.baseUrl())));
    }

    @Test @DisplayName("TheHackerNews - 목록 수집")
    void theHackerNews_parse() {
        assertParseResult("TheHackerNews", theHackerNewsParser.parse(findSource(theHackerNewsParser.baseUrl())));
    }

    @Test @DisplayName("Netflix Tech Blog - 목록 수집")
    void netflix_parse() {
        assertParseResult("Netflix", netflixTechBlogParser.parse(findSource(netflixTechBlogParser.baseUrl())));
    }

    @Test @DisplayName("Uber Engineering - 목록 수집")
    void uber_parse() {
        assertParseResult("Uber", uberEngineeringParser.parse(findSource(uberEngineeringParser.baseUrl())));
    }

    @Test @DisplayName("AWS Blog - 목록 수집")
    void aws_parse() {
        assertParseResult("AWS", amazonWebServicesBlogParser.parse(findSource(amazonWebServicesBlogParser.baseUrl())));
    }

    @Test @DisplayName("InfoQ - 목록 수집")
    void infoQ_parse() {
        assertParseResult("InfoQ", infoQParser.parse(findSource(infoQParser.baseUrl())));
    }

    @Test @DisplayName("Martin Fowler Blog - 목록 수집")
    void martinFowler_parse() {
        assertParseResult("MartinFowler", martinFowlerBlogParser.parse(findSource(martinFowlerBlogParser.baseUrl())));
    }


    // ════════════════════════════════════════════════════════════
    // 2. 전체 파서 일괄 테스트 — 파서 추가 시 자동 포함
    // ════════════════════════════════════════════════════════════

    @Test
    @DisplayName("모든 파서 - 목록 수집 (파서 추가 시 자동 포함)")
    void allParsers_parse() {
        Map<String, Sources> sourceMap = sourceMap();
        for (CrawlParser parser : parsers) {
            Sources source = sourceMap.get(parser.baseUrl());
            if (source == null) {
                System.err.printf("[SKIP] Sources 에 없는 URL: %s%n", parser.baseUrl());
                continue;
            }
            List<Trends> result = parser.parse(source);
            System.out.printf("[%s] 수집: %d건%n", parser.getClass().getSimpleName(), result.size());
            assertThat(result)
                    .as(parser.getClass().getSimpleName() + " 결과가 비어있음")
                    .isNotEmpty();
        }
    }


    // ════════════════════════════════════════════════════════════
    // 3. CrawlScheduler 통합 테스트 — 실제 DB 저장/검증
    // ════════════════════════════════════════════════════════════

    @Test
    @DisplayName("collectList - 실행 후 Trends 가 DB에 저장됨")
    void collectList_savesToDB() {
        long before = trendsRepository.count();

        crawlScheduler.collectList();

        long after = trendsRepository.count();
        System.out.printf("[collectList] 저장 전: %d건 / 저장 후: %d건 / 신규: %d건%n",
                before, after, after - before);

        assertThat(after).isGreaterThan(before);
    }

    @Test
    @DisplayName("collectList - 같은 URL 이 이미 있으면 중복 저장 안 됨")
    void collectList_noDuplicates() {
        crawlScheduler.collectList();
        long afterFirst = trendsRepository.count();

        crawlScheduler.collectList();
        long afterSecond = trendsRepository.count();

        System.out.printf("[중복 방지] 1회: %d건 / 2회: %d건%n", afterFirst, afterSecond);
        assertThat(afterFirst).isEqualTo(afterSecond);
    }

    @Test
    @DisplayName("collectContent - content null 인 Trends 에 본문이 채워짐")
    void collectContent_fillsContentToDB() {
        crawlScheduler.collectList();

        long nullBefore = trendsRepository.findByContentIsNull().size();
        System.out.printf("[collectContent] 본문 미수집: %d건%n", nullBefore);

        crawlScheduler.collectContent();

        long nullAfter = trendsRepository.findByContentIsNull().size();
        System.out.printf("[collectContent] 본문 수집 후 미수집: %d건%n", nullAfter);

        assertThat(nullAfter).isLessThan(nullBefore);
    }

    @Test
    @DisplayName("Sources - DB에 등록된 URL 과 파서 baseUrl 이 모두 일치")
    void sources_allParsersHaveMatchingSource() {
        Map<String, Sources> sourceMap = sourceMap();

        System.out.println("[Sources] 등록된 사이트 목록:");
        sourceMap.values().forEach(s -> System.out.printf("  [%s] %s - %s%n",
                s.getCategory(), s.getName(), s.getBaseUrl()));

        assertThat(sourceMap).isNotEmpty();
        for (CrawlParser parser : parsers) {
            assertThat(sourceMap)
                    .as("파서 [%s] 의 baseUrl(%s) 이 Sources DB에 없음",
                            parser.getClass().getSimpleName(), parser.baseUrl())
                    .containsKey(parser.baseUrl());
        }
    }


    // ════════════════════════════════════════════════════════════
    // 공용 헬퍼
    // ════════════════════════════════════════════════════════════
    private void assertParseResult(String parserName, List<Trends> result) {
        System.out.printf("[%s] 수집 건수: %d%n", parserName, result.size());
        result.forEach(t -> System.out.printf("  - %s | %s%n", t.getTitle(), t.getOriginUrl()));

        assertThat(result).isNotEmpty();
        assertThat(result).allSatisfy(trend -> {
            assertThat(trend.getTitle()).isNotBlank();
            assertThat(trend.getOriginUrl()).isNotBlank();
            assertThat(trend.getSources()).isNotNull();
            assertThat(trend.getSources().getId()).isNotNull();
        });
    }
}
