package be.virtualsushi.wanuus.components;

/**
 * Search for images(for now) with Google's Custom Seach API. Limit: 100
 * query/day. More is for money only.
 * 
 * @author spv
 * 
 */
public interface GoogleSearchApiAccessor {

	public static final String GOOGLE_SEARCH_URL_PATTERN = "https://www.googleapis.com/customsearch/v1?key={apiKey}&cx=017576662512468239146:omuauf_lfve&q={query}";

	public static final String GOOGLE_IMAGE_SEARCH_URL_PATTERN = GOOGLE_SEARCH_URL_PATTERN + "&imgSize=large&searchType=image";

	String seachForImage(String keyWord);

}
