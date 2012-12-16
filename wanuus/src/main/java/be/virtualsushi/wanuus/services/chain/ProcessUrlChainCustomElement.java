package be.virtualsushi.wanuus.services.chain;

import java.awt.image.BufferedImage;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import be.virtualsushi.wanuus.components.ImageDownloader;
import be.virtualsushi.wanuus.services.GoogleSearchService;

public class ProcessUrlChainCustomElement extends AbstractProcessChainElement<Document> {

	private static final int DOCUMENT_IMGS_TO_CHECK_COUNT = 5;
	private static final int MIN_IMAGE_SIZE = 200;

	private ImageDownloader imageDownloader;

	private GoogleSearchService googleSearchService;

	public ProcessUrlChainCustomElement(ImageDownloader imageDownloader, GoogleSearchService googleSearchService) {
		this.imageDownloader = imageDownloader;
		this.googleSearchService = googleSearchService;
	}

	@Override
	protected boolean canProcess(Document object) {
		return true;
	}

	@Override
	protected String doProcess(Document object) {
		Elements elements = object.select("img");
		int imgsCount = elements.size() < DOCUMENT_IMGS_TO_CHECK_COUNT ? elements.size() : DOCUMENT_IMGS_TO_CHECK_COUNT;
		for (int i = 0; i < imgsCount; i++) {
			String imageUrl = object.absUrl(elements.get(i).attr("src"));
			BufferedImage image = imageDownloader.downloadImageTemporarily(imageUrl);
			if (image != null && image.getHeight() > MIN_IMAGE_SIZE && image.getWidth() > MIN_IMAGE_SIZE) {
				return imageUrl;
			}
		}
		// No luck with images let's ask google for help.
		elements = object.select("title");
		return googleSearchService.searchForImage(elements.get(0).ownText());
	}

}
