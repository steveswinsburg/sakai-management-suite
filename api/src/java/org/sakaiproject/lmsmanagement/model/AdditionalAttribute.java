package org.sakaiproject.lmsmanagement.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * An additional attribute that can be attached to a user. Has key and value. The value that one fills in is stored as a User property.
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */

public class AdditionalAttribute implements Serializable {
	
	@Getter @Setter
	private long id;
	
	@Getter @Setter
	private String key;
	
	@Getter @Setter
	private String value;
	
}
