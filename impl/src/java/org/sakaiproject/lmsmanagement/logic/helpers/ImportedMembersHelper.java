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
import org.sakaiproject.lmsmanagement.model.ImportedMember;
import org.sakaiproject.lmsmanagement.model.ImportedUser;
import org.sakaiproject.util.BaseResourcePropertiesEdit;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Helper class for dealing with uploaded membership files
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
@CommonsLog
public class ImportedMembersHelper extends BaseImportHelper {
	
	//the column headings in the imported file, which will be used as the primary user attributes
	private static final String IMPORT_SITE_ID="site id";
	private static final String IMPORT_USER_ID="user id";
	private static final String IMPORT_USER_ROLE="role";

	/**
	 * Parse a CSV into a list of ImportedMember objects. Returns list if ok, or null if error
	 * @param is InputStream of the data to parse
	 * @return
	 */
	public static List<ImportedMember> parseCsv(InputStream is) {
				
		//manually parse method so we can support arbitrary columns
		CSVReader reader = new CSVReader(new InputStreamReader(is));
	    String [] nextLine;
	    int lineCount = 0;
	    List<ImportedMember> list = new ArrayList<ImportedMember>();
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
	 * Parse an XLS into a list of ImportedMember objects
	 * Note that only the first sheet of the Excel file is supported.
	 * 
	 * @param is InputStream of the data to parse
	 * @return
	 */
	public static List<ImportedMember> parseXls(InputStream is) {
		
		int lineCount = 0;
		List<ImportedMember> list = new ArrayList<ImportedMember>();
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
	* Takes a row of data and maps it into the appropriate ImportedMember properties
	* We have a fixed list of properties, anything else is discarded
	* @param line
	* @param mapping
	* @return
	*/
	private static ImportedMember mapLine(String[] line, Map<Integer,String> mapping){

		ImportedMember m = new ImportedMember();

		for(Map.Entry<Integer,String> entry: mapping.entrySet()) {
			int i = entry.getKey();
			//trim in case some whitespace crept in
			String col = trim(entry.getValue());

			//now check each of the main properties in turn to determine which one to set, otherwise it is bad data so ignore it.
			if(StringUtils.equals(col, IMPORT_SITE_ID)) {
				m.setSiteId(trim(line[i]));
			} else if(StringUtils.equals(col, IMPORT_USER_ID)) {
				m.setUserEid(trim(line[i]));
			} else if(StringUtils.equals(col, IMPORT_USER_ROLE)) {
				m.setRole(trim(line[i]));
			} 			
			
		}
		
		return m;
	}
	
	
}
