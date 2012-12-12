package be.virtualsushi.wanuus.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import be.virtualsushi.wanuus.components.WanuusStatusListener;
import be.virtualsushi.wanuus.model.TwitterUser;
import be.virtualsushi.wanuus.repositories.TweetRepository;
import be.virtualsushi.wanuus.repositories.TwitterUserRepositoy;
import be.virtualsushi.wanuus.services.DataCreationService;
import be.virtualsushi.wanuus.services.TweetProcessService;

@Service("dateCreationService")
public class DataCreationServiceImpl implements DataCreationService {

	@Autowired
	private TweetRepository tweetRepository;

	@Autowired
	private TwitterUserRepositoy twitterUserRepository;

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
		long cursor = -1;
		List<Long> existingUserIds = twitterUserRepository.getExistingUserIds();
		getListMembers(cursor, existingUserIds);
		tweetProcessService.processFollowList(existingUserIds);
		wanuusStatusListener.listen(existingUserIds);
	}

	private void getListMembers(long cursor, List<Long> existingUserIds) throws TwitterException {
		PagableResponseList<User> members = twitter.getUserListMembers(listOwnerName, listSlug, cursor);
		while (!members.isEmpty()) {
			readListMembers(members, existingUserIds);
			cursor = members.getNextCursor();
			members = twitter.getUserListMembers(listOwnerName, listSlug, cursor);
		}
	}

	private void readListMembers(PagableResponseList<User> members, List<Long> existingUserIds) {
		List<TwitterUser> users = new ArrayList<TwitterUser>();
		for (User member : members) {
			if (!existingUserIds.contains(member.getId())) {
				TwitterUser user = new TwitterUser();
				user.setId(member.getId());
				user.setScreenName(member.getScreenName());
				user.setName(member.getName());
				users.add(user);
				existingUserIds.add(user.getId());
			}
		}
		twitterUserRepository.save(users);
	}

}
