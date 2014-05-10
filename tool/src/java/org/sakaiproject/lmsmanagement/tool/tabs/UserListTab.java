package org.sakaiproject.lmsmanagement.tool.tabs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.DefaultItemReuseStrategy;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.lmsmanagement.logic.SakaiProxy;
import org.sakaiproject.lmsmanagement.tool.models.SearchModel;
import org.sakaiproject.lmsmanagement.tool.pages.MembershipPage;
import org.sakaiproject.lmsmanagement.tool.pages.UsersPage;
import org.sakaiproject.user.api.User;

/**
 * Panel that lists all users and allows search
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class UserListTab extends Panel {

	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.SakaiProxy")
	private SakaiProxy sakaiProxy;
	
	private final int MAX_ITEMS_PER_PAGE = 20;
	private UsersDataProvider provider;	
	
	private String search;
	
	public UserListTab(String id, PageParameters params) {
		super(id);
		
		//if we have a search string in the params, set it up
		if(params != null) {
			search = params.getString("search");
		}

		//form
		add(new SearchForm("form"));
		
		//get list of users from Sakai, wrapped in a dataprovider
		provider = new UsersDataProvider(search);
		
		//present the data in a table
		final DataView<User> dataView = new DataView<User>("data", provider) {

			@Override
			public void populateItem(final Item item) {
                final User u = (User) item.getModelObject();
                
                //link to edit
                Form edit = new Form("edit");
                edit.add(new EditLink("editEid", u.getId()).add(new Label("eid", u.getEid())));
                item.add(edit);
                
                
                item.add(new Label("name", u.getDisplayName()));
                item.add(new Label("email", u.getEmail()));
                item.add(new Label("type", u.getType()));
                item.add(new Label("uuid", u.getId()));

            }
        };
        dataView.setItemReuseStrategy(new DefaultItemReuseStrategy());
        dataView.setItemsPerPage(MAX_ITEMS_PER_PAGE);
        add(dataView);

        //add a pager to our table, only visible if we have more than 5 items
        add(new PagingNavigator("navigator", dataView) {
        	
        	@Override
        	public boolean isVisible() {
        		if(provider.size() > MAX_ITEMS_PER_PAGE) {
        			return true;
        		}
        		return false;
        	}

        });
        
        
	}

	
	/**
	 * DataProvider to manage our list
	 * 
	 */
	private class UsersDataProvider implements IDataProvider<User> {
	   
		private List<User> list;
		private String search;
		
		public UsersDataProvider(String search) {
			this.search = search;
		}

		private List<User> getData() {
			if(list == null) {
				if(StringUtils.isNotBlank(search)) {
					//filter results
					list = sakaiProxy.searchUsers(search);
				} else {
					//get all users
					list = sakaiProxy.getUsers();
				}
			}
			return list;
		}
		
		
		@Override
		public Iterator<User> iterator(int first, int count){
			return getData().subList(first, first + count).iterator();
		}

		@Override
		public int size(){
			return getData().size();
		}

		@Override
		public IModel<User> model(User object){
			return new DetachableUserModel(object);
		}

		@Override
		public void detach(){
			list = null;
		}
	}
	
	/**
	 * Detachable model to wrap a User
	 * 
	 */
	private class DetachableUserModel extends LoadableDetachableModel<User>{

		private final String uuid;
		
		/**
		 * @param u - User
		 */
		public DetachableUserModel(User u){
			this.uuid = u.getId();
		}
		
		/**
		 * @see org.apache.wicket.model.LoadableDetachableModel#load()
		 */
		protected User load(){
			
			// get the user
			return sakaiProxy.getUser(uuid);
		}
	}
	
	/**
	 * For for searching users
	 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
	 *
	 */
	private class SearchForm extends Form<SearchModel> {
		
		public SearchForm(String id){
			super(id);
			
			SearchModel searchModel = new SearchModel();
			
			//if we already have a term, set it into the model
			if(StringUtils.isNotBlank(search)){
				searchModel.setSearch(search);
			}
			setDefaultModel(new CompoundPropertyModel<SearchModel>(searchModel));
			
			add(new TextField<String>("search"));
			
			//reset link
			add(new Link("reset") {
                @Override
                public void onClick() {
                    setResponsePage(new UsersPage());
                }
                
                @Override
                public boolean isVisible() {
                	//only show if there is a search term
                	if(StringUtils.isNotBlank(search)){
                		return true;
                	}
                	return false;
                }
                
            });
			
		}
		
		public void onSubmit(){
			SearchModel model = getModelObject();
			String search = model.getSearch();
			
			if(StringUtils.isNotBlank(search)) {
				
				Map<String,Object> params = new HashMap<String,Object>();
				params.put("search", search);
				
				//refresh page with the data
				setResponsePage(new UsersPage(new PageParameters(params), 0));
			}
			
		}
		
	}
	
	//link for editing a user
	private class EditLink extends SubmitLink {
	
		private String uuid;
		
		public EditLink(String id, String uuid) {
			super(id);
			this.uuid=uuid;
		}
		
		@Override
		public void onSubmit() {
			
			System.out.println("uuid: " + uuid);
			
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("edit", uuid);
			
			//refresh page with the data
			setResponsePage(new UsersPage(new PageParameters(params), 1));
			
		}

	
		
	}

	
	
}
	
	
	