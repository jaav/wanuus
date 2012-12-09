package be.virtualsushi.wanuus.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import be.virtualsushi.wanuus.model.Tweet;
import be.virtualsushi.wanuus.model.TweetObject;
import be.virtualsushi.wanuus.model.TweetStates;
import be.virtualsushi.wanuus.repositories.TweetRepository;

@Service("dataAnalysisService")
public class DataAnalysisServiceImpl implements DataAnalysisService {

	private static final Logger log = LoggerFactory.getLogger(DataAnalysisServiceImpl.class);

	@Autowired
	private TweetRepository tweetRepository;

	@Override
	// @Scheduled(fixedRate = 15 * 60 * 1000)
	public void analyseTweets() {
		List<Tweet> tweets = tweetRepository.getNotRatedTweets();
		log.debug("Rating tweets. Count: " + tweets.size());
		rateTweets(tweets);
		log.debug("Processing top rated tweets.");
		processTopRatedTweets(tweetRepository.getTopRatedTweets(new PageRequest(0, 5, Direction.DESC, "rate")));
	}

	private void processTopRatedTweets(Page<Tweet> topRatedTweets) {
		for (Tweet tweet : topRatedTweets.getContent()) {
			tweet.setState(TweetStates.TOP_RATED);
			tweetRepository.save(tweet);
		}

	}

	private void rateTweets(List<Tweet> tweets) {
		for (Tweet tweet : tweets) {
			int rate = tweet.getQuantity() * 2;
			for (TweetObject object : tweet.getObjects()) {
				rate += object.getQuantity() * object.getQuantityFactor();
			}
			tweet.setRate(rate);
			tweet.setState(TweetStates.RATED);
		}
		tweetRepository.save(tweets);
	}

}
