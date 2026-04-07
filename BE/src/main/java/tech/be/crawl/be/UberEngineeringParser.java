package tech.be.crawl.be;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import tech.be.crawl.CrawlParser;
import tech.be.model.Category;
import tech.be.model.Sources;
import tech.be.model.Trends;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class UberEngineeringParser implements CrawlParser {

    private static final String BASE_URL = "https://www.uber.com/us/en/blog/engineering/";
    private static final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

    @Override
    public String baseUrl() { return BASE_URL; }

    @Override
    public List<Trends> parse(Sources source) {
        List<Trends> result = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(BASE_URL).userAgent(UA).timeout(10_000).get();

            Elements articles = doc.select("article, [data-baseweb='block'], .css-jnCjHJ");
            for (Element article : articles) {
                String title = article.select(
                        "h2, h3, [data-baseweb='typo-headingmedium'], [data-baseweb='typo-headingsmall']"
                ).text();
                String url = article.select("a[href]").attr("abs:href");
                if (title.isBlank() || url.isBlank()) continue;
                if (!isValidUberBlogUrl(url)) continue;
                result.add(build(source, title, url));
            }

            // article 셀렉터로 못 잡을 경우 a 태그 직접 순회
            if (result.isEmpty()) {
                for (Element link : doc.select("a[href*='/blog/engineering/']")) {
                    String url   = link.attr("abs:href");
                    String title = link.text();
                    if (title.isBlank() || url.isBlank()) continue;
                    if (!url.contains("uber.com")) continue;
                    if (url.endsWith("/blog/engineering/") || url.endsWith("/blog/engineering")) continue;
                    result.add(build(source, title, url));
                }
            }
        } catch (IOException e) {
            System.err.println("[UberEngineeringParser] 목록 크롤링 실패: " + e.getMessage());
        }
        return result;
    }

    @Override
    public String parseContent(String url) {
        try {
            Document doc = Jsoup.connect(url).userAgent(UA).timeout(10_000).get();
            Element body = doc.selectFirst("article, .article__body, main");
            return body != null ? body.text() : doc.body().text();
        } catch (IOException e) {
            System.err.println("[UberEngineeringParser] 본문 크롤링 실패: " + url + " / " + e.getMessage());
            return null;
        }
    }

    private boolean isValidUberBlogUrl(String url) {
        if (!url.contains("uber.com") || !url.contains("/blog/")) return false;
        return !url.contains("ubereats.com") && !url.contains("drivers.uber")
            && !url.contains("business.uber") && !url.contains("auth.uber")
            && !url.contains("account.uber") && !url.contains("m.uber.com");
    }

    private Trends build(Sources source, String title, String url) {
        return Trends.builder()
                .sources(source).title(title).originUrl(url)
                .category(Category.BE).collectedAt(LocalDateTime.now()).build();
    }
}
