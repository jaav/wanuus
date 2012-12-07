package be.virtualsushi.wanuus.services;

import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;
import be.virtualsushi.wanuus.model.TwitterUser;

public interface TweetProcessService {

	void processTweet(TwitterUser user, Status status);

	void processTweets(TwitterUser user, List<Status> statuses);

	void processFollowList(List<Long> followList) throws TwitterException;

}
