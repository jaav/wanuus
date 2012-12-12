package be.virtualsushi.wanuus.services.chain;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import be.virtualsushi.wanuus.components.ImageDownloader;
import be.virtualsushi.wanuus.model.Tweet;
import be.virtualsushi.wanuus.services.GoogleSearchService;

public class ProcessTweetChainUrlElement extends AbstractProcessChainElement<Tweet> {

	private ProcessChainElement<Document, String> processUrlChain;

	private Document document;

	public ProcessTweetChainUrlElement(ImageDownloader imageDownloader, GoogleSearchService googleSearchService) {
		processUrlChain = new ProcessUrlChainOpenTagElement();
		processUrlChain.setNext(new ProcessUrlChainFacebookElement()).setNext(new ProcessUrlChainSchemaOrgElement()).setNext(new ProcessUrlChainCustomElement(imageDownloader, googleSearchService));
	}

	@Override
	protected boolean canProcess(Tweet object) {
		boolean result = object.hasUrl();
		if (result) {
			try {
				document = Jsoup.connect(object.getFirstUrlObject().getValue()).get();
			} catch (IOException e) {
				return false;
			}
		}
		return result;
	}

	@Override
	protected String doProcess(Tweet object) {
		return processUrlChain.process(document);
	}

}
