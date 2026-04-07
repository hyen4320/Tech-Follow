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
public class HuggingFaceParser implements CrawlParser {

    private static final String BASE_URL = "https://huggingface.co/blog";

    @Override
    public String baseUrl() { return BASE_URL; }

    @Override
    public List<Trends> parse(Sources source) {
        List<Trends> result = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(BASE_URL).userAgent("Mozilla/5.0").timeout(10_000).get();
            Elements articles = doc.select("article, .blog-card");
            for (Element article : articles) {
                String title = article.select("h4, h3, h2").text();
                String relUrl = article.select("a[href]").attr("href");
                String url = relUrl.startsWith("http") ? relUrl : "https://huggingface.co" + relUrl;
                if (title.isBlank() || url.isBlank()) continue;
                result.add(Trends.builder()
                        .sources(source).title(title).originUrl(url)
                        .category(Category.AI).collectedAt(LocalDateTime.now()).build());
            }
        } catch (IOException e) {
            System.err.println("[HuggingFaceParser] 목록 크롤링 실패: " + e.getMessage());
        }
        return result;
    }

    @Override
    public String parseContent(String url) {
        try {
            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(10_000).get();
            Element body = doc.selectFirst(".prose, article, main");
            if (body == null) body = doc.body();
            return body.text();
        } catch (IOException e) {
            System.err.println("[HuggingFaceParser] 본문 크롤링 실패: " + url + " / " + e.getMessage());
            return null;
        }
    }
}
