package org.sakaiproject.lmsmanagement.logic;

import java.io.InputStream;
import java.util.List;

import lombok.Setter;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sakaiproject.lmsmanagement.dao.ProjectDao;
import org.sakaiproject.lmsmanagement.logic.helpers.ImportedMembersHelper;
import org.sakaiproject.lmsmanagement.logic.helpers.ImportedUsersHelper;
import org.sakaiproject.lmsmanagement.model.AdditionalAttribute;
import org.sakaiproject.lmsmanagement.model.ImportedMember;
import org.sakaiproject.lmsmanagement.model.ImportedUser;

/**
 * Implementation of {@link ProjectLogic}
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class ProjectLogicImpl implements ProjectLogic {

	private static final Logger log = Logger.getLogger(ProjectLogicImpl.class);
	
	//list of mimetypes for each category. Must be compatible with the parser
	private static final String[] XLS_MIME_TYPES={"application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
	private static final String[] CSV_MIME_TYPES={"text/csv"};
	
	
	@Override
	public AdditionalAttribute getAdditionalAttribute(long id) {
		return dao.getAdditionalAttribute(id);
	}
	
	@Override
	public List<AdditionalAttribute> getAdditionalAttributes() {
		return dao.getAdditionalAttributes();
	}
	
	@Override
	public boolean addOrUpdateAdditionalAttribute(AdditionalAttribute a) {
		
		//check if it already exists
		AdditionalAttribute exist = dao.getAdditionalAttribute(a.getId());
		if(exist == null) {
			return dao.addAdditionalAttribute(a);
		} else {
			return dao.updateAdditionalAttribute(a);
		}
	}
	
	@Override
	public boolean deleteAdditionalAttribute(long id) {
		return dao.deleteAdditionalAttribute(id);
	}
	
	
	@Override
	public List<ImportedUser> parseImportedUserFile(InputStream is, String mimetype){
		
		//determine file type and delegate
		if(ArrayUtils.contains(CSV_MIME_TYPES, mimetype)) {
			return ImportedUsersHelper.parseCsv(is);
		} else if (ArrayUtils.contains(XLS_MIME_TYPES, mimetype)) {
			return ImportedUsersHelper.parseXls(is);
		} else {
			log.error("Invalid file type for user import: " + mimetype);
		}
		return null;
	}
	
	@Override
	public List<ImportedMember> parseImportedMemberFile(InputStream is, String mimetype){
		
		//determine file type and delegate
		if(ArrayUtils.contains(CSV_MIME_TYPES, mimetype)) {
			return ImportedMembersHelper.parseCsv(is);
		} else if (ArrayUtils.contains(XLS_MIME_TYPES, mimetype)) {
			return ImportedMembersHelper.parseXls(is);
		} else {
			log.error("Invalid file type for member import: " + mimetype);
		}
		return null;
	}
	
	
	/**
	 * init - perform any actions required here for when this bean starts up
	 */
	public void init() {
		log.info("init");
	}

	
	
	@Setter
	private ProjectDao dao;
	
	/*
	@Setter
	private Cache cache;
	*/
	
	
}
