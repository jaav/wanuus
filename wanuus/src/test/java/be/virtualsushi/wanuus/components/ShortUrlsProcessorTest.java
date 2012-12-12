package be.virtualsushi.wanuus.components;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import be.virtualsushi.wanuus.BaseWanuusTest;
import be.virtualsushi.wanuus.components.ShortUrlsProcessor;

public class ShortUrlsProcessorTest extends BaseWanuusTest {

	@Autowired
	private ShortUrlsProcessor shortUrlsProcessor;

	@Test
	public void testShowUrlsProcessor() {
		Assert.assertEquals("http://www.meetup.com/Bay-Area-Scala-Enthusiasts/events/88585602/", shortUrlsProcessor.getRealUrl("http://buff.ly/Q0Iqdm"));
	}

}
