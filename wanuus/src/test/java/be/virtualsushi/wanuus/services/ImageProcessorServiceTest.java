package be.virtualsushi.wanuus.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import be.virtualsushi.wanuus.BaseWanuusTest;
import be.virtualsushi.wanuus.components.ImageFilter;

public class ImageProcessorServiceTest extends BaseWanuusTest {

	private static final String[] TEST_IMAGES = { "img1.jpg", "img2.png", "img3.jpg" };

	@Autowired
	private ImageFilter imageProcessorService;

	@Test
	public void testImageProcessing() throws IOException {
		for (String imageName : TEST_IMAGES) {
			File imageFile = File.createTempFile("tmp", imageName);
			FileOutputStream outFile = new FileOutputStream(imageFile);
			IOUtils.copyLarge(getClass().getClassLoader().getResourceAsStream(imageName), outFile);
			IOUtils.closeQuietly(outFile);
			imageProcessorService.applyFilter(imageFile);
		}
	}

}
