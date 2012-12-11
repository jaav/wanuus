package be.virtualsushi.wanuus.services.impl;

import java.io.File;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import be.virtualsushi.wanuus.components.ImageDownloader;
import be.virtualsushi.wanuus.components.ImageFilter;
import be.virtualsushi.wanuus.model.Tweet;
import be.virtualsushi.wanuus.services.ImageProcessService;

@Service("imageProcessService")
public class ImageProcessServiceImpl implements ImageProcessService {

	private static final Logger log = LoggerFactory.getLogger(ImageProcessServiceImpl.class);

	@Autowired
	private ImageFilter imageFilter;

	@Autowired
	private ImageDownloader imageDownloader;

	@Async
	@Override
	public Future<File> createTweetImage(Tweet tweet) {
		File result = null;
		try {
			if (tweet.hasImage()) {
				result = imageDownloader.downloadImage(tweet.getFirstImageObject().getValue());
			} else if (tweet.hasUrl()) {

			} else {

			}
			imageFilter.applyFilter(result);
		} catch (Exception e) {
			log.error("Error processing image. Tweet id - " + tweet.getId(), e);
		}
		return new AsyncResult<File>(result);
	}

}
