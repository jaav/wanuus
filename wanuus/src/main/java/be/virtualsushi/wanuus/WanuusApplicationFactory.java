package be.virtualsushi.wanuus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;

@Configuration
@ComponentScan(basePackages = { "be.virtualsushi.wanuus" })
@PropertySource("classpath:application.properties")
@ImportResource("META-INF/infrastructure.xml")
@EnableScheduling
@EnableJpaRepositories
public class WanuusApplicationFactory {

	@Bean(name = "twitter")
	public Twitter getTwitter() {
		return TwitterFactory.getSingleton();
	}

}
