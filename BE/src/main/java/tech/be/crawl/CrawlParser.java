package tech.be.crawl;

import tech.be.model.Sources;
import tech.be.model.Trends;

import java.util.List;

public interface CrawlParser {

    /** Sources.baseUrl 과 일치하는 값을 반환 */
    String baseUrl();

    /** 1단계: 목록 수집 */
    List<Trends> parse(Sources source);

    /** 2단계: 본문 수집 */
    String parseContent(String url);
}
