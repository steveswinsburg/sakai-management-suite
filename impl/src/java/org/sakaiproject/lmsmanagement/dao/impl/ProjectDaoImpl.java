package org.sakaiproject.lmsmanagement.dao.impl;

import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.InvariantReloadingStrategy;
import org.apache.log4j.Logger;

import org.sakaiproject.component.cover.ServerConfigurationService;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import org.sakaiproject.lmsmanagement.dao.ProjectDao;
import org.sakaiproject.lmsmanagement.model.AdditionalAttribute;


/**
 * Implementation of ProjectDao
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class ProjectDaoImpl extends JdbcDaoSupport implements ProjectDao {

	private static final Logger log = Logger.getLogger(ProjectDaoImpl.class);
	
	private PropertiesConfiguration statements;
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public AdditionalAttribute getAdditionalAttribute(long id) {
		
		if(log.isDebugEnabled()) {
			log.debug("getAdditionalAttribute(" + id + ")");
		}
		
		try {
			return (AdditionalAttribute) getJdbcTemplate().queryForObject(getStatement("select.additionalattribute"),
				new Object[]{Long.valueOf(id)},
				new AdditionalAttributeMapper()
			);
		} catch (DataAccessException ex) {
           log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
           return null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<AdditionalAttribute> getAdditionalAttributes() {
		if(log.isDebugEnabled()) {
			log.debug("getAdditionalAttributes()");
		}
		
		try {
			return getJdbcTemplate().query(getStatement("select.additionalattributes"),
				new AdditionalAttributeMapper()
			);
		} catch (DataAccessException ex) {
           log.warn("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
           return null;
		} 
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public boolean addAdditionalAttribute(AdditionalAttribute a) {
		
		if(log.isDebugEnabled()) {
			log.debug("addAdditionalAttribute( " + a.toString() + ")");
		}
		
		try {
			getJdbcTemplate().update(getStatement("insert.additionalattribute"),
				new Object[]{a.getKey(), a.getValue()}
			);
			return true;
		} catch (DataAccessException ex) {
           log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
           return false;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public boolean updateAdditionalAttribute(AdditionalAttribute a) {
		
		if(log.isDebugEnabled()) {
			log.debug("updateAdditionalAttribute( " + a.toString() + ")");
		}
		
		try {
			getJdbcTemplate().update(getStatement("update.additionalattribute"),
				new Object[]{a.getKey(), a.getValue(), Long.valueOf(a.getId())}
			);
			return true;
		} catch (DataAccessException ex) {
           log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
           return false;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public boolean deleteAdditionalAttribute(long id) {
		
		if(log.isDebugEnabled()) {
			log.debug("deleteAdditionalAttribute(" + id + ")");
		}
		
		try {
			getJdbcTemplate().update(getStatement("delete.additionalattribute"),
				new Object[]{Long.valueOf(id)}
			);
			return true;
		} catch (DataAccessException ex) {
           log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
           return false;
		}
	}
	
	/**
	 * init
	 */
	public void init() {
		log.info("init()");
		
		//setup the vendor
		String vendor = ServerConfigurationService.getInstance().getString("vendor@org.sakaiproject.db.api.SqlService", null);
		
		//initialise the statements
		initStatements(vendor);
		
		//setup tables if we have auto.ddl enabled.
		boolean autoddl = ServerConfigurationService.getInstance().getBoolean("auto.ddl", true);
		if(autoddl) {
			initTables();
			initIndexes();
		}
	}
	
	/**
	 * Sets up our tables
	 */
	private void initTables() {
		try {
			getJdbcTemplate().execute(getStatement("create.table"));
		} catch (DataAccessException ex) {
			log.info("Error creating tables: " + ex.getClass() + ":" + ex.getMessage());
			return;
		}
	}
	
	/**
	 * Sets up our indexes
	 */
	private void initIndexes() {
		try {
			getJdbcTemplate().execute(getStatement("create.index"));
		} catch (DataAccessException ex) {
			log.info("Error creating indexes. This is safe to ignore as the index probably already exists.");
			return;
		}
	}
	
	/**
	 * Loads our SQL statements from the appropriate properties file
	 
	 * @param vendor	DB vendor string. Must be one of mysql, oracle, hsqldb
	 */
	private void initStatements(String vendor) {
		
		URL url = getClass().getClassLoader().getResource(vendor + ".properties"); 
		
		try {
			statements = new PropertiesConfiguration(); //must use blank constructor so it doesn't parse just yet (as it will split)
			statements.setReloadingStrategy(new InvariantReloadingStrategy());	//don't watch for reloads
			statements.setThrowExceptionOnMissing(true);	//throw exception if no prop
			statements.setDelimiterParsingDisabled(true); //don't split properties
			statements.load(url); //now load our file
		} catch (ConfigurationException e) {
			log.error(e.getClass() + ": " + e.getMessage());
			return;
		}
	}
	
	/**
	 * Get an SQL statement for the appropriate vendor from the bundle
	
	 * @param key
	 * @return statement or null if none found. 
	 */
	private String getStatement(String key) {
		try {
			return statements.getString(key);
		} catch (NoSuchElementException e) {
			log.error("Statement: '" + key + "' could not be found in: " + statements.getFileName());
			return null;
		}
	}
}
