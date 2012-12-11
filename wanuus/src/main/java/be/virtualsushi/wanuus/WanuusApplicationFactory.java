package be.virtualsushi.wanuus;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@Configuration
@ComponentScan(basePackages = { "be.virtualsushi.wanuus" })
@PropertySource("classpath:application.properties")
@ImportResource("META-INF/infrastructure.xml")
@EnableScheduling
@EnableJpaRepositories
@EnableTransactionManagement
public class WanuusApplicationFactory {

	@Bean(name = "twitter")
	public Twitter getTwitter() {
		return TwitterFactory.getSingleton();
	}

	@Bean(name = "propertyConfigurer")
	public PropertySourcesPlaceholderConfigurer getPropertyPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean(name = "twitterStream")
	public TwitterStream getTwitterStream() {
		return TwitterStreamFactory.getSingleton();
	}

	@Bean(name = "httpClient")
	public HttpClient getHttpClient() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(100);
		connectionManager.setDefaultMaxPerRoute(30);

		HttpParams params = new BasicHttpParams();
		params.setParameter("http.protocol.handle-redirects", false);

		return new DecompressingHttpClient(new DefaultHttpClient(connectionManager, params));
	}

	@Bean(name = "restTemplate")
	public RestTemplate getRestTemplate() {
		return new RestTemplate(new HttpComponentsClientHttpRequestFactory(getHttpClient()));
	}

}
