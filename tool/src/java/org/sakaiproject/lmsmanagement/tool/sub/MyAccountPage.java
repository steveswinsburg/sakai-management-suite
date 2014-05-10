package org.sakaiproject.lmsmanagement.tool.sub;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.apachecommons.CommonsLog;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.lmsmanagement.logic.ProjectLogic;
import org.sakaiproject.lmsmanagement.logic.SakaiProxy;
import org.sakaiproject.lmsmanagement.model.ImportedUser;
import org.sakaiproject.lmsmanagement.tool.LMSManagementApp;
import org.sakaiproject.lmsmanagement.tool.panels.UserAttributePanel;
import org.sakaiproject.user.api.User;
import org.sakaiproject.util.BaseResourcePropertiesEdit;

/**
 * Simplified page for the My Account app.
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
@CommonsLog
public class MyAccountPage extends WebPage implements IHeaderContributor {

	
	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.SakaiProxy")
	private SakaiProxy sakaiProxy;
	
	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.ProjectLogic")
	private ProjectLogic projectLogic;
	
	FeedbackPanel feedbackPanel;
	boolean editUser = false;

	boolean emailEditAllowed = true;
	
	public MyAccountPage() {
		
		log.debug("MyAccountPage()");
		
		//is the user currently logged in? if so, get their own record.
		String currentUserUuid = sakaiProxy.getCurrentUserId();
		ImportedUser user = null;
		if(StringUtils.isNotBlank(currentUserUuid)) {
			user = sakaiProxy.getImportedUser(currentUserUuid);
		}
		
		//if null, setup a blank one so the form can use it.
		if(user == null) {
			user = new ImportedUser();
			editUser = false;
		} else {
			//we are editing the user
			editUser = true;
		}
		
		//setup heading accordingly
		if(editUser){
			add(new Label("accountHeading", new ResourceModel("heading.myaccount")));
		} else {
			add(new Label("accountHeading", new ResourceModel("heading.newaccount")));
		}
		
		add(new CreateForm("form", user));
		
		// Add a FeedbackPanel for displaying our messages
        feedbackPanel = new FeedbackPanel("feedback"){
        	        	
        	@Override
        	protected Component newMessageDisplayComponent(String id, FeedbackMessage message) {
        		Component newMessageDisplayComponent = super.newMessageDisplayComponent(id, message);
        		
        		//set class
        		if(message.getLevel() == FeedbackMessage.ERROR ||
        			message.getLevel() == FeedbackMessage.DEBUG ||
        			message.getLevel() == FeedbackMessage.FATAL ||
        			message.getLevel() == FeedbackMessage.WARNING){
        			add(new SimpleAttributeModifier("class", "alertMessage"));
        		} else if(message.getLevel() == FeedbackMessage.INFO){
        			add(new SimpleAttributeModifier("class", "success"));        			
        		} 
        		
        		return newMessageDisplayComponent;
        	}
        	
        };
        add(feedbackPanel); 

	}
	
	private class CreateForm extends Form<ImportedUser> {
		
		public CreateForm(String id, ImportedUser user) {
			super(id);
			
			setDefaultModel(new CompoundPropertyModel<ImportedUser>(user));
			
			//eid
			RequiredTextField eid = new RequiredTextField<String>("eid");
			eid.setOutputMarkupId(true);
			add(eid);
			
			if(editUser) {
				eid.setEnabled(false);
			}
			
			//fname
			TextField fname = new TextField<String>("fname", new PropertyModel(user, "firstName"));
			fname.setOutputMarkupId(true);
			add(fname);
			
			//lname
			TextField lname = new TextField<String>("lname", new PropertyModel(user, "lastName"));
			lname.setOutputMarkupId(true);
			add(lname);
						
			if(editUser && !sakaiProxy.allowUpdateUserName()) {
				fname.setEnabled(false);
				lname.setEnabled(false);
			}
			
			//email		
			TextField email = new TextField<String>("email", new PropertyModel(user, "email"));
			email.setOutputMarkupId(true);
			add(email);
						
			if(editUser && !sakaiProxy.allowUpdateUserEmail()) {
				email.setEnabled(false);
			}
			
			//password
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
			
			//likewise if restricted but this time remove the field
			if(editUser && !sakaiProxy.allowUpdateUserPassword()) {
				password1Required.setVisible(false);
				password2Required.setVisible(false);
				p1.setVisible(false);
				p2.setVisible(false);
			}
			
			//ensure passwords match
			add(new EqualPasswordInputValidator(p1, p2));
						
			//type		
			TextField type = new TextField<String>("type", new PropertyModel(user, "type"));
			type.setOutputMarkupId(true);
			add(type);
						
			if(editUser && !sakaiProxy.allowUpdateUserType()) {
				type.setEnabled(false);
			}
			
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
				} else {
					error(getString("action.user.update.failed"));
				}
			} else {
				if(sakaiProxy.createUser(user)){
					info(getString("action.user.created"));
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
	
	
	/**
	 * This block adds the required wrapper markup to style it like a Sakai tool. 
	 * Add to this any additional CSS or JS references that you need.
	 * 
	 */
	public void renderHead(IHeaderResponse response) {
		
		
		//get Sakai skin
		String skinRepo = sakaiProxy.getSkinRepoProperty();
		String toolCSS = sakaiProxy.getToolSkinCSS(skinRepo);
		String toolBaseCSS = skinRepo + "/tool_base.css";
		
		//Sakai additions
		response.renderJavascriptReference("/library/js/headscripts.js");
		response.renderCSSReference(toolBaseCSS);
		response.renderCSSReference(toolCSS);
		response.renderOnLoadJavascript("setMainFrameHeight( window.name )");
				
		//Tool additions (at end so we can override if required)
		response.renderString("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		response.renderCSSReference("css/lmsmanagement.css");
		
	}
	
	
}
