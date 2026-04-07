package tech.be.crawl.security;

import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import tech.be.crawl.AbstractRssParser;
import tech.be.model.Category;
import tech.be.model.Sources;
import tech.be.model.Trends;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TheHackerNews: 사이트 자체가 403 을 반환하므로 RSS description 을 content 로 바로 저장.
 * parse() 만 custom, parseContent() 는 null 반환.
 */
@Component
public class TheHackerNewsParser extends AbstractRssParser {
    @Override protected String feedUrl()         { return "https://feeds.feedburner.com/TheHackersNews"; }
    @Override protected Category category()      { return Category.SECURITY; }
    @Override protected String contentSelector() { return "article, main"; }

    @Override
    public List<Trends> parse(Sources source) {
        List<Trends> result = new ArrayList<>();
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.connect(feedUrl())
                    .userAgent("Mozilla/5.0").timeout(10_000)
                    .parser(org.jsoup.parser.Parser.xmlParser()).get();
            for (Element item : doc.select("item")) {
                String title       = item.select("title").text();
                String url         = item.select("link").text();
                String description = item.select("description").text();
                if (title.isBlank() || url.isBlank()) continue;
                result.add(Trends.builder()
                        .sources(source).title(title).originUrl(url)
                        .content(description.isBlank() ? null : description)
                        .category(Category.SECURITY).collectedAt(LocalDateTime.now()).build());
            }
        } catch (java.io.IOException e) {
            System.err.printf("[TheHackerNewsParser] 목록 크롤링 실패: %s%n", e.getMessage());
        }
        return result;
    }

    /** 사이트 403 차단 — RSS description 을 이미 content 로 저장하므로 호출 안 됨 */
    @Override
    public String parseContent(String url) { return null; }
}
