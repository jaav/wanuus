package be.virtualsushi.wanuus.services;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import be.virtualsushi.wanuus.BaseWanuusTest;

public class GoogleSearchServiceTest extends BaseWanuusTest {

	@Autowired
	private GoogleSearchService googleSearchService;

	@Test
	public void testSearch() {
		String result = googleSearchService.searchForImage("flowers");
		Assert.assertNotNull(result);
		String result2 = googleSearchService.searchForImage("flowers", "house");
		Assert.assertEquals(result, result2);
	}

}
