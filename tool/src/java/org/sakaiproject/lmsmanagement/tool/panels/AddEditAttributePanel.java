package org.sakaiproject.lmsmanagement.tool.panels;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.lmsmanagement.logic.ProjectLogic;
import org.sakaiproject.lmsmanagement.model.AdditionalAttribute;

/**
 * Panel with form to either edit or add a new attribute
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class AddEditAttributePanel extends Panel {

	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.ProjectLogic")
	protected ProjectLogic projectLogic;
	
	/**
	 * Constructor called when adding a new attribute
	 * @param id	component id
	 */
	public AddEditAttributePanel(String id){
		super(id);
		
		add(new AttributeForm("form", new AdditionalAttribute()));
	}
	
	/**
	 * Constructor called when editing an existing attribute.
	 * @param id		component id
	 * @param attributeId	attribute id
	 */
	public AddEditAttributePanel(String id, long attributeId){
		super(id);
		
		AdditionalAttribute a = projectLogic.getAdditionalAttribute(attributeId);
		add(new AttributeForm("form", a));
	}
	
	
	/**
	 * Form for adding a new Attribute. It is automatically linked up if the form fields match the object fields
	 */
	private class AttributeForm extends Form<AdditionalAttribute> {
	   
		public AttributeForm(String id, AdditionalAttribute attribute) {
	        super(id, new CompoundPropertyModel<AdditionalAttribute>(attribute));
	        add(new TextField<String>("key").setOutputMarkupId(true));
	        add(new TextField<String>("value").setOutputMarkupId(true));
	    }
		
		@Override
        public void onSubmit(){
			AdditionalAttribute a = (AdditionalAttribute)getDefaultModelObject();
			
			if(projectLogic.addOrUpdateAdditionalAttribute(a)){
				info("Attribute added/updated successfully.");
				
				this.setDefaultModel(new CompoundPropertyModel<AdditionalAttribute>(new AdditionalAttribute()));
				
			} else {
				error("Error adding/updating attribute.");
			}
			
        }
	}
	
}
