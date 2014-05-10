package org.sakaiproject.lmsmanagement.tool.pages;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.lmsmanagement.logic.ProjectLogic;
import org.sakaiproject.lmsmanagement.logic.SakaiProxy;


/**
 * This is our base page for our Sakai app. It sets up the containing markup and top navigation.
 * All top level pages should extend from this page so as to keep the same navigation. The content for those pages will
 * be rendered in the main area below the top nav.
 * 
 * <p>It also allows us to setup the API injection and any other common methods, which are then made available in the other pages.
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class BasePage extends WebPage implements IHeaderContributor {

	private static final Logger log = Logger.getLogger(BasePage.class); 
	
	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.SakaiProxy")
	protected SakaiProxy sakaiProxy;
	
	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.ProjectLogic")
	protected ProjectLogic projectLogic;
	
	Link<Void> usersLink;
	Link<Void> membershipLink;
	Link<Void> attributesLink;
	
	FeedbackPanel feedbackPanel;
	
	public BasePage() {
		
		log.debug("BasePage()");
				
    	//users link
		usersLink = new Link<Void>("usersLink") {
			private static final long serialVersionUID = 1L;
			public void onClick() {
				setResponsePage(new UsersPage());
			}
		};
		usersLink.add(new Label("usersLinkLabel",new ResourceModel("link.users")).setRenderBodyOnly(true));
		usersLink.add(new AttributeModifier("title", true, new ResourceModel("link.users.tooltip")));
		add(usersLink);
		
		
		
		//membership link
		membershipLink = new Link<Void>("membershipLink") {
			private static final long serialVersionUID = 1L;
			public void onClick() {
				setResponsePage(new MembershipPage());
			}
		};
		membershipLink.add(new Label("membershipLinkLabel",new ResourceModel("link.membership")).setRenderBodyOnly(true));
		membershipLink.add(new AttributeModifier("title", true, new ResourceModel("link.membership.tooltip")));
		add(membershipLink);
		
		
		
		//attributes link
		attributesLink = new Link<Void>("attributesLink") {
			private static final long serialVersionUID = 1L;
			public void onClick() {
				setResponsePage(new AttributesPage());
			}
		};
		attributesLink.add(new Label("attributesLinkLabel",new ResourceModel("link.attributes")).setRenderBodyOnly(true));
		attributesLink.add(new AttributeModifier("title", true, new ResourceModel("link.attributes.tooltip")));
		add(attributesLink);
		
		
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
	
	/**
	 * Helper to clear the feedbackpanel display.
	 * @param f	FeedbackPanel
	 */
	public void clearFeedback(FeedbackPanel f) {
		if(!f.hasFeedbackMessage()) {
			f.add(new SimpleAttributeModifier("class", ""));
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
		
		response.renderJavascriptReference("/library/js/jquery/1.4.2/jquery-1.4.2.min.js");
		
		//Tool additions (at end so we can override if required)
		response.renderString("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		response.renderCSSReference("css/lmsmanagement.css");
		//response.renderJavascriptReference("js/my_tool_javascript.js");
		
	}
	
	
	/** 
	 * Helper to disable a link. Add the Sakai class 'current'.
	 */
	protected void disableLink(Link<Void> l) {
		l.add(new SimpleAttributeModifier("class", "current"));
		//l.setRenderBodyOnly(true);
		l.setEnabled(false);
	}
	
	
	
}
