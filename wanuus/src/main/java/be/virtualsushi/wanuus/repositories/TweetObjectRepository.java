package be.virtualsushi.wanuus.repositories;

import org.springframework.stereotype.Repository;

import be.virtualsushi.wanuus.model.TweetObject;
import be.virtualsushi.wanuus.model.TweetObjectTypes;

@Repository
public interface TweetObjectRepository extends WanuusRepository<TweetObject> {

	public TweetObject findByValueAndType(String value, TweetObjectTypes type);

}
