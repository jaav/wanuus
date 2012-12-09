package be.virtualsushi.wanuus.services;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("shortUrlsProcessor")
public class ShortUrlsProcessorImpl implements ShortUrlsProcessor {

	private static final Logger log = LoggerFactory.getLogger(ShortUrlsProcessorImpl.class);

	@Autowired
	private HttpClient httpClient;

	@Override
	public String getRealUrl(String shortUrl) {
		try {
			HttpResponse response = httpClient.execute(new HttpGet(shortUrl));
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY) {
				return response.getFirstHeader(HttpHeaders.LOCATION).getValue();
			}
		} catch (Exception e) {
			log.error("Error expanding url.", e);
		}
		return shortUrl;
	}
}
