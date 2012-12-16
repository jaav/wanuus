package be.virtualsushi.wanuus.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import be.virtualsushi.wanuus.model.Tweet;
import be.virtualsushi.wanuus.model.TweetObject;
import be.virtualsushi.wanuus.model.TweetStates;
import be.virtualsushi.wanuus.repositories.TweetRepository;
import be.virtualsushi.wanuus.services.DataAnalysisService;
import be.virtualsushi.wanuus.services.ImageProcessService;

@Service("dataAnalysisService")
public class DataAnalysisServiceImpl implements DataAnalysisService {

	private static final String IMAGE_FILE_POST_FIELD_NAME_PATTERN = "tweetImg_%d";
	private static final String TWEETS_LIST_POST_FIELD_NAME = "jsonTweet";

	private static final Logger log = LoggerFactory.getLogger(DataAnalysisServiceImpl.class);

	@Autowired
	private TweetRepository tweetRepository;

	@Autowired
	private ImageProcessService imageProcessService;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${gae.postUrl}")
	private String postUrl;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void analyseTweets() {
		try {
			List<Tweet> tweets = tweetRepository.getNotRatedTweets();
			log.debug("Rating tweets. Count: " + tweets.size());
			rateTweets(tweets);
			log.debug("Processing top rated tweets.");
			processTopRatedTweets(tweetRepository.getTopRatedTweets(new PageRequest(0, 5, Direction.DESC, "rate")).getContent());
		} catch (Exception e) {
			log.error("Error analysing tweets.", e);
		}
	}

	private void processTopRatedTweets(List<Tweet> topRatedTweets) throws InterruptedException, ExecutionException, JsonGenerationException, JsonMappingException, IOException {
		List<Future<File>> futureImageFiles = new ArrayList<Future<File>>(topRatedTweets.size());
		for (Tweet tweet : topRatedTweets) {
			tweet.setState(TweetStates.TOP_RATED);
			futureImageFiles.add(imageProcessService.createTweetImage(tweetRepository.save(tweet)));
		}
		MultiValueMap<String, Object> postValues = new LinkedMultiValueMap<String, Object>();
		for (int i = 0; i < futureImageFiles.size(); i++) {
			File imageFile = futureImageFiles.get(i).get();
			topRatedTweets.get(i).setImage(imageFile.getName());
			postValues.add(String.format(IMAGE_FILE_POST_FIELD_NAME_PATTERN, i), new FileSystemResource(imageFile));
		}
		postValues.add(TWEETS_LIST_POST_FIELD_NAME, objectMapper.writeValueAsString(topRatedTweets));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("multipart", "form-data"));
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(postValues, headers);
		restTemplate.exchange(postUrl, HttpMethod.POST, request, null);
	}

	private void rateTweets(List<Tweet> tweets) {
		for (Tweet tweet : tweets) {
			int rate = tweet.getRawRate();
			for (TweetObject object : tweet.getObjects()) {
				rate += object.getQuantity() * object.getQuantityFactor();
			}
			tweet.setRate(rate);
			tweet.setState(TweetStates.RATED);
		}
		tweetRepository.save(tweets);
	}

}
