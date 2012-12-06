package be.virtualsushi.wanuus.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service("dataAnalysisService")
public class DataAnalysisServiceImpl implements DataAnalysisService {

	@Override
	@Scheduled(fixedRate = 15 * 60 * 1000)
	public void processTweets() {
		System.out.println("perocessing tweets");
	}

}
