package org.sakaiproject.lmsmanagement.dao.impl;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import org.sakaiproject.lmsmanagement.model.AdditionalAttribute;

/**
 * RowMapper to handle Things
 *
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class AdditionalAttributeMapper implements RowMapper{
	
	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public AdditionalAttribute mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		AdditionalAttribute t = new AdditionalAttribute();
		
		t.setId(rs.getLong("ID"));
		t.setKey(rs.getString("ATTR_KEY"));
		t.setValue(rs.getString("ATTR_VALUE"));
		
		return t;
	}
	
	
	
}
