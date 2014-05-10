package org.sakaiproject.lmsmanagement.dao;

import java.util.List;

import org.sakaiproject.lmsmanagement.model.AdditionalAttribute;

/**
 * DAO interface for our project
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public interface ProjectDao {

	/**
	 * Gets a single AdditionalAttribute from the db
	 * @param id the id of the attribute
	 * @return an item or null if no result
	 */
	public AdditionalAttribute getAdditionalAttribute(long id);
	
	/**
	 * Get all AdditionalAttributes
	 * @return a list of items, an empty list if no items
	 */
	public List<AdditionalAttribute> getAdditionalAttributes();
		
	/**
	 * Add a new AdditionalAttribute record to the database.
	 * @param a AdditionalAttribute
	 * @return	true if success, false if not
	 */
	public boolean addAdditionalAttribute(AdditionalAttribute a);
	
	/**
	 * Delete an AdditionalAttribute from the database
	 * @param id the id of the attribute
	 * @return	true if success, false if not
	 */
	public boolean deleteAdditionalAttribute(long id);
	
	/**
	 * Update the given AdditionalAttribute record in the database
	 * @param a	AdditionalAttribute
	 * @return boolean if success, false if not
	 */
	public boolean updateAdditionalAttribute(AdditionalAttribute a);
}
