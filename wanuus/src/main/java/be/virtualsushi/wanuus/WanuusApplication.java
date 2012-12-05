package be.virtualsushi.wanuus;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class WanuusApplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		new AnnotationConfigApplicationContext(WanuusApplicationFactory.class);

	}

}
