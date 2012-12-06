package be.virtualsushi.wanuus.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;

@Entity
public class Tweet extends BaseEntity {

	private static final long serialVersionUID = -5452953243879914216L;

	@ManyToOne
	@JoinColumn(name = "USER_ID")
	private TwitterUser user;

	@Column(name = "TWEET_TEXT")
	private String text;

	@Column(name = "IS_RETWEET")
	private boolean isRetweet;

	@ElementCollection
	@JoinTable(name = "TWEET_HASHTAGS", joinColumns = { @JoinColumn(name = "TWEET_ID") })
	@Column(name = "HASHTAG")
	private Set<String> hashtags;

	@ElementCollection
	@JoinTable(name = "TWEET_URLS", joinColumns = { @JoinColumn(name = "TWEET_ID") })
	@Column(name = "URL")
	private Set<String> urls;

	@ElementCollection
	@JoinTable(name = "TWEET_IMAGES", joinColumns = { @JoinColumn(name = "TWEET_ID") })
	@Column(name = "IMAGE")
	private Set<String> images;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isRetweet() {
		return isRetweet;
	}

	public void setRetweet(boolean isRetweet) {
		this.isRetweet = isRetweet;
	}

	public Set<String> getHashtags() {
		return hashtags;
	}

	public void setHashtags(Set<String> hashtags) {
		this.hashtags = hashtags;
	}

	public Set<String> getUrls() {
		return urls;
	}

	public void setUrls(Set<String> urls) {
		this.urls = urls;
	}

	public Set<String> getImages() {
		return images;
	}

	public void setImages(Set<String> images) {
		this.images = images;
	}

	public void addHashTag(String hashtag) {
		if (hashtags == null) {
			hashtags = new HashSet<String>();
		}
		hashtags.add(hashtag);
	}

	public void addUrl(String url) {
		if (urls == null) {
			urls = new HashSet<String>();
		}
		urls.add(url);
	}

	public void addImage(String image) {
		if (images == null) {
			images = new HashSet<String>();
		}
		images.add(image);
	}

	public TwitterUser getUser() {
		return user;
	}

	public void setUser(TwitterUser user) {
		this.user = user;
	}

	public static Tweet fromStatus(Status status) {
		Tweet tweet = new Tweet();
		tweet.setId(status.getId());
		tweet.setText(status.getText());
		tweet.setRetweet(status.isRetweet());
		for (HashtagEntity hashtag : status.getHashtagEntities()) {
			tweet.addHashTag(hashtag.getText());
		}
		for (URLEntity url : status.getURLEntities()) {
			tweet.addUrl(url.getExpandedURL());
		}
		for (MediaEntity media : status.getMediaEntities()) {
			if (media.getType().equals("photo")) {
				tweet.addImage(media.getExpandedURL());
			}
		}
		return tweet;
	}

}
