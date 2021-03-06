package be.virtualsushi.wanuus.services.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.URLEntity;
import be.virtualsushi.wanuus.components.ShortUrlsProcessor;
import be.virtualsushi.wanuus.model.Tweet;
import be.virtualsushi.wanuus.model.TweetObject;
import be.virtualsushi.wanuus.model.TweetObjectTypes;
import be.virtualsushi.wanuus.model.TweetStates;
import be.virtualsushi.wanuus.model.TwitterUser;
import be.virtualsushi.wanuus.repositories.TweetObjectRepository;
import be.virtualsushi.wanuus.repositories.TweetRepository;
import be.virtualsushi.wanuus.repositories.TwitterUserRepositoy;
import be.virtualsushi.wanuus.services.TweetProcessService;

@Service("tweetProcessService")
public class TweetProcessServiceImpl implements TweetProcessService {

	private static final Logger log = LoggerFactory.getLogger(TweetProcessServiceImpl.class);

	private class ProcessTweetCallable implements Callable<Tweet> {

		private final TwitterUser user;
		private final Status status;

		public ProcessTweetCallable(TwitterUser user, Status status) {
			this.user = user;
			this.status = status;
		}

		@Override
		public Tweet call() throws Exception {
			return processStatus(user, status, false);
		}

	}

	@Autowired
	private Twitter twitter;

	@Autowired
	private TweetRepository tweetRepository;

	@Autowired
	private TwitterUserRepositoy twitterUserRepositoy;

	@Autowired
	private TweetObjectRepository tweetObjectRepository;

	@Autowired
	private ShortUrlsProcessor shortUrlsProcessor;

	@Override
	public Tweet processStatus(TwitterUser user, Status status, boolean saveAfterProcess) {
		if (status.isRetweet()) {
			status = status.getRetweetedStatus();
		}
		Tweet tweet = tweetRepository.findOne(status.getId());
		if (tweet == null) {
			TwitterUser realAuthor = twitterUserRepositoy.findOne(status.getUser().getId());
			tweet = Tweet.fromStatus(status, realAuthor == null ? user : realAuthor);
			tweet.addObjects(processTweetObjects(status, saveAfterProcess));
		} else if (TweetStates.TOP_RATED.equals(tweet.getState())) {
			return tweet;
		}
		tweet.setState(TweetStates.NOT_RATED);
		tweet.increaseQuantity(1);
		if (saveAfterProcess) {
			return tweetRepository.save(tweet);
		}
		return tweet;
	}

	private Set<TweetObject> processTweetObjects(Status status, boolean saveAfterProcess) {
		HashSet<TweetObject> result = new HashSet<TweetObject>();
		for (HashtagEntity hashtag : status.getHashtagEntities()) {
			result.add(processTweetObject(hashtag.getText(), TweetObjectTypes.HASHTAG));
		}
		for (URLEntity url : status.getURLEntities()) {
			result.add(processTweetObject(url.getExpandedURL(), TweetObjectTypes.URL));
		}
		for (MediaEntity media : status.getMediaEntities()) {
			result.add(processTweetObject(media.getMediaURL(), TweetObjectTypes.IMAGE));
		}
		return result;
	}

	private TweetObject processTweetObject(String value, TweetObjectTypes type) {
		if (TweetObjectTypes.URL.equals(type)) {
			value = shortUrlsProcessor.getRealUrl(value);
		}
		TweetObject object = tweetObjectRepository.findByValueAndType(value, type);
		if (object == null) {
			object = new TweetObject();
			object.setValue(value);
			object.setType(type);
		}
		object.increaseQuantity(1);
		return object;
	}

	@Async
	@Override
	public Future<List<Tweet>> importUserTimeline(TwitterUser user) throws TwitterException {
		ResponseList<Status> timeline = twitter.getUserTimeline(user.getId());
		ExecutorService executor = Executors.newFixedThreadPool(timeline.size());
		List<Future<Tweet>> futureTweets = new ArrayList<Future<Tweet>>();
		for (Status status : timeline) {
			futureTweets.add(executor.submit(new ProcessTweetCallable(user, status)));
		}
		List<Tweet> tweets = new ArrayList<Tweet>();
		for (Future<Tweet> futureTweet : futureTweets) {
			try {
				tweets.add(futureTweet.get());
			} catch (Exception e) {
				log.error("Error processing imported tweet.", e);
			}
		}
		return new AsyncResult<List<Tweet>>(tweets);
	}

	@Override
	public void deleteTweet(Long tweetId) {
		tweetRepository.delete(tweetId);
	}

}
