package tech.be.crawl.be;

import org.springframework.stereotype.Component;
import tech.be.crawl.AbstractRssParser;
import tech.be.model.Category;

@Component
public class NetflixTechBlogParser extends AbstractRssParser {

    static {
        // SSL 인증서 검증 우회 (netflixtechblog.com PKIX 오류 대응)
        try {
            javax.net.ssl.TrustManager[] trustAll = new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[0]; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] c, String a) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] c, String a) {}
                }
            };
            javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
            sc.init(null, trustAll, new java.security.SecureRandom());
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((h, s) -> true);
        } catch (Exception ignored) {}
    }

    @Override protected String feedUrl()         { return "https://netflixtechblog.com/feed"; }
    @Override protected Category category()      { return Category.BE; }
    @Override protected String contentSelector() { return "article, .postArticle-content, section, main"; }
}
