package be.virtualsushi.wanuus.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import twitter4j.Status;

@Entity
public class Tweet extends BaseEntity implements HasQuantity {

	private static final long serialVersionUID = -5452953243879914216L;

	@ManyToOne
	@JoinColumn(name = "USER_ID")
	private TwitterUser user;

	@Column(name = "TWEET_TEXT")
	private String text;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "TWEET_OBJECT", joinColumns = @JoinColumn(name = "TWEET_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "OBJECT_ID", referencedColumnName = "ID"))
	private Set<TweetObject> objects;

	@Column(name = "QUANTITY")
	private int quantity;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public TwitterUser getUser() {
		return user;
	}

	public void setUser(TwitterUser user) {
		this.user = user;
	}

	@Override
	public void increaseQuantity(int amount) {
		quantity += amount;
	}

	@Override
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void addObject(TweetObject object) {
		if (objects == null) {
			objects = new HashSet<TweetObject>();
		}
		objects.add(object);
	}

	public Set<TweetObject> getObjects() {
		return objects;
	}

	public void setObjects(Set<TweetObject> objects) {
		this.objects = objects;
	}

	public static Tweet fromStatus(Status status, TwitterUser user) {
		Tweet tweet = new Tweet();
		tweet.setId(status.getId());
		tweet.setText(status.getText());
		tweet.setUser(user);
		return tweet;
	}

}
