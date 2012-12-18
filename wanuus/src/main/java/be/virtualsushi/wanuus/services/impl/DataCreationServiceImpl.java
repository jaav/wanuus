package be.virtualsushi.wanuus.services.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import be.virtualsushi.wanuus.components.WanuusStatusListener;
import be.virtualsushi.wanuus.model.Tweet;
import be.virtualsushi.wanuus.model.TweetObject;
import be.virtualsushi.wanuus.model.TwitterUser;
import be.virtualsushi.wanuus.repositories.TweetObjectRepository;
import be.virtualsushi.wanuus.repositories.TweetRepository;
import be.virtualsushi.wanuus.repositories.TwitterUserRepositoy;
import be.virtualsushi.wanuus.services.DataCreationService;
import be.virtualsushi.wanuus.services.TweetProcessService;

@Service("dateCreationService")
public class DataCreationServiceImpl implements DataCreationService {

	private static final Logger log = LoggerFactory.getLogger(DataCreationServiceImpl.class);

	@Autowired
	private TweetRepository tweetRepository;

	@Autowired
	private TwitterUserRepositoy twitterUserRepository;

	@Autowired
	private TweetObjectRepository tweetObjectRepository;

	@Autowired
	private Twitter twitter;

	@Autowired
	private WanuusStatusListener wanuusStatusListener;

	@Autowired
	private TweetProcessService tweetProcessService;

	@Value("${twitter.listOwnerName}")
	private String listOwnerName;

	@Value("${twitter.listSlug}")
	private String listSlug;

	@Override
	public void createListData() throws TwitterException {
		List<TwitterUser> existingUsers = twitterUserRepository.findAll();
		if (existingUsers == null) {
			existingUsers = new ArrayList<TwitterUser>();
		}
		getListMembers(existingUsers);
		wanuusStatusListener.listen(existingUsers);
		/*List<Tweet> importedTweets = importExistingTweets(existingUsers);
		processImportedTweets(importedTweets);*/
		log.info("List import finished.");
	}

	private void processImportedTweets(List<Tweet> importedTweets) {
		for (Tweet tweet : importedTweets) {
			if (tweetRepository.exists(tweet.getId())) {
				tweetRepository.updateTweetQuantity(tweet.getId());
			} else {
				Set<TweetObject> resultObjects = new HashSet<TweetObject>();
				for (TweetObject object : tweet.getObjects()) {
					TweetObject existingObject = tweetObjectRepository.findByValueAndType(object.getValue(), object.getType());
					if (existingObject != null) {
						existingObject.increaseQuantity(1);
						resultObjects.add(existingObject);
					} else {
						resultObjects.add(object);
					}
				}
				tweet.setObjects(resultObjects);
				tweetRepository.save(tweet);
			}
		}
	}

	private List<Tweet> importExistingTweets(List<TwitterUser> existingUsers) throws TwitterException {
		List<Future<List<Tweet>>> importResults = new ArrayList<Future<List<Tweet>>>(existingUsers.size());
		for (TwitterUser user : existingUsers) {
			importResults.add(tweetProcessService.importUserTimeline(user));
		}
		int importedTweetsCount = 0;
		List<Tweet> importedTweets = new ArrayList<Tweet>();
		for (Future<List<Tweet>> importResult : importResults) {
			try {
				List<Tweet> tweetsList = importResult.get();
				importedTweetsCount += tweetsList.size();
				importedTweets.addAll(tweetsList);
			} catch (Exception e) {
				log.error("Error importing tweets for user - " + existingUsers.get(importResults.indexOf(importResult)), e);
			}
		}
		log.info(importedTweetsCount + " tweets imported.");
		return importedTweets;
	}

	private void getListMembers(List<TwitterUser> existingUsers) throws TwitterException {
		long cursor = -1;
		while (cursor != 0) {
			PagableResponseList<User> members = twitter.getUserListMembers(listOwnerName, listSlug, cursor);
			readListMembers(members, existingUsers);
			cursor = members.getNextCursor();
		}
	}

	private void readListMembers(PagableResponseList<User> members, List<TwitterUser> existingUsers) {
		final List<TwitterUser> users = new ArrayList<TwitterUser>();
		for (User member : members) {
			TwitterUser user = TwitterUser.fromUser(member);
			if (!existingUsers.contains(user)) {
				users.add(user);
			}
		}
		CollectionUtils.addAll(existingUsers, twitterUserRepository.save(users).iterator());
		log.info(users.size() + " new members added.");
	}

}
