package tech.be.crawl.ai;

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
public class GoogleAIParser implements CrawlParser {

    private static final String BASE_URL = "https://blog.google/technology/ai/";

    @Override
    public String baseUrl() { return BASE_URL; }
    @Override
    public List<Trends> parse(Sources source) {
        List<Trends> result = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(BASE_URL)
                    .userAgent("Mozilla/5.0")
                    .timeout(10_000)
                    .get();
            Elements articles = doc.select("article, .article-info");
            for (Element article : articles) {
                String title = article.select("h3, h2, .article-title").text();
                String url = article.select("a[href]").attr("abs:href");
                if (title.isBlank() || url.isBlank()) continue;
                result.add(Trends.builder()
                        .sources(source).title(title).originUrl(url)
                        .category(Category.AI).collectedAt(LocalDateTime.now()).build());
            }
        } catch (IOException e) {
            System.err.println("[GoogleAIParser] 목록 크롤링 실패: " + e.getMessage());
        }
        return result;
    }
    @Override
    public String parseContent(String url) {
        try {
            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(10_000).get();
            Element body = doc.selectFirst("article, .article-body, main");
            if (body == null) body = doc.body();
            return body.text();
        } catch (IOException e) {
            System.err.println("[GoogleAIParser] 본문 크롤링 실패: " + url + " / " + e.getMessage());
            return null;
        }
    }
}
