package be.virtualsushi.wanuus.services;

import java.io.File;
import java.util.concurrent.Future;

import be.virtualsushi.wanuus.model.Tweet;

public interface ImageProcessService {

	public Future<File> createTweetImage(Tweet tweet);

}
