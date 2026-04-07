package tech.be.crawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.parser.Parser;
import tech.be.model.Sources;
import tech.be.model.Trends;
import tech.be.model.Category;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * RSS / Atom 피드를 읽는 파서들의 공통 로직.
 * 구현체는 feedUrl / category / contentSelector 만 정의하면 된다.
 */
public abstract class AbstractRssParser implements CrawlParser {

    /** RSS/Atom 피드 URL (= baseUrl) */
    protected abstract String feedUrl();

    protected abstract Category category();

    /** 본문 페이지에서 사용할 CSS 셀렉터 (우선순위 순서로 콤마 구분) */
    protected abstract String contentSelector();

    /** RSS → "item",  Atom → "entry" */
    protected String itemTag() { return "item"; }

    /** RSS link 태그 이름. 대부분 "link" 이지만 Atom 은 "link[href]" 로 override */
    protected String urlFrom(Element item) {
        String url = item.select("link").text();
        if (url.isBlank()) url = item.select("guid, id").text();
        return url;
    }

    @Override
    public String baseUrl() { return feedUrl(); }

    @Override
    public List<Trends> parse(Sources source) {
        List<Trends> result = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(feedUrl())
                    .userAgent("Mozilla/5.0")
                    .timeout(10_000)
                    .ignoreContentType(true)
                    .parser(Parser.xmlParser())
                    .get();
            for (Element item : doc.select(itemTag())) {
                String title = item.select("title").text();
                String url   = urlFrom(item);
                if (title.isBlank() || url.isBlank()) continue;
                result.add(Trends.builder()
                        .sources(source).title(title).originUrl(url)
                        .category(category()).collectedAt(LocalDateTime.now()).build());
            }
        } catch (IOException e) {
            System.err.printf("[%s] 목록 크롤링 실패: %s%n", getClass().getSimpleName(), e.getMessage());
        }
        return result;
    }

    @Override
    public String parseContent(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10_000)
                    .ignoreHttpErrors(true)
                    .get();
            Element body = doc.selectFirst(contentSelector());
            return body != null ? body.text() : doc.body().text();
        } catch (IOException e) {
            System.err.printf("[%s] 본문 크롤링 실패: %s / %s%n",
                    getClass().getSimpleName(), url, e.getMessage());
            return null;
        }
    }
}
