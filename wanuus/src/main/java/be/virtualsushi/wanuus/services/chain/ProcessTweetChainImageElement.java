package be.virtualsushi.wanuus.services.chain;

import be.virtualsushi.wanuus.model.Tweet;

public class ProcessTweetChainImageElement extends AbstractProcessChainElement<Tweet> {

	@Override
	protected boolean canProcess(Tweet object) {
		return object.hasImage();
	}

	@Override
	protected String doProcess(Tweet object) {
		return object.getFirstImageObject().getValue();
	}

}
