package be.virtualsushi.wanuus.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import be.virtualsushi.wanuus.model.Tweet;

@Repository
public interface TweetRepository extends CrudRepository<Tweet, Long> {

	@Query("from Tweet where state=be.virtualsushi.wanuus.model.TweetStates.NOT_RATED")
	List<Tweet> getNotRatedTweets();

	@Query(value = "from Tweet where state=be.virtualsushi.wanuus.model.TweetStates.RATED", countQuery = "select count(id) from Tweet where state=be.virtualsushi.wanuus.model.TweetStates.RATED")
	Page<Tweet> getTopRatedTweets(Pageable pageable);

}
