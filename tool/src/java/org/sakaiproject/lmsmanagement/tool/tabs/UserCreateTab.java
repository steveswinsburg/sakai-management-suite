package org.sakaiproject.lmsmanagement.tool.tabs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.lmsmanagement.logic.ProjectLogic;
import org.sakaiproject.lmsmanagement.logic.SakaiProxy;
import org.sakaiproject.lmsmanagement.model.AdditionalAttribute;
import org.sakaiproject.lmsmanagement.model.ImportedUser;
import org.sakaiproject.lmsmanagement.tool.LMSManagementApp;
import org.sakaiproject.lmsmanagement.tool.pages.UsersPage;
import org.sakaiproject.lmsmanagement.tool.panels.UserAttributePanel;
import org.sakaiproject.util.BaseResourcePropertiesEdit;

/**
 * Panel to create a single user
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class UserCreateTab extends Panel {

	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.SakaiProxy")
	private SakaiProxy sakaiProxy;
	
	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.ProjectLogic")
	private ProjectLogic logic;

	private String editUuid;
	boolean editUser = false;
	
	public UserCreateTab(String id, PageParameters params) {
		super(id);
		
		//if we have the edit uuid string in the params, set it up
		if(params != null) {
			editUuid = params.getString("edit");
		}
		
		//get obj based on passed in param
		ImportedUser user = null;
		if(StringUtils.isNotBlank(editUuid)) {
			user = sakaiProxy.getImportedUser(editUuid);
		}
		
		//if null, setup a blank one so the form can use it.
		if(user == null) {
			user = new ImportedUser();
			editUser = false;
		} else {
			//we are editing the user
			editUser = true;
		}
		
		add(new CreateForm("form", user));
	}
	
	private class CreateForm extends Form<ImportedUser> {
		
		public CreateForm(String id, ImportedUser user) {
			super(id);
			
			setDefaultModel(new CompoundPropertyModel<ImportedUser>(user));
			
			//add form fields
			add(new RequiredTextField<String>("eid").setOutputMarkupId(true));
			add(new TextField<String>("fname", new PropertyModel(user, "firstName")).setOutputMarkupId(true));
			add(new TextField<String>("lname", new PropertyModel(user, "lastName")).setOutputMarkupId(true));
			add(new TextField<String>("email", new PropertyModel(user, "email")).setOutputMarkupId(true));
			
			Label password1Required = new Label("password1Required", new ResourceModel("label.required"));
			Label password2Required = new Label("password2Required", new ResourceModel("label.required"));
			add(password1Required);
			add(password2Required);
			
			PasswordTextField p1 = new PasswordTextField("password1", new PropertyModel(user, "password"));
			PasswordTextField p2 = new PasswordTextField("password2", new Model(""));
			p1.setOutputMarkupId(true);
			p2.setOutputMarkupId(true);
			add(p1);
			add(p2);
			
			//if editing we dont need to ensure they have a password set
			//or show the required labels
			if(editUser) {
				password1Required.setVisible(false);
				password2Required.setVisible(false);
				p1.setRequired(false);
				p2.setRequired(false);
			}
			
			//ensure passwords match
			add(new EqualPasswordInputValidator(p1, p2));
			
			add(new TextField<String>("type", new PropertyModel(user, "type")).setOutputMarkupId(true));
			
			//panel to manage attributes
			add(new UserAttributePanel("attributes", user));

		}
			
		@Override
        public void onSubmit(){
			ImportedUser user = getModelObject();
			
			//get existing properties.
			ResourceProperties properties = user.getProperties();
			if(properties == null) {
				properties = new BaseResourcePropertiesEdit();
			}
			
			//get the attributes from the request
			Map<String,String> requestAttributes = getAdditionalAttributes();
			
			//iterate over the request and collect the attributes that were sent
			//any null valued ones will wipe out existing ones
			//and any duplicate ones will override previous ones (currently)
			for (Map.Entry<String, String> entry : requestAttributes.entrySet()) {
				String key = entry.getKey();
				if(StringUtils.isNotBlank(key)) {
					String value = entry.getValue();
					//System.out.println("Adding property: " + key + " with value: " + value);
					properties.addProperty(key, value);
				}
			}
			
			//add the props to the user
			user.setProperties(properties);
			
			/* test properties
			System.out.println("All properties to be saved. May include properties that we do not track since they are set by other apps:");
			for(Iterator i = properties.getPropertyNames(); i.hasNext();) {
					String key = (String)i.next();
					String value = properties.getProperty(key);
					System.out.println("Property:" + key + "=" + value);
			}
			*/
			
			if(editUser) {
				if(sakaiProxy.editUser(user)){
					info(getString("action.user.updated"));
					setResponsePage(new UsersPage());
				} else {
					error(getString("action.user.update.failed"));
				}
			} else {
				if(sakaiProxy.createUser(user)){
					info(getString("action.user.created"));
					setResponsePage(new UsersPage());
				} else {
					error(getString("action.user.create.failed"));
				}
			}
			
			
		}
		
		/**
		 * Get the properties submitted from the form
		 * @return
		 */
		private Map<String,String> getAdditionalAttributes() {
			
			Map<String,String> m = new HashMap<String,String>();
			
			//get the raw request
			Request request = RequestCycle.get().getRequest();
			Map<String,String[]> params = request.getParameterMap();
			
			//get the params we are interested in
			for (Map.Entry<String, String[]> entry : params.entrySet()) {
				String param = entry.getKey();
				
				if(StringUtils.startsWith(param, LMSManagementApp.ATTR_PREFIX)) {
					
					//note we can only handle single valued properties
					String value = request.getParameter(param);
					
					//string the param prefix for storing
					m.put(StringUtils.removeStart(param, LMSManagementApp.ATTR_PREFIX), value);
				}
			}
			
			return m;
		}
		
		
	}
	
	
}
