package org.sakaiproject.lmsmanagement.tool.pages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.repeater.DefaultItemReuseStrategy;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.sakaiproject.lmsmanagement.model.AdditionalAttribute;
import org.sakaiproject.lmsmanagement.tool.components.JavascriptEventConfirmation;
import org.sakaiproject.lmsmanagement.tool.panels.AddEditAttributePanel;

/**
 * Page for managing user attributes
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class AttributesPage extends BasePage {

	private PageParameters params;
	private AttributesDataProvider provider;
	private Long editId;
	
	public AttributesPage(PageParameters params) {
		this.params=params;
		
		doRender();
	}
	
	public AttributesPage() {
		this(null);
	}

	private void doRender() {
		disableLink(attributesLink);
		
		//if we have the edit Id string in the params, set it up
		if(params != null) {
			editId = params.getLong("edit");
		}
		
		//get list of attributes from db, wrapped in a dataprovider
		provider = new AttributesDataProvider();		
		
		//present the data in a table
		final DataView<AdditionalAttribute> dataView = new DataView<AdditionalAttribute>("data", provider) {

			@Override
			public void populateItem(final Item item) {
                final AdditionalAttribute a = (AdditionalAttribute) item.getModelObject();
                
                //link to edit the attribute (by the key)
                Form edit = new Form("edit");
                edit.add(new EditLink("editAttribute", a.getId()).add(new Label("key", a.getKey())));
                item.add(edit);
               
                item.add(new Label("value", a.getValue()));
                
                //link to remove the attribute, with confirmation
                Form remove = new Form("remove");
                RemoveLink removeLink = new RemoveLink("removeAttribute", a.getId());
                removeLink.add(new JavascriptEventConfirmation("onclick", getString("action.confirm.remove.attribute")));
                remove.add(removeLink);
                item.add(remove);
                                               
            }
        };
        dataView.setItemReuseStrategy(new DefaultItemReuseStrategy());
        add(dataView);
                
        //add our form
        if(editId == null) {
        	add(new AddEditAttributePanel("addEditAttributePanel"));
        } else {
        	add(new AddEditAttributePanel("addEditAttributePanel", editId));
        }
       

        
	}
	
	
	
	/**
	 * DataProvider to manage our list of attributes
	 * 
	 */
	private class AttributesDataProvider implements IDataProvider<AdditionalAttribute> {
	   
		private List<AdditionalAttribute> list;
		
		private List<AdditionalAttribute> getData() {
			if(list == null) {
				list = projectLogic.getAdditionalAttributes();
			}
			return list;
		}
		
		@Override
		public Iterator<AdditionalAttribute> iterator(int first, int count){
			return getData().subList(first, first + count).iterator();
		}

		@Override
		public int size(){
			List<AdditionalAttribute> l = getData();
			if(l==null) {
				return 0;
			}
			return l.size();
		}

		@Override
		public IModel<AdditionalAttribute> model(AdditionalAttribute object){
			return new DetachableAttributeModel(object);
		}

		@Override
		public void detach(){
			list = null;
		}
	}
	
	/**
	 * Detachable model to wrap an Attribute
	 * 
	 */
	private class DetachableAttributeModel extends LoadableDetachableModel<AdditionalAttribute>{

		private final long id;
		
		/**
		 * @param a
		 */
		public DetachableAttributeModel(AdditionalAttribute a){
			this.id = a.getId();
		}
		
		/**
		 * @param id
		 */
		public DetachableAttributeModel(long id){
			this.id = id;
		}
		
		/**
		 * @see org.apache.wicket.model.LoadableDetachableModel#load()
		 */
		protected AdditionalAttribute load(){
			
			// get the attribute
			return projectLogic.getAdditionalAttribute(id);
		}
	}
	
	//link for editing an attribute
	private class EditLink extends SubmitLink {
	
		private long attributeId;
		
		public EditLink(String id, long attributeId) {
			super(id);
			this.attributeId=attributeId;
		}
		
		@Override
		public void onSubmit() {
						
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("edit", attributeId);
			
			//refresh page with the data
			setResponsePage(new AttributesPage(new PageParameters(params)));
		}
	}
	
	//link for removing an attribute
	private class RemoveLink extends SubmitLink {
	
		private long attributeId;
		
		public RemoveLink(String id, long attributeId) {
			super(id);
			this.attributeId=attributeId;
		}
		
		@Override
		public void onSubmit() {
						
			if(projectLogic.deleteAdditionalAttribute(attributeId)){
				info("Attribute removed successfully.");
			} else {
				error("Error removing attribute.");
			}
			
			
			//refresh page
			setResponsePage(new AttributesPage());
		}
	}
}
