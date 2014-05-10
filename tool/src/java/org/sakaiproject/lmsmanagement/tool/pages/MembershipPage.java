package org.sakaiproject.lmsmanagement.tool.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import org.sakaiproject.lmsmanagement.tool.tabs.MembershipImportTab;
import org.sakaiproject.lmsmanagement.tool.tabs.MembershipListTab;
import org.sakaiproject.lmsmanagement.tool.tabs.UserCreateTab;
import org.sakaiproject.lmsmanagement.tool.tabs.UserImportTab;
import org.sakaiproject.lmsmanagement.tool.tabs.UserListTab;

/**
 * Top level membership page.
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class MembershipPage extends BasePage {

	private PageParameters parameters;
	private int selectedTab;
	
	public MembershipPage(PageParameters params, int tabIndex) {
		this.parameters=params;
		this.selectedTab=tabIndex;
		
		doRender();
	}
	
	public MembershipPage() {
		this(null,0);
	}

	private void doRender() {
		
		disableLink(membershipLink);

		// list of tabs
		List<ITab> tabs=new ArrayList<ITab>();
		tabs.add(new AbstractTab(new ResourceModel("heading.tab.membership.list")) {
			public Panel getPanel(String panelId) {
				return new MembershipListTab(panelId, parameters);
			}
		});
		
		
		tabs.add(new AbstractTab(new ResourceModel("heading.tab.membership.import")) {
			public Panel getPanel(String panelId) {
				return new MembershipImportTab(panelId);
			}
		});
		
 

		TabbedPanel tabbedPanel = new TabbedPanel("tabs", tabs);
		tabbedPanel.setSelectedTab(selectedTab);
		
		
		add(tabbedPanel);
		
		
	}
}
