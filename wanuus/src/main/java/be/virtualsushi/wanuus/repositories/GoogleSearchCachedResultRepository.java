package be.virtualsushi.wanuus.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import be.virtualsushi.wanuus.model.GoogleSearchCachedResult;

@Repository
public interface GoogleSearchCachedResultRepository extends WanuusRepository<GoogleSearchCachedResult> {

	@Query("from GoogleSearchCachedResult where queryString like :query")
	public List<GoogleSearchCachedResult> findByQuery(@Param("query") String query);

}
