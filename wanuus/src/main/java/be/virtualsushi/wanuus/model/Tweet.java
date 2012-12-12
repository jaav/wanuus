package be.virtualsushi.wanuus.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import twitter4j.Status;

@Entity
public class Tweet implements HasQuantity, Serializable {

	private static final long serialVersionUID = -5452953243879914216L;

	private static final int TWEET_QUANTITY_FACTOR = 2;

	@Id
	@Column(name = "ID")
	private Long id;

	@Version
	@Column(name = "LAST_MODIFIED")
	private Timestamp lastModified;

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

	@Column(name = "TWEET_STATE")
	@Enumerated(EnumType.STRING)
	private TweetStates state;

	@Column(name = "RATE")
	private int rate;

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

	public TweetStates getState() {
		return state;
	}

	public void setState(TweetStates state) {
		this.state = state;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public boolean hasImage() {
		for (TweetObject object : objects) {
			if (TweetObjectTypes.IMAGE.equals(object.getType())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasUrl() {
		for (TweetObject object : objects) {
			if (TweetObjectTypes.URL.equals(object.getType())) {
				return true;
			}
		}
		return false;
	}

	public TweetObject getFirstImageObject() {
		for (TweetObject object : objects) {
			if (TweetObjectTypes.IMAGE.equals(object.getType())) {
				return object;
			}
		}
		return null;
	}

	public TweetObject getFirstUrlObject() {
		for (TweetObject object : objects) {
			if (TweetObjectTypes.URL.equals(object.getType())) {
				return object;
			}
		}
		return null;
	}

	public List<String> getHashTags() {
		List<String> result = new ArrayList<String>();
		for (TweetObject object : objects) {
			if (TweetObjectTypes.HASHTAG.equals(object.getType())) {
				result.add(object.getValue());
			}
		}
		return result;
	}

	public int getRawRate() {
		return getQuantity() * TWEET_QUANTITY_FACTOR;
	}

	public Timestamp getLastModified() {
		return lastModified;
	}

	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tweet other = (Tweet) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
