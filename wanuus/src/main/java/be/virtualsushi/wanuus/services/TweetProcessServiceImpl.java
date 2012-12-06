package be.virtualsushi.wanuus.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import be.virtualsushi.wanuus.model.Tweet;
import be.virtualsushi.wanuus.model.TwitterUser;
import be.virtualsushi.wanuus.repositories.TweetRepository;
import be.virtualsushi.wanuus.repositories.TwitterUserRepositoy;

@Service("tweetProcessService")
public class TweetProcessServiceImpl implements TweetProcessService {

	private static final int STATUSES_FETCH_STEP = 1000;

	@Autowired
	private Twitter twitter;

	@Autowired
	private TweetRepository tweetRepository;

	@Autowired
	private TwitterUserRepositoy twitterUserRepositoy;

	@Async
	@Override
	public void processTweet(TwitterUser user, Status status, boolean isFromBatchCreate) {
		if (!isFromBatchCreate || tweetRepository.findOne(status.getId()) == null) {
			Tweet tweet = Tweet.fromStatus(status);
			tweet.setUser(user);
			tweetRepository.save(tweet);
		}
	}

	@Async
	@Override
	public void processTweets(TwitterUser user, List<Status> statuses) {
		for (Status status : statuses) {
			processTweet(user, status, true);
		}
	}

	@Async
	@Override
	public void processFollowList(List<Long> followList) throws TwitterException {
		for (Long followId : followList) {
			fetchUserTweets(followId);
		}
	}

	private void fetchUserTweets(Long followId) throws TwitterException {
		TwitterUser user = twitterUserRepositoy.findOne(followId);
		int pageNumber = 1;
		Paging paging = new Paging(pageNumber, STATUSES_FETCH_STEP);
		ResponseList<Status> statuses = null;
		do {
			paging.setPage(pageNumber);
			statuses = twitter.getUserTimeline(followId, paging);
			processTweets(user, statuses);
			pageNumber++;
		} while (statuses.size() == STATUSES_FETCH_STEP);
	}
}
