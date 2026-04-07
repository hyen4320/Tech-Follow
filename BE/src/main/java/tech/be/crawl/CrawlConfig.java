package tech.be.crawl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CrawlConfig {

    /**
     * 크롤링 전용 스레드풀.
     * 서버가 항상 떠 있으므로 앱 시작 시 한 번만 생성하고 재사용한다.
     * 너무 높이면 대상 사이트에서 차단될 수 있으므로 5로 고정.
     */
    @Bean(destroyMethod = "shutdown")
    public ExecutorService crawlExecutor() {
        return Executors.newFixedThreadPool(5);
    }
}
