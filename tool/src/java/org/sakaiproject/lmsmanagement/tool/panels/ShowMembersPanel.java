package org.sakaiproject.lmsmanagement.tool.panels;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.lmsmanagement.logic.ProjectLogic;
import org.sakaiproject.lmsmanagement.logic.SakaiProxy;
import org.sakaiproject.lmsmanagement.model.SiteMember;
import org.sakaiproject.lmsmanagement.tool.LMSManagementApp;

/**
 * Shows all members in the selected site.
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class ShowMembersPanel extends Panel {

	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.SakaiProxy")
	private SakaiProxy sakaiProxy;
	
	/*
	 * Construct
	 */
	public ShowMembersPanel(String id, String siteId) {
		super(id);
		
		//get the list of members
    	List<SiteMember> members = sakaiProxy.getSiteMembers(siteId);
		
		//display users
		add(new PageableListView<SiteMember>("data", members, LMSManagementApp.MAX_ITEMS_PER_PAGE) {
			public void populateItem(final ListItem<SiteMember> item) {
				final SiteMember m = item.getModelObject();
				item.add(new Label("name", sakaiProxy.getUserDisplayName(m.getEid())));
				item.add(new Label("eid", m.getEid()));
				item.add(new Label("role", m.getRole()));
			}
		});
		
	}
	
}
