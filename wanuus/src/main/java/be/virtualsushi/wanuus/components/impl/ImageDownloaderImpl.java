package be.virtualsushi.wanuus.components.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import be.virtualsushi.wanuus.components.ImageDownloader;

@Component("imageDownloader")
public class ImageDownloaderImpl implements ImageDownloader {

	private static final Logger log = LoggerFactory.getLogger(ImageDownloaderImpl.class);

	@Autowired
	private HttpClient httpClient;

	@Value("#{systemProperties.getProperty('user.home')}")
	private String homeDirectoryName;

	@Value("${storage.directory.name}")
	private String storageDirectoryName;

	private File storageRoot;

	@PostConstruct
	public void initImageDirectory() {
		storageRoot = new File(homeDirectoryName, storageDirectoryName);
		storageRoot.mkdirs();
	}

	@Override
	public File downloadImage(String url) {
		File result = null;
		try {
			HttpEntity responseEntity = httpClient.execute(new HttpGet(url)).getEntity();
			String fileName = UUID.randomUUID().toString().replaceAll("-", "");
			String fileExtension = ".jpg";
			if ("image/png".equals(responseEntity.getContentType())) {
				fileExtension = ".png";
			}
			result = new File(storageRoot, fileName + fileExtension);
			InputStream input = responseEntity.getContent();
			FileOutputStream out = new FileOutputStream(result);
			IOUtils.copy(input, out);
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(out);
			EntityUtils.consume(responseEntity);
		} catch (Exception e) {
			log.error("Error downloading file. url - " + url, e);
		}
		return result;
	}
}
