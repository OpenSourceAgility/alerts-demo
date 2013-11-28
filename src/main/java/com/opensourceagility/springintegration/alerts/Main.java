package com.opensourceagility.springintegration.alerts;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**

 */
public class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class);


	private final static String[] configFiles = {
		"/META-INF/spring/integration/common.xml",
		"/META-INF/spring/integration/urgent-alerts.xml",
		"/META-INF/spring/integration/non-urgent-alerts.xml"
	};

	public static void main(String[] args) {

		SetupUtils.prepare();
		LOGGER.info("Urgent Alerts (urgency > 50) are sent to a queue and are read off the queue to stdout");
		LOGGER.info("Non Urgent Alerts (urgency <= 50) are written as csv to text file and uploaded to S3 bucket");
		LOGGER.info("Polling database for new Alerts");

		new ClassPathXmlApplicationContext(configFiles, Main.class);

	}

}
