package be.virtualsushi.wanuus;

import java.util.HashMap;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

	@Test
	public void test() {
		String string = "blah+blah+blah+";
		System.out.println(string.substring(0, string.length() - 1));
		HashMap<Long, Integer> map = new HashMap<Long, Integer>();
		map.put(1l, 0);
		System.out.println(map.put(1l, map.get(1l) + 1));
		System.out.println(map.put(1l, map.get(1l) + 1));
		System.out.println(map.put(1l, map.get(1l) + 1));
	}
}
