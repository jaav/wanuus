package be.virtualsushi.wanuus.services.chain;

import java.awt.image.BufferedImage;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import be.virtualsushi.wanuus.components.ImageDownloader;
import be.virtualsushi.wanuus.services.GoogleSearchService;

public class ProcessUrlChainCustomElement extends AbstractProcessChainElement<Document> {

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
		int imgsCount = elements.size() < 3 ? elements.size() : 3;
		for (int i = 0; i < imgsCount; i++) {
			String imageUrl = elements.get(i).attr("src");
			BufferedImage image = imageDownloader.downloadImageTemporarily(imageUrl);
			if (image.getHeight() > 200 && image.getWidth() > 200) {
				return imageUrl;
			}
		}
		// No luck with images let's ask google for help.
		elements = object.select("title");
		return googleSearchService.searchForImage(elements.get(0).ownText());
	}

}
