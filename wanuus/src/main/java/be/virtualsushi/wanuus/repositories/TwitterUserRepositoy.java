package be.virtualsushi.wanuus.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import be.virtualsushi.wanuus.model.TwitterUser;

@Repository
public interface TwitterUserRepositoy extends WanuusRepository<TwitterUser> {

	@Query("from TwitterUser")
	public List<TwitterUser> findAll();

}
