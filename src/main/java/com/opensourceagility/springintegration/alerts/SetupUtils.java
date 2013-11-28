package com.opensourceagility.springintegration.alerts;

import java.io.File;

import org.apache.log4j.Logger;

public class SetupUtils {

	private static final Logger LOGGER = Logger.getLogger(SetupUtils.class);

	public static void prepare() {
		LOGGER.info("Refreshing ActiveMQ and CSV data directories");
		File activeMqTempDir = new File("activemq-data");
		File csvTempDir = new File("csvfiles");

		deleteDir(activeMqTempDir);
		deleteDir(csvTempDir);

	}
	private static void deleteDir(File directory){
		if (directory.exists()){
			String[] children = directory.list();
			if (children != null){
				for (int i=0; i < children.length; i++) {
					deleteDir(new File(directory, children[i]));
				}
			}
		}
		directory.delete();
	}
}
