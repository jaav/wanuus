package be.virtualsushi.wanuus.services;

import java.util.concurrent.ExecutionException;

import twitter4j.TwitterException;

public interface DataCreationService {

	void createListData() throws TwitterException, InterruptedException, ExecutionException;

}
