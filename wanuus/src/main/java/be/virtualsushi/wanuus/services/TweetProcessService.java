package be.virtualsushi.wanuus.services;

import java.util.List;
import java.util.concurrent.Future;

import twitter4j.Status;
import twitter4j.TwitterException;
import be.virtualsushi.wanuus.model.TwitterUser;

public interface TweetProcessService {

	void processTweet(TwitterUser user, Status status);

	void processTweets(TwitterUser user, List<Status> statuses);

	Future<Integer> processFollowing(TwitterUser user) throws TwitterException;

	void deleteTweet(Long tweetId);

}
