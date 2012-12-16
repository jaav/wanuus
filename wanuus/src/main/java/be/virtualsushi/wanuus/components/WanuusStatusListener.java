package be.virtualsushi.wanuus.components;

import java.util.List;

import be.virtualsushi.wanuus.model.TwitterUser;

public interface WanuusStatusListener {

	void listen(List<TwitterUser> followings);

}
