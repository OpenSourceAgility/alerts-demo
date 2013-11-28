package com.opensourceagility.springintegration.alerts.nonurgent;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

/**
 * Maps a filePath returned from SQL query to a File object
 * 
 * @author Michael Lavelle
 */
@Component
public class FilePathToFileMapper implements RowMapper<File> {

	@Override
	public File mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new File(rs.getString("filePath"));
	}

}
