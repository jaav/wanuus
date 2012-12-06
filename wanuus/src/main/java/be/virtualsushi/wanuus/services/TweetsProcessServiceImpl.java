package be.virtualsushi.wanuus.services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import be.virtualsushi.wanuus.model.TwitterUser;
import be.virtualsushi.wanuus.repositories.TweetRepository;
import be.virtualsushi.wanuus.repositories.TwitterUserRepositoy;

@Service("tweetsProcessService")
public class TweetsProcessServiceImpl implements TweetsProcessService {

	@Autowired
	private TweetRepository tweetRepository;

	@Autowired
	private TwitterUserRepositoy twitterUserRepository;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private Twitter twitter;

	@Autowired
	private WanuusStatusListener wanuusStatusListener;

	@Value("${twitter.listOwnerName}")
	private String listOwnerName;

	@Value("${twitter.listSlug}")
	private String listSlug;

	@PostConstruct
	@Override
	public void loadListMembers() throws TwitterException {
		long cursor = -1;
		PagableResponseList<User> members = twitter.getUserListMembers(listOwnerName, listSlug, cursor);
		List<Long> existingUserIds = twitterUserRepository.getExistingUserIds();
		while (!members.isEmpty()) {
			readListMembers(members, existingUserIds);
			cursor = members.getNextCursor();
			members = twitter.getUserListMembers(listOwnerName, listSlug, cursor);
		}
		wanuusStatusListener.listen(existingUserIds);
	}

	private void readListMembers(PagableResponseList<User> members, List<Long> existingUserIds) {
		List<TwitterUser> users = new ArrayList<TwitterUser>();
		for (User member : members) {
			if (!existingUserIds.contains(member.getId())) {
				TwitterUser user = new TwitterUser();
				user.setTwitterId(member.getId());
				user.setScreenName(member.getScreenName());
				user.setName(member.getName());
				users.add(user);
				existingUserIds.add(user.getTwitterId());
			}
		}
		twitterUserRepository.save(users);
	}

	@Override
	@Scheduled(fixedRate = 15 * 60 * 1000)
	public void processTweets() {
		System.out.println("perocessing tweets");
	}
}
