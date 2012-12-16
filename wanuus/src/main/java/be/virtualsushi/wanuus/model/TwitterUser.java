package be.virtualsushi.wanuus.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import twitter4j.User;

@Entity
public class TwitterUser extends CustomIdBaseEntity {

	private static final long serialVersionUID = 3549168563451689799L;

	@JsonProperty("author")
	@Column(name = "NAME")
	private String name;

	@JsonIgnore
	@Column(name = "SCREEN_NAME")
	private String screenName;

	@JsonIgnore
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "user")
	private List<Tweet> tweets;

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

	public static TwitterUser fromUser(User user) {
		TwitterUser result = new TwitterUser();
		result.setId(user.getId());
		result.setScreenName(user.getScreenName());
		result.setName(user.getName());
		return result;
	}

}
