package org.sakaiproject.lmsmanagement.logic;

import java.util.List;

import org.sakaiproject.lmsmanagement.model.ImportResponse;
import org.sakaiproject.lmsmanagement.model.ImportedMember;
import org.sakaiproject.lmsmanagement.model.ImportedUser;
import org.sakaiproject.lmsmanagement.model.SiteMember;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.user.api.User;


/**
 * An interface to abstract all Sakai related API calls in a central method that can be injected into our app.
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public interface SakaiProxy {

	/**
	 * Get current siteid
	 * @return
	 */
	public String getCurrentSiteId();
	
	/**
	 * Get current user id
	 * @return
	 */
	public String getCurrentUserId();
	
	/**
	 * Get current user display name
	 * @return
	 */
	public String getCurrentUserDisplayName();
	
	/**
	 * Is the current user a superUser? (anyone in admin realm)
	 * @return
	 */
	public boolean isSuperUser();
	
	/**
	 * Post an event to Sakai
	 * 
	 * @param event			name of event
	 * @param reference		reference
	 * @param modify		true if something changed, false if just access
	 * 
	 */
	public void postEvent(String event,String reference,boolean modify);
	
	/**
	 * Wrapper for ServerConfigurationService.getString("skin.repo")
	 * @return
	 */
	public String getSkinRepoProperty();
	
	/**
	 * Gets the tool skin CSS first by checking the tool, otherwise by using the default property.
	 * @param	the location of the skin repo
	 * @return
	 */
	public String getToolSkinCSS(String skinRepo);
	
	/**
	 * Get a configuration parameter as a boolean
	 * 
	 * @param	dflt the default value if the param is not set
	 * @return
	 */
	public boolean getConfigParam(String param, boolean dflt);
	
	/**
	 * Get a configuration parameter as a String
	 * 
	 * @param	dflt the default value if the param is not set
	 * @return
	 */
	public String getConfigParam(String param, String dflt);
	
	/**
	 * Get a list of all users that Sakai knows about. No external users
	 * @return
	 */
	public List<User> getUsers();
	
	/**
	 * Get a User object given a uuid
	 * @param uuid uuid of the user
	 * @return
	 */
	public User getUser(String uuid);
	
	/**
	 * Import users into Sakai, taking data from the ImportedUser objects
	 * 
	 * @param users list of ImportedUser objects
	 * @return an ImportResponse wrapper which contains various bits of info the UI can use if it needs to
	 * If the response is null, it means there was a permission error.
	 */
	public ImportResponse importUsers(List<ImportedUser> users);
	
	/**
	 * Create a User in Sakai
	 * @param user	an ImportedUser object that holds the data we need
	 * @return
	 */
	public boolean createUser(ImportedUser user);
	
	/**
	 * Edit a User in Sakai
	 * @param user	an ImportedUser object that holds the data we need to update with the actual user
	 * @return
	 */
	public boolean editUser(ImportedUser user);
	
	/**
	 * Get a list of all users that Sakai knows about, that also meet the search criteria. Hardcoded to return max of 200 results
	 * 
	 * @param search	optional search term to filter by
	 * @return
	 */
	public List<User> searchUsers(String search);
	
	/**
	 * Get an ImportedUser object given a uuid. No password is included.
	 * 
	 * @param uuid uuid of the user
	 * @return
	 */
	public ImportedUser getImportedUser(String uuid);
	
	/**
	 * Get a list of all sites with optional search.
	 * @param search optional string, if set, will filter the list based on the site title
	 * @return
	 */
	public List<Site> getSites(String search);
	
	/**
	 * Get a Site object given a siteId
	 * @param siteId id of the site
	 * @return
	 */
	public Site getSite(String siteId);
	
	/**
	 * Get the count of members in the given site
	 * @param s Site
	 * @return
	 */
	public int getMemberCount(Site s);
	
	/**
	 * Import members into Sakai sites, taking data from the ImportedMember objects
	 * 
	 * @param users list of ImportedMember objects
	 * @return an ImportResponse wrapper which contains various bits of info the UI can use if it needs to
	 * If the response is null, it means there was a permission error.
	 */
	public ImportResponse importMembers(List<ImportedMember> members);
	
	/**
	 * Get a List of SiteMembers that are in a given site.
	 * @param siteId - id of the site
	 * @return
	 */
	public List<SiteMember> getSiteMembers(String siteId);
	
	/**
	 * Get the displayname for a user, given the eid
	 * @param eid	eid of the user
	 * @return
	 */
	public String getUserDisplayName(String eid);
	
	/**
	 * Is the current user able to update their own email address?
	 * @return
	 */
	public boolean allowUpdateUserEmail();
	
	/**
	 * Is the current user able to update their own account type?
	 * @return
	 */
	public boolean allowUpdateUserType();
	
	/**
	 * Is the current user able to update their own name?
	 * @return
	 */
	public boolean allowUpdateUserName();
	
	/**
	 * Is the current user able to update their own password?
	 * @return
	 */
	public boolean allowUpdateUserPassword();
	
	/**
	 * Check if the user exists, given an eid
	 * @param eid	eid of the user
	 * @return
	 */
	public boolean userExists(String eid);
	

}
