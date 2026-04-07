package tech.be.crawl.be;

import org.springframework.stereotype.Component;
import tech.be.crawl.AbstractRssParser;
import tech.be.model.Category;

@Component
public class InfoQParser extends AbstractRssParser {
    @Override protected String feedUrl()         { return "https://feed.infoq.com/development"; }
    @Override protected Category category()      { return Category.BE; }
    @Override protected String contentSelector() { return ".article__content, article, main"; }
}
