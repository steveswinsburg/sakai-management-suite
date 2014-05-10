package org.sakaiproject.lmsmanagement.logic;

import java.io.InputStream;
import java.util.List;

import org.sakaiproject.lmsmanagement.model.AdditionalAttribute;
import org.sakaiproject.lmsmanagement.model.ImportedMember;
import org.sakaiproject.lmsmanagement.model.ImportedUser;

/**
 * An example logic interface
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public interface ProjectLogic {

	/**
	 * Get an AdditionalAttribute
	 * @param id the id of the attribute
	 * @return
	 */
	public AdditionalAttribute getAdditionalAttribute(long id);
	
	/**
	 * Get all AdditionalAttributes
	 * @return
	 */
	public List<AdditionalAttribute> getAdditionalAttributes();
	
	/**
	 * Add a new AdditionalAttribute to the database, or update an existing one
	 * @param a	the AdditionalAttribute to add/update
	 * @return boolean if success, false if not
	 */
	public boolean addOrUpdateAdditionalAttribute(AdditionalAttribute a);
	
	/**
	 * Delete an AdditionalAttribute from the database
	 * @param id the id of the attribute
	 * @return boolean if success, false if not
	 */
	public boolean deleteAdditionalAttribute(long id);
	
	/**
	 * Parse the import file into a list of ImportedUser objects
	 * @param is InputStream of the data to parse
	 * @param mimetype type of stream content this is
	 * @return list or null if error
	 */
	public List<ImportedUser> parseImportedUserFile(InputStream is, String mimetype);
	
	/**
	 * Parse the import file into a list of ImportedMember objects
	 * @param is InputStream of the data to parse
	 * @param mimetype type of stream content this is
	 * @return list or null if error
	 */
	public List<ImportedMember> parseImportedMemberFile(InputStream is, String mimetype);

	
}
