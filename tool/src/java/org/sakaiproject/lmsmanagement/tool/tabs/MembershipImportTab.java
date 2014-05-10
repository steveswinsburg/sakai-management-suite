package org.sakaiproject.lmsmanagement.tool.tabs;

import org.apache.wicket.markup.html.panel.Panel;
import org.sakaiproject.lmsmanagement.tool.panels.MembershipImportUploadStep;
import org.sakaiproject.lmsmanagement.tool.panels.UserImportUploadStep;

/**
 * Panel that bootstraps the import panels
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class MembershipImportTab extends Panel {

	public MembershipImportTab(String id) {
		super(id);
		add (new MembershipImportUploadStep("wizard"));
	}
	
	
}
