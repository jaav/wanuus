package be.virtualsushi.wanuus.services;

import java.io.IOException;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.springframework.stereotype.Service;

/**
 * Toaster filter: $this->tempfile(); $this->colortone($this->_tmp, '#330000',
 * 100, 0); $this->execute(
 * "convert $this->_tmp -modulate 150,80,100 -gamma 1.2 -contrast -contrast $this->_tmp"
 * ); $this->vignette($this->_tmp, 'none', 'LavenderBlush3');
 * $this->vignette($this->_tmp, '#ff9966', 'none'); $this->output(); Standard
 * instagram photo: 612x612
 * 
 * @author spv
 * 
 */
@Service("imageProcessorService")
public class ImageProcessorServiceImpl implements ImageProcessorService {

	private static final int MAX_SIZE = 612;

	@Override
	public void applyFilter(String... imageNames) {
		ConvertCmd command = new ConvertCmd();

		IMOperation operation = new IMOperation();
		operation.crop(100, 100);
		operation.bordercolor("#FFFFFF");
		operation.border(97);
		operation.colorize(0x330000, 100, 0);
		operation.modulate(150d, 80d, 100d);
		operation.gamma(1.2);
		operation.contrast();
		operation.contrast();

		try {
			command.run(operation);
		} catch (IOException e) {

		} catch (InterruptedException e) {

		} catch (IM4JavaException e) {

		}
	}
}
