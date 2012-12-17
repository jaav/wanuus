package be.virtualsushi.wanuus.services;

import java.util.List;
import java.util.concurrent.Future;

import twitter4j.Status;
import twitter4j.TwitterException;
import be.virtualsushi.wanuus.model.Tweet;
import be.virtualsushi.wanuus.model.TwitterUser;

public interface TweetProcessService {

	Tweet processStatus(TwitterUser user, Status status, boolean saveAfterProcess);

	Future<List<Tweet>> importUserTimeline(TwitterUser user) throws TwitterException;

	void deleteTweet(Long tweetId);

}
