package org.sakaiproject.lmsmanagement.tool.panels;

import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.lmsmanagement.logic.ProjectLogic;
import org.sakaiproject.lmsmanagement.logic.SakaiProxy;
import org.sakaiproject.lmsmanagement.model.ImportResponse;
import org.sakaiproject.lmsmanagement.model.ImportedMember;
import org.sakaiproject.lmsmanagement.tool.LMSManagementApp;
import org.sakaiproject.lmsmanagement.tool.pages.MembershipPage;

/**
 * Confirmation panel when uploading members. Shows all members in the uploaded file.
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class MembershipImportConfirmationStep extends Panel {

	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.SakaiProxy")
	private SakaiProxy sakaiProxy;
	
	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.ProjectLogic")
	private ProjectLogic projectLogic;
	
	private List<ImportedMember> members;
	
	
	/*
	 * Construct
	 */
	public MembershipImportConfirmationStep(String id, List<ImportedMember> members) {
		super(id);
		this.members = members;
		
		//display users
		add(new PageableListView<ImportedMember>("data", members, LMSManagementApp.MAX_ITEMS_PER_PAGE) {
			public void populateItem(final ListItem<ImportedMember> item) {
				final ImportedMember m = item.getModelObject();
				item.add(new Label("siteid", m.getSiteId()));
				item.add(new Label("usereid", m.getUserEid()));
				item.add(new Label("role", m.getRole()));
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
			
			ImportResponse response = sakaiProxy.importMembers(members);
			
			//if null, permission error
			if(response == null) {
				error(getString("error.parse.upload"));
			} else {
				//success
				if(response.getSuccess() > 0) {
					info(new StringResourceModel("action.members.imported", null, new Object[]{response.getSuccess()}).getObject());
				}
				
				//other errors
				for (Map.Entry<String, String> entry: response.getReportables().entrySet()){
					error(new StringResourceModel(entry.getValue(), null, new Object[]{entry.getKey()}).getObject());
				}
			}
			
			setResponsePage(new MembershipPage());
						
        }
	}
}
