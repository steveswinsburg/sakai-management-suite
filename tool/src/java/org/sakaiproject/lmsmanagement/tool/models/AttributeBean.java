package org.sakaiproject.lmsmanagement.tool.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Class for holding details about an attribute including the key, display value and the user entered value
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class AttributeBean implements Serializable {

	@Getter @Setter private String key;	//from AdditionalAttribute.key
	@Getter @Setter private String display; //from AdditionalAttribute.value
	@Getter @Setter private String value;	//user entered value
}
