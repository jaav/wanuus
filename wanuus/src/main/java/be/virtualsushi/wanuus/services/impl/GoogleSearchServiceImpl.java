package be.virtualsushi.wanuus.services.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import be.virtualsushi.wanuus.model.GoogleImageSearchResult;
import be.virtualsushi.wanuus.model.GoogleSearchCachedResult;
import be.virtualsushi.wanuus.repositories.GoogleSearchCachedResultRepository;
import be.virtualsushi.wanuus.services.GoogleSearchService;

@Service("googleSearchService")
public class GoogleSearchServiceImpl implements GoogleSearchService {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${google.search.apiKey}")
	private String apiKey;

	@Value("${google.search.engineId}")
	private String engineId;

	@Autowired
	private GoogleSearchCachedResultRepository googleSearchCachedResultRepository;

	@Override
	public String searchForImage(String... keyWords) {
		String query = "";
		for (String word : keyWords) {
			query += word + "+";
		}
		query = query.substring(0, query.length() - 1);
		List<GoogleSearchCachedResult> result = googleSearchCachedResultRepository.findByQuery(query);
		if (CollectionUtils.isNotEmpty(result)) {
			return result.get(0).getResultLink();
		}
		for (String word : keyWords) {
			result = googleSearchCachedResultRepository.findByQuery(word);
			if (CollectionUtils.isNotEmpty(result)) {
				return result.get(0).getResultLink();
			}
		}
		ResponseEntity<GoogleImageSearchResult> response = restTemplate.exchange(GOOGLE_IMAGE_SEARCH_URL_PATTERN, HttpMethod.GET, null, GoogleImageSearchResult.class, apiKey, engineId, query);
		String resultLink = response.getBody().getItems().get(0).getLink();
		GoogleSearchCachedResult cachedResult = new GoogleSearchCachedResult();
		cachedResult.setQueryString(query);
		cachedResult.setResultLink(resultLink);
		googleSearchCachedResultRepository.save(cachedResult);
		return resultLink;
	}

	@Override
	public String searchForImage(List<String> keyWords) {
		return searchForImage(keyWords.toArray(new String[keyWords.size()]));
	}

	@Override
	public String searchForImage(String keyPhrase) {
		return searchForImage(keyPhrase.split(" "));
	}

}
