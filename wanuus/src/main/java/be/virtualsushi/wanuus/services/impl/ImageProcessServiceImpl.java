package be.virtualsushi.wanuus.services.impl;

import java.io.File;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import be.virtualsushi.wanuus.components.ImageDownloader;
import be.virtualsushi.wanuus.components.ImageFilter;
import be.virtualsushi.wanuus.model.Tweet;
import be.virtualsushi.wanuus.services.GoogleSearchService;
import be.virtualsushi.wanuus.services.ImageProcessService;
import be.virtualsushi.wanuus.services.chain.ProcessChainElement;
import be.virtualsushi.wanuus.services.chain.ProcessTweetChainHashtagElement;
import be.virtualsushi.wanuus.services.chain.ProcessTweetChainImageElement;
import be.virtualsushi.wanuus.services.chain.ProcessTweetChainUrlElement;

@Service("imageProcessService")
public class ImageProcessServiceImpl implements ImageProcessService {

	private static final Logger log = LoggerFactory.getLogger(ImageProcessServiceImpl.class);

	@Autowired
	private ImageFilter imageFilter;

	@Autowired
	private ImageDownloader imageDownloader;

	@Autowired
	private GoogleSearchService googleSearchService;

	private ProcessChainElement<Tweet, String> processTweetChain;

	@PostConstruct
	public void initProcessChain() {
		processTweetChain = new ProcessTweetChainImageElement();
		processTweetChain.setNext(new ProcessTweetChainUrlElement(imageDownloader, googleSearchService)).setNext(new ProcessTweetChainHashtagElement(googleSearchService));
	}

	@Async
	@Override
	public Future<File> createTweetImage(Tweet tweet) {
		File result = null;
		try {
			result = imageDownloader.downloadImage(processTweetChain.process(tweet));
			imageFilter.applyFilter(result);
		} catch (Exception e) {
			log.error("Error processing image. Tweet id - " + tweet.getId(), e);
		}
		return new AsyncResult<File>(result);
	}
}
