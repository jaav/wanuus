package be.virtualsushi.wanuus.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import be.virtualsushi.wanuus.BaseWanuusTest;
import be.virtualsushi.wanuus.model.TwitterUser;
import be.virtualsushi.wanuus.repositories.TwitterUserRepositoy;
import be.virtualsushi.wanuus.services.TweetProcessService;

public class MergeTest extends BaseWanuusTest {

	@Autowired
	private TweetProcessService tweetProcessService;

	@Autowired
	private TwitterUserRepositoy twitterUserRepositoy;

	@Autowired
	private Twitter twitter;

	@Test
	public void testTweetProcess() throws TwitterException {
		TwitterUser user = new TwitterUser();
		user.setId(System.currentTimeMillis());
		user.setName("name");
		user.setScreenName("Name name");
		user = twitterUserRepositoy.save(user);
		ResponseList<Status> statuses = twitter.getUserTimeline("mrmrfrag");
		for (Status status : statuses) {
			if ((status.getURLEntities() != null && status.getURLEntities().length > 0) || (status.getHashtagEntities() != null && status.getHashtagEntities().length > 0)
					|| (status.getMediaEntities() != null && status.getMediaEntities().length > 0)) {
				tweetProcessService.processStatus(user, status, true);
				break;
			}
		}
	}
}
