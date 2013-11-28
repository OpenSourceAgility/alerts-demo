package com.opensourceagility.springintegration.alerts.nonurgent;

import org.apache.log4j.Logger;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Transformer;

import com.opensourceagility.springintegration.alerts.Alert;

/**
 * Transforms an Alert to a line of delimited text as defined by injected LineAggregator, and appends a new line
 * 
 * @author Michael Lavelle
 */
public class AlertTextTransformer {

	@Autowired
	private LineAggregator<Alert> lineAggregator;
	
	private static final Logger LOGGER = Logger.getLogger(AlertTextTransformer.class);

	
	@Transformer
	public String transform(Alert alert)
	{
		String csv = lineAggregator.aggregate(alert);
		String csvLine = csv + "\n";
		LOGGER.debug("Tranformed alert to csv:" + csv); 
		return csvLine;
	}
}
