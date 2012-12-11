package be.virtualsushi.wanuus.components.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import be.virtualsushi.wanuus.components.GoogleSearchApiAccessor;

public class GoogleSearchApiAccessorImpl implements GoogleSearchApiAccessor {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${google.search.apiKey}")
	private String apiKey;

	@Override
	public String seachForImage(String keyWord) {
		restTemplate.exchange(GOOGLE_IMAGE_SEARCH_URL_PATTERN, HttpMethod.GET, null, null, apiKey, keyWord);
		return null;
	}

}
