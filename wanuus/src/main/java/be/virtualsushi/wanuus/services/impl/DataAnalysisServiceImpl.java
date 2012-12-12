package be.virtualsushi.wanuus.services.impl;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import be.virtualsushi.wanuus.model.Tweet;
import be.virtualsushi.wanuus.model.TweetObject;
import be.virtualsushi.wanuus.model.TweetStates;
import be.virtualsushi.wanuus.repositories.TweetRepository;
import be.virtualsushi.wanuus.services.DataAnalysisService;
import be.virtualsushi.wanuus.services.ImageProcessService;

@Service("dataAnalysisService")
public class DataAnalysisServiceImpl implements DataAnalysisService {

	private static final Logger log = LoggerFactory.getLogger(DataAnalysisServiceImpl.class);

	private class PostTweetRunnable implements Runnable {

		private final Tweet tweet;

		public PostTweetRunnable(Tweet tweet) {
			this.tweet = tweet;
		}

		@Override
		public void run() {
			try {
				File imageFile = imageProcessService.createTweetImage(tweet).get();
				// TODO post to GAE
			} catch (Exception e) {
				log.error("Error posting tweet. Tweet id - " + tweet.getId(), e);
			}
		}

	}

	@Autowired
	private TweetRepository tweetRepository;

	@Autowired
	private ImageProcessService imageProcessService;

	@Autowired
	private TaskExecutor taskExecutor;

	@Override
	public void analyseTweets() {
		try {
			List<Tweet> tweets = tweetRepository.getNotRatedTweets();
			log.debug("Rating tweets. Count: " + tweets.size());
			rateTweets(tweets);
			log.debug("Processing top rated tweets.");
			processTopRatedTweets(tweetRepository.getTopRatedTweets(new PageRequest(0, 5, Direction.DESC, "rate")));
		} catch (Exception e) {
			log.error("Error analysing tweets.", e);
		}
	}

	private void processTopRatedTweets(Page<Tweet> topRatedTweets) {
		for (Tweet tweet : topRatedTweets.getContent()) {
			tweet.setState(TweetStates.TOP_RATED);
			taskExecutor.execute(new PostTweetRunnable(tweetRepository.save(tweet)));
		}
	}

	private void rateTweets(List<Tweet> tweets) {
		for (Tweet tweet : tweets) {
			int rate = tweet.getRawRate();
			for (TweetObject object : tweet.getObjects()) {
				rate += object.getQuantity() * object.getQuantityFactor();
			}
			tweet.setRate(rate);
			tweet.setState(TweetStates.RATED);
		}
		tweetRepository.save(tweets);
	}

}
