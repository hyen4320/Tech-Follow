package tech.be.crawl.security;

import org.springframework.stereotype.Component;
import tech.be.crawl.AbstractRssParser;
import tech.be.model.Category;

/**
 * CISA: Sources.baseUrl = "https://www.cisa.gov" 이지만
 * 실제 피드는 /cybersecurity-advisories/all.xml 이므로 baseUrl 을 별도로 override.
 */
@Component
public class CISAParser extends AbstractRssParser {
    @Override protected String feedUrl()         { return "https://www.cisa.gov/cybersecurity-advisories/all.xml"; }
    @Override protected Category category()      { return Category.SECURITY; }
    @Override protected String contentSelector() { return ".l-full__main, article, main"; }
    @Override public    String baseUrl()         { return "https://www.cisa.gov"; }
}
