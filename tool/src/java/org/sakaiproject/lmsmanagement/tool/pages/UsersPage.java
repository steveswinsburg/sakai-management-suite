package org.sakaiproject.lmsmanagement.tool.pages;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import org.sakaiproject.lmsmanagement.tool.tabs.UserCreateTab;
import org.sakaiproject.lmsmanagement.tool.tabs.UserImportTab;
import org.sakaiproject.lmsmanagement.tool.tabs.UserListTab;

/**
 * Top level users page.
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class UsersPage extends BasePage {

	private PageParameters parameters;
	private int selectedTab;
	
	public UsersPage(PageParameters params, int tabIndex) {
		this.parameters=params;
		this.selectedTab=tabIndex;
		
		doRender();
	}
	
	public UsersPage() {
		this(null,0);
	}

	private void doRender() {
		
		disableLink(usersLink);

		// list of tabs
		List<ITab> tabs=new ArrayList<ITab>();
		tabs.add(new AbstractTab(new ResourceModel("heading.tab.user.list")) {
			public Panel getPanel(String panelId) {
				return new UserListTab(panelId, parameters);
			}
		});
		
		tabs.add(new AbstractTab(new ResourceModel("heading.tab.user.create")) {
			public Panel getPanel(String panelId) {
				return new UserCreateTab(panelId, parameters);
			}
		});
 
		tabs.add(new AbstractTab(new ResourceModel("heading.tab.user.import")) {
			public Panel getPanel(String panelId) {
				return new UserImportTab(panelId);
			}
		});
		
		//add(new AjaxTabbedPanel("tabs", tabs));

		TabbedPanel tabbedPanel = new TabbedPanel("tabs", tabs);
		tabbedPanel.setSelectedTab(selectedTab);
		
		
		add(tabbedPanel);
		
		
	}
	
}
