package be.virtualsushi.wanuus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.TaskScheduler;

import be.virtualsushi.wanuus.services.DataAnalysisService;
import be.virtualsushi.wanuus.services.DataCreationService;

import java.util.Date;

public class WanuusApplication {

	private static final Logger log = LoggerFactory.getLogger(WanuusApplication.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			ApplicationContext applicationContext = new AnnotationConfigApplicationContext(WanuusApplicationFactory.class);
			applicationContext.getBean(DataCreationService.class).createListData();
			final DataAnalysisService dataAnalysisService = applicationContext.getBean(DataAnalysisService.class);
			applicationContext.getBean(TaskScheduler.class).scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					dataAnalysisService.analyseTweets();
				}
			}, new Date(new Date().getTime()+5 * 60 * 1000), 15 * 60 * 1000);
		} catch (Exception e) {
			log.error("Wanuus application error:", e);
		}

	}
}
