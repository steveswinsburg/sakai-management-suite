package org.sakaiproject.lmsmanagement.tool.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Simple model for storing a string, used by search forms
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class SearchModel implements Serializable {

	@Getter @Setter
	private String search;
}
