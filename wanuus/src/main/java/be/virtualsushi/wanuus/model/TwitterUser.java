package be.virtualsushi.wanuus.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class TwitterUser extends BaseEntity {

	private static final long serialVersionUID = 3549168563451689799L;

	@Column(name = "TWITTER_ID")
	private long twitterId;

	@Column(name = "NAME")
	private String name;

	@Column(name = "SCREEN_NAME")
	private String screenName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public long getTwitterId() {
		return twitterId;
	}

	public void setTwitterId(long twitterId) {
		this.twitterId = twitterId;
	}

}
