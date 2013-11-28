package com.opensourceagility.springintegration.alerts.urgent;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.opensourceagility.springintegration.alerts.Alert;

/**
 * Maps a row of the Alert table to an Alert object
 * 
 * @author Michael Lavelle
 */
@Component
public class AlertRowMapper implements RowMapper<Alert> {

	@Override
	public Alert mapRow(ResultSet rs, int rowNum) throws SQLException {

		Alert oddEven = new Alert();
		oddEven.setId(rs.getLong("id"));
		oddEven.setMessageCode(rs.getString("message_code"));
		oddEven.setPersistDate(rs.getTimestamp("persist_date"));
		oddEven.setUrgency(rs.getInt("urgency"));
		return oddEven;
	}

}