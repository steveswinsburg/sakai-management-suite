package org.sakaiproject.lmsmanagement.model;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Wrapper so we can bundle up bits of information for reporting.
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class ImportResponse {

	/**
	 * Count of the items that were successfully imported
	 */
	@Getter @Setter
	private int success;
	
	/**
	 * Map of items that we need to report on
	 */
	@Getter
	private Map<String,String> reportables;
	

	/**
	 * Method to add a report
	 * @param id
	 * @param message
	 */
	public void addReportable(String id, String message){
		reportables.put(id, message);
	}
	
	/**
	 * Constructor for initialising the fields
	 */
	public ImportResponse() {
		reportables = new LinkedHashMap<String,String>();
		success=0;
	}
}
