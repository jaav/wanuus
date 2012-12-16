package be.virtualsushi.wanuus.services.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
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

	@Autowired
	private TaskExecutor taskExecutor;

	private Map<Long, Integer> processingTweetsQuantities = Collections.synchronizedMap(new HashMap<Long, Integer>());

	@Async
	@Override
	public void processTweet(TwitterUser user, Status status) {
		if (status.isRetweet()) {
			status = status.getRetweetedStatus();
		}
		if (processingTweetsQuantities.containsKey(status.getId())) {
			Integer quantity = processingTweetsQuantities.get(status.getId());
			processingTweetsQuantities.put(status.getId(), quantity + 1);
		} else {
			processingTweetsQuantities.put(status.getId(), 1);
			Tweet tweet = tweetRepository.findOne(status.getId());
			if (tweet == null) {
				TwitterUser realAuthor = twitterUserRepositoy.findOne(status.getUser().getId());
				tweet = Tweet.fromStatus(status, realAuthor == null ? user : realAuthor);
				tweet.addObjects(processTweetObjects(status));
			} else if (TweetStates.TOP_RATED.equals(tweet.getState())) {
				return;
			}
			tweet.setState(TweetStates.NOT_RATED);
			tweet.increaseQuantity(processingTweetsQuantities.get(status.getId()));
			tweetRepository.save(tweet);
			processingTweetsQuantities.remove(status.getId());
		}
	}

	private Set<TweetObject> processTweetObjects(Status status) {
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
		return tweetObjectRepository.save(object);
	}

	@Async
	@Override
	public void processTweets(TwitterUser user, List<Status> statuses) {
		for (Status status : statuses) {
			processTweet(user, status);
		}
		log.info(statuses.size() + " tweets processed for user - " + user.getId());
	}

	@Async
	@Override
	public Future<Integer> processFollowing(TwitterUser user) throws TwitterException {
		ResponseList<Status> timeline = twitter.getUserTimeline(user.getId());
		processTweets(user, timeline);
		return new AsyncResult<Integer>(timeline.size());
	}

	@Override
	public void deleteTweet(Long tweetId) {
		tweetRepository.delete(tweetId);
	}

}
