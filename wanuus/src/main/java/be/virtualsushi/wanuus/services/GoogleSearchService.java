package be.virtualsushi.wanuus.services;

import java.util.List;

/**
 * Search for images(for now) with Google's Custom Seach API. Limit: 100
 * query/day. More is for money only.
 * 
 * @author spv
 * 
 */
public interface GoogleSearchService {

	public static final String GOOGLE_SEARCH_URL_PATTERN = "https://www.googleapis.com/customsearch/v1?key={apiKey}&cx={engineId}&q={query}";

	public static final String GOOGLE_IMAGE_SEARCH_URL_PATTERN = GOOGLE_SEARCH_URL_PATTERN + "&imgSize=xlarge&searchType=image&fields=items/link";

	String searchForImage(String... keyWords);

	String searchForImage(List<String> keyWords);

	String searchForImage(String keyPhrase);

}
