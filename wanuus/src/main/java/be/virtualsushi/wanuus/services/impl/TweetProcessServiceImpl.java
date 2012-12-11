package be.virtualsushi.wanuus.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
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

	@Async
	@Override
	public void processTweet(TwitterUser user, Status status) {
		if (status.isRetweet()) {
			processTweet(user, status.getRetweetedStatus());
			return;
		}
		Tweet tweet = tweetRepository.findOne(status.getId());
		if (tweet == null) {
			tweet = Tweet.fromStatus(status, user);
		} else if (TweetStates.TOP_RATED.equals(tweet.getState())) {
			return;
		}
		tweet.setState(TweetStates.NOT_RATED);
		tweet.increaseQuantity(1);
		processTweetObjects(tweet, status);
		tweetRepository.save(tweet);
	}

	private void processTweetObjects(Tweet tweet, Status status) {
		for (HashtagEntity hashtag : status.getHashtagEntities()) {
			tweet.addObject(processTweetObject(hashtag.getText(), TweetObjectTypes.HASHTAG));
		}
		for (URLEntity url : status.getURLEntities()) {
			tweet.addObject(processTweetObject(url.getExpandedURL(), TweetObjectTypes.URL));
		}
		for (MediaEntity media : status.getMediaEntities()) {
			tweet.addObject(processTweetObject(media.getMediaURL(), TweetObjectTypes.IMAGE));
		}
	}

	private TweetObject processTweetObject(String value, TweetObjectTypes type) {
		TweetObject object = tweetObjectRepository.findByValueAndType(value, type);
		if (object == null) {
			object = new TweetObject();
			object.setId(System.currentTimeMillis());
			if (TweetObjectTypes.URL.equals(type)) {
				object.setValue(shortUrlsProcessor.getRealUrl(value));
			} else {
				object.setValue(value);
			}
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
	}

	@Async
	@Override
	public void processFollowList(List<Long> followList) throws TwitterException {
		for (Long followId : followList) {
			processTweets(twitterUserRepositoy.findOne(followId), twitter.getUserTimeline(followId));
		}
	}

}
