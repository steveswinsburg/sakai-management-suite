package org.sakaiproject.lmsmanagement.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

import org.sakaiproject.entity.api.ResourceProperties;

/**
 * Model object to store a record about an imported user
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
@Data
public class ImportedUser implements Serializable {

	private String uuid;
	private String eid;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String type;
	private ResourceProperties properties;
		
	/**
	 * Convenience method to get the list of properties as a map;
	 * @return
	 */
	public Map<String,String> propertiesToMap() {
		Map<String,String> m = new LinkedHashMap<String, String>();
		Iterator<String> iter = properties.getPropertyNames();
		while(iter.hasNext()){
			String prop = iter.next();
			m.put(prop, properties.getProperty(prop));
			
		}
		return m;
		
	}
	
}