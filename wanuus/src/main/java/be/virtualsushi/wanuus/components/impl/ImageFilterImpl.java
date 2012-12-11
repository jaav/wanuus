package be.virtualsushi.wanuus.components.impl;

import java.io.File;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.Info;
import org.im4java.core.InfoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import be.virtualsushi.wanuus.components.ImageFilter;

/**
 * Creates instagram like photo: 612x612
 * 
 * @author spv
 * 
 */
@Component("imageFilter")
public class ImageFilterImpl implements ImageFilter {

	private static final Logger log = LoggerFactory.getLogger(ImageFilterImpl.class);

	private static final int IMAGE_SIZE = 612;

	private ConvertCmd command = new ConvertCmd();

	@Override
	public void applyFilter(File imageFile) {
		try {
			boolean isGothamFilter = Math.random() > 0.5;
			if (isGothamFilter) {
				applyGothamFilter(imageFile.getAbsolutePath());
			} else {
				applyTaosterFilter(imageFile.getAbsolutePath());
			}
			applyCropResize(imageFile);
			applyBorder(imageFile.getAbsolutePath(), isGothamFilter ? "#000000" : "#ffffff");

		} catch (InfoException e) {
			log.error("Error reading image information.", e);
		}
	}

	private void applyCropResize(File imageFile) throws InfoException {
		Info imageInfo = new Info(imageFile.getAbsolutePath(), true);
		int height = imageInfo.getImageHeight();
		int width = imageInfo.getImageWidth();

		IMOperation operation = new IMOperation();
		operation.addImage();
		if (width != height) {
			if (width > height) {
				operation.crop(height, height, (width - height) / 2, 0);
			} else {
				operation.crop(width, width, 0, (height - width) / 2);
			}
		}
		operation.resize(IMAGE_SIZE);
		operation.addImage();
		runCommand(imageFile.getAbsolutePath(), operation);
	}

	private void runCommand(String fileName, IMOperation operation) {
		try {
			if (log.isDebugEnabled()) {
				command.createScript(File.createTempFile("script", fileName).getAbsolutePath(), operation);
			}
			command.run(operation, fileName, fileName);
		} catch (Exception e) {
			log.error("Error converting image. File name: " + fileName, e);
		}
	}

	/**
	 * Gotham filter: $this->execute(
	 * "convert $this->_tmp -modulate 120,10,100 -fill '#222b6d' -colorize 20 -gamma 0.5 -contrast -contrast $this->_tmp"
	 * );
	 * 
	 * @param operation
	 */
	private void applyGothamFilter(String fileName) {
		IMOperation operation = new IMOperation();
		operation.addImage();
		operation.modulate(120d, 10d, 100d);
		operation.fill("#222b6d");
		operation.colorize(20);
		operation.gamma(0.5);
		operation.contrast();
		operation.contrast();
		operation.addImage();
		runCommand(fileName, operation);
	}

	/**
	 * Toaster filter: convert img1.jpg +level-colors navy,lemonchiffon
	 * -contrast -contrast -contrast -gamma 0.9 ( -clone 0 -background black
	 * -vignette 700x100 ) -compose blend -define compose:args=40,60 -composite
	 * img2.jpg
	 * 
	 * @param operation
	 */
	private void applyTaosterFilter(String fileName) {
		IMOperation operation = new IMOperation();
		operation.addImage();
		operation.p_levelColors("navy", "lemonchiffon");
		operation.contrast();
		operation.gamma(0.9d);
		IMOperation cloneOperation = new IMOperation();
		cloneOperation.clone(0);
		cloneOperation.background("black");
		cloneOperation.vignette(700d, 100d);
		operation.addSubOperation(cloneOperation);
		operation.compose("blend");
		operation.define("compose:args=40,60");
		operation.composite();
		operation.addImage();
		runCommand(fileName, operation);
	}

	private void applyBorder(String fileName, String borderColor) {
		IMOperation operation = new IMOperation();
		operation.addImage();
		operation.bordercolor(borderColor);
		operation.border(15);
		operation.addImage();
		runCommand(fileName, operation);
	}
}
