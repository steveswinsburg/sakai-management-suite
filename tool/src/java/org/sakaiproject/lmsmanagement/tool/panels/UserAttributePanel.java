package org.sakaiproject.lmsmanagement.tool.panels;


import java.util.List;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.lmsmanagement.logic.ProjectLogic;
import org.sakaiproject.lmsmanagement.model.AdditionalAttribute;
import org.sakaiproject.lmsmanagement.model.ImportedUser;
import org.sakaiproject.lmsmanagement.tool.LMSManagementApp;

/**
 * Panel to take care of rendering the label and textfield for each attribute
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class UserAttributePanel extends Panel {	
	
	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.ProjectLogic")
	private ProjectLogic logic;
	
	private List<AdditionalAttribute> allAttributes;
		
	public UserAttributePanel(String id, final ImportedUser user) {
		super(id);
		
		//get a list of all attributes in the db
		allAttributes = logic.getAdditionalAttributes();
				
		//display a row for each attribute
		final ListView<AdditionalAttribute> attributeList = new ListView<AdditionalAttribute>("attributes", allAttributes) {

			@Override
			protected void populateItem(ListItem<AdditionalAttribute> item) {
				final AdditionalAttribute a = (AdditionalAttribute) item.getModelObject();
	            
                item.add(new Label("label", a.getValue()));
               
    			TextField tf = new TextField<String>("attribute", new Model<String>(getAttributeValue(user, a.getKey()))) {
    				
    				@Override
    				protected void onComponentTag(final ComponentTag tag){
    					super.onComponentTag(tag);
    					tag.put("name", LMSManagementApp.ATTR_PREFIX + a.getKey());
    					//makes a name of attribute_blah so we can lookup all attributes that start with attribute_ in the request
    				}
    				
    			};
    			tf.setOutputMarkupId(true);
    			item.add(tf);
				
			}
        };
        add(attributeList);
		
	}
	
	
	
	/**
	 * Helper to get an attribute, or handle nulls
	 * @param user
	 * @param attributeName
	 * @return
	 */
	private String getAttributeValue(ImportedUser user, String attributeName){
		try {
			return user.getProperties().getProperty(attributeName);
		} catch (Exception e) {
			return null;
		}
	}
	

}
