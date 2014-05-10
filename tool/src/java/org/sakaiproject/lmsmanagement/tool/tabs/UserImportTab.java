package org.sakaiproject.lmsmanagement.tool.tabs;

import org.apache.wicket.markup.html.panel.Panel;
import org.sakaiproject.lmsmanagement.tool.panels.UserImportUploadStep;

/**
 * Panel that bootstraps the import panels
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class UserImportTab extends Panel {

	public UserImportTab(String id) {
		super(id);
		add (new UserImportUploadStep("wizard"));
	}
	
	
}
