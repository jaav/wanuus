package be.virtualsushi.wanuus.components.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import be.virtualsushi.wanuus.components.WanuusStatusListener;
import be.virtualsushi.wanuus.repositories.TwitterUserRepositoy;
import be.virtualsushi.wanuus.services.TweetProcessService;

@Component("twitterUserStatusListener")
public class WanuusStatusListenerImpl implements StatusListener, WanuusStatusListener {

	@Autowired
	private TwitterStream twitterStream;

	@Autowired
	private TweetProcessService tweetProcessService;

	@Autowired
	private TwitterUserRepositoy twitterUserRepositoy;

	@PostConstruct
	public void init() {
		twitterStream.addListener(this);
	}

	@Override
	public void onException(Exception ex) {

	}

	@Override
	public void onStatus(Status status) {
		tweetProcessService.processTweet(twitterUserRepositoy.findOne(status.getUser().getId()), status);
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

	}

	@Override
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

	}

	@Override
	public void onScrubGeo(long userId, long upToStatusId) {

	}

	@Override
	public void onStallWarning(StallWarning warning) {

	}

	@Override
	public void listen(List<Long> followIds) {
		long[] follow = new long[followIds.size()];
		for (int i = 0; i < follow.length; i++) {
			follow[i] = followIds.get(i);
		}
		twitterStream.filter(new FilterQuery(follow));
	}

}
