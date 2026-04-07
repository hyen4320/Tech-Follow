package tech.be.crawl.be;

import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import tech.be.crawl.AbstractRssParser;
import tech.be.model.Category;

@Component
public class MartinFowlerBlogParser extends AbstractRssParser {

    @Override protected String feedUrl()         { return "https://martinfowler.com/feed.atom"; }
    @Override protected Category category()      { return Category.BE; }
    @Override protected String contentSelector() { return "article, .paper, main"; }
    @Override protected String itemTag()         { return "entry"; }  // Atom 형식

    @Override
    protected String urlFrom(Element item) {
        String url = item.select("link[href]").attr("href");
        if (url.isBlank()) url = item.select("id").text();
        return url;
    }
}
