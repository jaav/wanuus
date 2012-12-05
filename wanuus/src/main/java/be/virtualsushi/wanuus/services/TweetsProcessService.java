package be.virtualsushi.wanuus.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import twitter4j.Twitter;
import be.virtualsushi.wanuus.repositories.TweetRepository;

@Service("tweetsProcess")
public class TweetsProcessService {

	@Autowired
	private TweetRepository tweetRepository;

	@Autowired
	private Twitter twitter;

	@Scheduled(fixedRate = 15 * 60 * 1000)
	public void processTweets() {
		System.out.println("perocessing tweets");
	}

}
