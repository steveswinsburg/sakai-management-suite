package org.sakaiproject.lmsmanagement.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

import org.sakaiproject.entity.api.ResourceProperties;

/**
 * Model object to store a record about an imported member
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
@Data
public class ImportedMember implements Serializable {

	private String siteId;
	private String userEid;
	private String role;
	
}