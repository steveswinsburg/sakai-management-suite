package org.sakaiproject.lmsmanagement.tool.panels;

import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.lmsmanagement.logic.ProjectLogic;
import org.sakaiproject.lmsmanagement.logic.SakaiProxy;
import org.sakaiproject.lmsmanagement.model.ImportResponse;
import org.sakaiproject.lmsmanagement.model.ImportedUser;
import org.sakaiproject.lmsmanagement.tool.LMSManagementApp;
import org.sakaiproject.lmsmanagement.tool.pages.UsersPage;

/**
 * Confirmation panel when uploading users. Shows all users in the uploaded file.
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class UserImportConfirmationStep extends Panel {

	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.SakaiProxy")
	private SakaiProxy sakaiProxy;
	
	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.ProjectLogic")
	private ProjectLogic projectLogic;
	
	private List<ImportedUser> users;
	
	
	/*
	 * Construct
	 */
	public UserImportConfirmationStep(String id, List<ImportedUser> users) {
		super(id);
		this.users = users;
		
		//display users
		add(new PageableListView<ImportedUser>("data", users, LMSManagementApp.MAX_ITEMS_PER_PAGE) {
			public void populateItem(final ListItem<ImportedUser> item) {
				final ImportedUser u = item.getModelObject();
				item.add(new Label("eid", u.getEid()));
				item.add(new Label("fname", u.getFirstName()));
				item.add(new Label("lname", u.getLastName()));
				item.add(new Label("email", u.getEmail()));
				item.add(new Label("password", u.getPassword()));
				item.add(new Label("type", u.getType()));
				item.add(new Label("properties", u.propertiesToMap().toString()));
			}
		});
		
		//confirm button
		add(new ConfirmationForm("form"));
	
	}
	
	private class ConfirmationForm extends Form<Void> {
				
		public ConfirmationForm(String id) {
			super(id);
			
		}
			
		@Override
        public void onSubmit(){
			ImportResponse response = sakaiProxy.importUsers(users);
			
			//if null, permission error
			if(response == null) {
				error(getString("error.parse.upload"));
			} else {
				//success
				if(response.getSuccess() > 0) {
					info(new StringResourceModel("action.users.imported", null, new Object[]{response.getSuccess()}).getObject());
				}
				
				//other errors
				for (Map.Entry<String, String> entry: response.getReportables().entrySet()){
					error(new StringResourceModel(entry.getValue(), null, new Object[]{entry.getKey()}).getObject());
				}
			}
			
			setResponsePage(new UsersPage());
						
        }
	}
}
