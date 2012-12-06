package be.virtualsushi.wanuus.services;

import twitter4j.TwitterException;

public interface TweetsProcessService {

	void processTweets();

	void loadListMembers() throws TwitterException;

}
