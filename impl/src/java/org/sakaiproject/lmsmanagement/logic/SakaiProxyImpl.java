package org.sakaiproject.lmsmanagement.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.lmsmanagement.model.ImportResponse;
import org.sakaiproject.lmsmanagement.model.ImportedMember;
import org.sakaiproject.lmsmanagement.model.ImportedUser;
import org.sakaiproject.lmsmanagement.model.SiteMember;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.SiteService.SelectionType;
import org.sakaiproject.site.api.SiteService.SortType;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserAlreadyDefinedException;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserEdit;
import org.sakaiproject.user.api.UserIdInvalidException;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.api.UserPermissionException;
import org.sakaiproject.util.BaseResourcePropertiesEdit;


/**
 * Implementation of our SakaiProxy API
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class SakaiProxyImpl implements SakaiProxy {

	private static final Logger log = Logger.getLogger(SakaiProxyImpl.class);
    
	/**
	 * properties that get translated in the UI
	 */
	private final String IMPORT_USER_RESPONSE_ALREADY_EXISTS="impl.import.user.response.already.exists";
	private final String IMPORT_USER_RESPONSE_INVALID_USERID="impl.import.user.response.invalid.userid";
	private final String IMPORT_MEMBER_RESPONSE_INVALID_SITEID="impl.import.member.response.invalid.siteid";
	private final String IMPORT_MEMBER_RESPONSE_INVALID_USERID="impl.import.member.response.invalid.userid";
	private final String IMPORT_MEMBER_RESPONSE_INVALID_SITEID_2="impl.import.member.response.invalid.siteid.2";
	private final String IMPORT_MEMBER_RESPONSE_INVALID_ROLE="impl.import.member.response.invalid.role";
	private final String UPDATE_USER_RESPONSE_FAILED="impl.update.member.response.failed";

	
	@Override
	public String getCurrentSiteId(){
		return toolManager.getCurrentPlacement().getContext();
	}
	
	@Override
	public String getCurrentUserId() {
		return sessionManager.getCurrentSessionUserId();
	}
	
	@Override
	public String getCurrentUserDisplayName() {
	   return userDirectoryService.getCurrentUser().getDisplayName();
	}
	
	@Override
	public boolean isSuperUser() {
		return securityService.isSuperUser();
	}
	
	@Override
	public void postEvent(String event,String reference,boolean modify) {
		eventTrackingService.post(eventTrackingService.newEvent(event,reference,modify));
	}
	
	@Override
	public String getSkinRepoProperty(){
		return serverConfigurationService.getString("skin.repo");
	}
	
	@Override
	public String getToolSkinCSS(String skinRepo){
		
		String skin = siteService.findTool(sessionManager.getCurrentToolSession().getPlacementId()).getSkin();			
		
		if(skin == null) {
			skin = serverConfigurationService.getString("skin.default");
		}
		
		return skinRepo + "/" + skin + "/tool.css";
	}
	
	@Override
	public boolean getConfigParam(String param, boolean dflt) {
		return serverConfigurationService.getBoolean(param, dflt);
	}
	
	@Override
	public String getConfigParam(String param, String dflt) {
		return serverConfigurationService.getString(param, dflt);
	}
	
	@Override
	public List<User> getUsers() {
		return userDirectoryService.getUsers();
	}
	
	@Override
	public User getUser(String uuid) {
		try {
			return userDirectoryService.getUser(uuid);
		} catch (UserNotDefinedException e){
			log.error("User: " + uuid + " could not be found.");
			return null;
		}
	}
	
	@Override
	public List<User> searchUsers(String search) {
		//must start at 1
		return userDirectoryService.searchUsers(search, 1, 200);
	}

	@Override
	public ImportResponse importUsers(List<ImportedUser> users) {
		
		int success = 0;
		ImportResponse response = new ImportResponse();
	
		for(ImportedUser user: users){
			
			log.debug("Processing: " + user.getEid());
			
			//if user already exists we want them edited
			if(userExists(user.getEid())){
				if(editUserByEid(user.getEid(), user)) {
					success++;
				} else {
					response.addReportable(user.getEid(), UPDATE_USER_RESPONSE_FAILED);
				}
			//otherwise create a new one
			} else {
			
				try {
					User newUser = userDirectoryService.addUser(null, user.getEid(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getType(), user.getProperties());
					log.info("Added user '"+ user.getEid()+"'");
					success++;
				}
				catch (UserAlreadyDefinedException e){
					log.warn("Error importing user '"+ user.getEid()+"': " + e.getClass() + ":" + e.getMessage());
					response.addReportable(user.getEid(), IMPORT_USER_RESPONSE_ALREADY_EXISTS);
				}
				catch (UserIdInvalidException e) {
					log.warn("Error importing user '"+ user.getEid()+"': " + e.getClass() + ":" + e.getMessage());
					response.addReportable(user.getEid(), IMPORT_USER_RESPONSE_INVALID_USERID);
				}
				catch (UserPermissionException e){
					log.warn("Error importing user '"+ user.getEid()+"': " + e.getClass() + ":" + e.getMessage());
					return null; //return immediately, we do not have permission
				} 
			}
		}
		
		//set success count
		response.setSuccess(success);
		
		return response;

	}
	
	@Override
	public boolean createUser(ImportedUser user) {
		try {
			User newUser = userDirectoryService.addUser(null, user.getEid(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getType(), user.getProperties());
			return true;
		}
		catch (Exception e){
			log.warn("Error adding user '"+ user.getEid()+"': " + e.getClass() + ":" + e.getMessage());
			return false;
		}
	}
	
	@Override
	public boolean editUser(ImportedUser user) {
		
		UserEdit userEdit = null;
		try {
			userEdit = userDirectoryService.editUser(user.getUuid());
			userEdit.setEid(user.getEid());
			userEdit.setFirstName(user.getFirstName());
			userEdit.setLastName(user.getLastName());
			userEdit.setEmail(user.getEmail());
			userEdit.setType(user.getType());
			
			//cannot set a blank password so if just editing we dont need to provide it and it wont be overriden.
			if(StringUtils.isNotBlank(user.getPassword())) {
				userEdit.setPassword(user.getPassword());
			}
			
			ResourcePropertiesEdit props = userEdit.getPropertiesEdit();
			if(props == null) {
				props = new BaseResourcePropertiesEdit();
			}
			props.addAll(user.getProperties());
			
			/* test properties
			for(Iterator i = user.getProperties().getPropertyNames(); i.hasNext();) {
				String key = (String)i.next();
				String value = user.getProperties().getProperty(key);
				System.out.println("Property in SakaiProxyImpl:" + key + "=" + value);
			}
			*/
			
			userDirectoryService.commitEdit(userEdit);
			return true;
		}
		catch (Exception e) {
			userDirectoryService.cancelEdit(userEdit);
			log.warn("Error editing user '"+ user.getEid()+"': " + e.getClass() + ":" + e.getMessage());
			return false;
		}
	}
	
	@Override
	public ImportedUser getImportedUser(String uuid) {
		
		if(StringUtils.isBlank(uuid)){
			log.error("Uuid was blank. Skipping lookup");
			return null;
		}
		
		User u = getUser(uuid);
		if(u == null) {
			return null;
		}
		
		ImportedUser iu = new ImportedUser();
		
		iu.setUuid(u.getId());
		iu.setEid(u.getEid());
		iu.setFirstName(u.getFirstName());
		iu.setLastName(u.getLastName());
		iu.setEmail(u.getEmail());
		//no password, there is no capability to do so
		iu.setProperties(u.getProperties());
		
		return iu;
	}
	
	@Override
	public List<Site> getSites(String search) {
				
		List<Site> sites = new ArrayList<Site>();
		List<Site> allSites = siteService.getSites(SelectionType.ANY, null, search,null, SortType.TITLE_ASC, null);
		
		for(Site s: allSites) {
			//filter my workspace sites
			if(siteService.isUserSite(s.getId())){
				continue;
			}
			
			//filter special sites
			if(siteService.isSpecialSite(s.getId())){
				continue;
			}
			
			//otherwise add it
			sites.add(s);
		}
		return sites;
				
	}
	
	@Override
	public Site getSite(String siteId) {
		try {
			return siteService.getSite(siteId);
		} catch (IdUnusedException e){
			log.error("Site: " + siteId + " could not be found.");
			return null;
		}
	}
	
	@Override
	public int getMemberCount(Site s) {
		return s.getMembers().size();
	}
	
	@Override
	public ImportResponse importMembers(List<ImportedMember> members) {
		
		int success = 0;
		ImportResponse response = new ImportResponse();
		
		//to make this more efficient, pre process the list and create a map of siteId to List<ImportedMembers> that are to be added to each site
		Map<String,List<ImportedMember>> importData = new HashMap<String,List<ImportedMember>>();

		for(ImportedMember member: members) {
			String siteId = member.getSiteId();
			
			//if not contained in the map already, create an entry
			if(!importData.containsKey(siteId)) {
				importData.put(siteId, new ArrayList<ImportedMember>());
			} 
			
			//get the Site
			List<ImportedMember> importList = importData.get(siteId);
			
			//add the member to the list
			importList.add(member);
		}
		
		//we now have a list of ImportedMembers for each site
		//iterate over it, get each site, add each member and save
		
		for(Map.Entry<String, List<ImportedMember>> entry: importData.entrySet()) {
			String siteId = entry.getKey();
			List<ImportedMember> importList = entry.getValue();
			
			//get the site, otherwise error
			Site site = getSite(siteId);
			if(site == null) {
				response.addReportable(siteId, IMPORT_MEMBER_RESPONSE_INVALID_SITEID);
				continue; //skip to next item
			}
			
			//get list of valid roles for this site - preprocess the Role list here into String list
			Set<Role> siteRoles = site.getRoles();
			List<String> validRoles = new ArrayList<String>();
			for(Role r: siteRoles) {
				validRoles.add(r.getId());
			}
		
			//add each member to the site
			for(ImportedMember im: importList) {
				String userEid = im.getUserEid();
				String role = im.getRole();
								
				//check null role
				if(StringUtils.isBlank(role)) {
					log.error("Role cannot be empty");
					response.addReportable(role, IMPORT_MEMBER_RESPONSE_INVALID_ROLE);
					continue; //skip to next item in this list
				}
				
				//check valid role
				if(!validRoles.contains(role)){
					log.error("Invalid role for site: " + siteId + ", role=" + role);
					response.addReportable(role, IMPORT_MEMBER_RESPONSE_INVALID_ROLE);
					continue; //skip to next item in this list
				}
				
				try {
					String userId = userDirectoryService.getUserByEid(userEid).getId();
					site.addMember(userId,role,true,false);
					success++;
				} catch (UserNotDefinedException e) {
					log.warn("Error importing member '"+ userEid +"': " + e.getClass() + ":" + e.getMessage());
					response.addReportable(userEid, IMPORT_MEMBER_RESPONSE_INVALID_USERID);
					continue; //skip to next item in this list
				} 
			}
			
			//save this site
			try {
				siteService.save(site);
			} catch (IdUnusedException e) {
				//this should already have been caught since we already have the site, but anyway...
				log.error("Site: " + siteId + " could not be found. This should already hve been caught which means that the site may have been removed mid import?");
				response.addReportable(siteId, IMPORT_MEMBER_RESPONSE_INVALID_SITEID_2);
				continue;
			} catch (PermissionException e) {
				log.warn("Error importing members to site: '"+ siteId +"': " + e.getClass() + ":" + e.getMessage());
				return null; //return immediately, we do not have permission
			}
		
		}
		
		//set the success count
		response.setSuccess(success);
		
		return response;
	}
	
	@Override
	public List<SiteMember> getSiteMembers(String siteId) {
		
		List<SiteMember> list = new ArrayList<SiteMember>();
		
		Site s = getSite(siteId);
		if(s == null) {
			return list;
		}
		
		Set<Member> members = s.getMembers();
		
		for(Member m: members) {
			SiteMember sm = new SiteMember();
			sm.setEid(m.getUserEid());
			sm.setRole(m.getRole().getId());
			
			list.add(sm);
		}
		
		return list;
	}
	
	@Override
	public String getUserDisplayName(String eid) {
		try {
			return userDirectoryService.getUserByEid(eid).getDisplayName();
		} catch (Exception e) {
			log.warn("Invalid user '"+ eid +"': " + e.getClass() + ":" + e.getMessage());
			return null;
		}
	}
	
	@Override
	public boolean allowUpdateUserEmail() {
		return userDirectoryService.allowUpdateUserEmail(getCurrentUserId());
	}
	
	@Override
	public boolean allowUpdateUserType() {
		return userDirectoryService.allowUpdateUserType(getCurrentUserId());
	}
	
	@Override
	public boolean allowUpdateUserName() {
		return userDirectoryService.allowUpdateUserName(getCurrentUserId());
	}
	
	@Override
	public boolean allowUpdateUserPassword() {
		return userDirectoryService.allowUpdateUserPassword(getCurrentUserId());
	}
	
	@Override
	public boolean userExists(String eid) {
		try {
			if (userDirectoryService.getUserByEid(eid) != null) {
				return true;
			}
		} catch (Exception e) {
			//do nothing, its going to be false
		}
		return false;
	}
	
	/**
	 * Internal method to allow the edit of a user given just the eid and the data to use.
	 * Does not update the eid, since that is the key here. Also cannot update password since there is no facility to do so.
	 * @param eid	eid of the user to lookup
	 * @param user	imported user object that we will take the data from
	 * @return
	 * @see editUser method is largely the same but is slighly different in that it finds the user from the ImportedUser object, plus allows eid edits.
	 */
	private boolean editUserByEid(String eid, ImportedUser user) {
		
		UserEdit userEdit = null;
		try {
			
			String uuid = userDirectoryService.getUserByEid(eid).getId();
			
			userEdit = userDirectoryService.editUser(uuid);
			userEdit.setFirstName(user.getFirstName());
			userEdit.setLastName(user.getLastName());
			userEdit.setEmail(user.getEmail());
			userEdit.setType(user.getType());
			
			//cannot set a blank password so if just editing we dont need to provide it and it wont be overriden.
			if(StringUtils.isNotBlank(user.getPassword())) {
				userEdit.setPassword(user.getPassword());
			}
			
			ResourcePropertiesEdit props = userEdit.getPropertiesEdit();
			if(props == null) {
				props = new BaseResourcePropertiesEdit();
			}
			props.addAll(user.getProperties());
			
			userDirectoryService.commitEdit(userEdit);
			return true;
		}
		catch (Exception e) {
			userDirectoryService.cancelEdit(userEdit);
			log.warn("Error editing user '"+ user.getEid()+"': " + e.getClass() + ":" + e.getMessage());
			return false;
		}
	}

	/**
	 * init - perform any actions required here for when this bean starts up
	 */
	public void init() {
		log.info("init");
	}
	
	@Getter @Setter
	private ToolManager toolManager;
	
	@Getter @Setter
	private SessionManager sessionManager;
	
	@Getter @Setter
	private UserDirectoryService userDirectoryService;
	
	@Getter @Setter
	private SecurityService securityService;
	
	@Getter @Setter
	private EventTrackingService eventTrackingService;
	
	@Getter @Setter
	private ServerConfigurationService serverConfigurationService;
	
	@Getter @Setter
	private SiteService siteService;

	
}
