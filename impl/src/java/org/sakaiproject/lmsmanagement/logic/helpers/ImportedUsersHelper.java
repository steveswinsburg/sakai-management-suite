package org.sakaiproject.lmsmanagement.logic.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.apachecommons.CommonsLog;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.lmsmanagement.model.ImportedUser;
import org.sakaiproject.util.BaseResourcePropertiesEdit;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Helper class for dealing with uploaded user files
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
@CommonsLog
public class ImportedUsersHelper extends BaseImportHelper {
	
	//the column headings in the imported file, which will be used as the primary user attributes
	private static final String IMPORT_USER_ID="user id";
	private static final String IMPORT_FIRST_NAME="first name";
	private static final String IMPORT_LAST_NAME="last name";
	private static final String IMPORT_EMAIL="email";
	private static final String IMPORT_PASSWORD="password";
	private static final String IMPORT_TYPE="type";

	/**
	 * Parse a CSV into a list of ImportedUser objects. Returns list if ok, or null if error
	 * @param is InputStream of the data to parse
	 * @return
	 */
	public static List<ImportedUser> parseCsv(InputStream is) {
				
		//manually parse method so we can support arbitrary columns
		CSVReader reader = new CSVReader(new InputStreamReader(is));
	    String [] nextLine;
	    int lineCount = 0;
	    List<ImportedUser> list = new ArrayList<ImportedUser>();
	    Map<Integer,String> mapping = null;
	    
	    try {
		    while ((nextLine = reader.readNext()) != null) {
		        
		    	if(lineCount == 0) {
		        	//header row, capture it
		    		mapping = mapHeaderRow(nextLine);
		        } else {
		        	//map the fields into the object
		        	list.add(mapLine(nextLine, mapping));
		        }
		        lineCount++;
		    }
	    } catch (Exception e) {
			log.error("Error reading imported file: " + e.getClass() + " : " + e.getMessage());
			return null;
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	    		    
		return list;
	}
	
	/**
	 * Parse an XLS into a list of ImportedUser objects
	 * Note that only the first sheet of the Excel file is supported.
	 * 
	 * @param is InputStream of the data to parse
	 * @return
	 */
	public static List<ImportedUser> parseXls(InputStream is) {
		
		int lineCount = 0;
		List<ImportedUser> list = new ArrayList<ImportedUser>();
	    Map<Integer,String> mapping = null;
		
		try {
			Workbook wb = WorkbookFactory.create(is);
			Sheet sheet = wb.getSheetAt(0);
			for (Row row : sheet) {
				
				String[] r = convertRow(row);
				
				if(lineCount == 0) {
		        	//header row, capture it
					mapping = mapHeaderRow(r);
				} else {
			        //map the fields into the object
			        list.add(mapLine(r, mapping));
				}
				lineCount++;
			}
		
		} catch (Exception e) {
			log.error("Error reading imported file: " + e.getClass() + " : " + e.getMessage());
			return null;
		}
		
		return list;
	}
	
	/**
	* Takes a row of data and maps it into the appropriate ImportedUser properties
	* We have a fixed list of properties, anything else goes into ResourceProperties
	* @param line
	* @param mapping
	* @return
	*/
	private static ImportedUser mapLine(String[] line, Map<Integer,String> mapping){

		ImportedUser u = new ImportedUser();
		ResourceProperties p = new BaseResourcePropertiesEdit();
		
		for(Map.Entry<Integer,String> entry: mapping.entrySet()) {
			int i = entry.getKey();
			//trim in case some whitespace crept in
			String col = trim(entry.getValue());
						
			//now check each of the main properties in turn to determine which one to set, otherwise set into props
			if(StringUtils.equals(col, IMPORT_USER_ID)) {
				u.setEid(trim(line[i]));
			} else if(StringUtils.equals(col, IMPORT_FIRST_NAME)) {
				u.setFirstName(trim(line[i]));
			} else if(StringUtils.equals(col, IMPORT_LAST_NAME)) {
				u.setLastName(trim(line[i]));
			} else if(StringUtils.equals(col, IMPORT_EMAIL)) {
				u.setEmail(trim(line[i]));
			} else if(StringUtils.equals(col, IMPORT_PASSWORD)) {
				u.setPassword(trim(line[i]));
			} else if(StringUtils.equals(col, IMPORT_TYPE)) {
				u.setType(trim(line[i]));
			} else {
			
				//only add if not blank
				if(StringUtils.isNotBlank(trim(line[i]))) {
					p.addProperty(col, trim(line[i]));
				}
			}
		}

		u.setProperties(p);
		return u;
	}
	
	
}
