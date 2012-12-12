package be.virtualsushi.wanuus.services.chain;

import be.virtualsushi.wanuus.model.Tweet;
import be.virtualsushi.wanuus.services.GoogleSearchService;

public class ProcessTweetChainHashtagElement extends AbstractProcessChainElement<Tweet> {

	private GoogleSearchService googleSearchService;

	public ProcessTweetChainHashtagElement(GoogleSearchService googleSearchService) {
		this.googleSearchService = googleSearchService;
	}

	@Override
	protected boolean canProcess(Tweet object) {
		return true;
	}

	@Override
	protected String doProcess(Tweet object) {
		return googleSearchService.searchForImage(object.getHashTags());
	}
}
