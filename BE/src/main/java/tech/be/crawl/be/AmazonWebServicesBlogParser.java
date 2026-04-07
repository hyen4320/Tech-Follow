package tech.be.crawl.be;

import org.springframework.stereotype.Component;
import tech.be.crawl.AbstractRssParser;
import tech.be.model.Category;

@Component
public class AmazonWebServicesBlogParser extends AbstractRssParser {
    @Override protected String feedUrl()         { return "https://aws.amazon.com/blogs/aws/feed/"; }
    @Override protected Category category()      { return Category.BE; }
    @Override protected String contentSelector() { return ".blog-post-content, article, #blog-post-content, main"; }
}
