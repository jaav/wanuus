package be.virtualsushi.wanuus.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class TwitterUser extends BaseEntity {

	private static final long serialVersionUID = 3549168563451689799L;

	@Column(name = "NAME")
	private String name;

	@Column(name = "SCREEN_NAME")
	private String screenName;

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

}
