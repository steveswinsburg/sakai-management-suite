package org.sakaiproject.lmsmanagement.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * Wrapper bean for displaying site Members
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */

public class SiteMember implements Serializable {
	
	@Getter @Setter
	private String eid;
	
	@Getter @Setter
	private String role;
	
}
