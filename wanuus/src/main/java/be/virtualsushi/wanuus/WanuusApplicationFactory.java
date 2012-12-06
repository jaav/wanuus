package be.virtualsushi.wanuus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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

}
