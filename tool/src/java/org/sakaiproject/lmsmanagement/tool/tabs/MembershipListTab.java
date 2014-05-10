package org.sakaiproject.lmsmanagement.tool.tabs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.EmptyPanel;
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
import org.sakaiproject.lmsmanagement.tool.panels.ShowMembersPanel;
import org.sakaiproject.site.api.Site;

/**
 * Panel that lists all sites, the count of members and allows search
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class MembershipListTab extends Panel {

	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.SakaiProxy")
	private SakaiProxy sakaiProxy;
	
	private final int MAX_ITEMS_PER_PAGE = 20;
	private SitesDataProvider provider;	
	
	private String search;
	private String view;
	
	public MembershipListTab(String id, PageParameters params) {
		super(id);
		
		//if we have a search string in the params, set it up
		if(params != null) {
			search = params.getString("search");
			view = params.getString("view");
		}

		//form
		add(new SearchForm("form"));
		
		//get list of sites from Sakai, wrapped in a dataprovider
		provider = new SitesDataProvider(search);
		
		//present the data in a table
		final DataView<Site> dataView = new DataView<Site>("data", provider) {

			@Override
			public void populateItem(final Item item) {
                final Site s = (Site) item.getModelObject();
                
                item.add(new Label("title", s.getTitle()));
                
                item.add(new Label("siteId", s.getId()));
                
                //link to view members
                Form edit = new Form("form");
                edit.add(new ViewMembersLink("viewMembers", s.getId()).add(new Label("members", String.valueOf(sakaiProxy.getMemberCount(s)))));
                item.add(edit);
                
                item.add(new Label("type", s.getType()));

                //panel for viewing members, only if matches the site that we are printing
                if(StringUtils.isNotBlank(view) && StringUtils.equals(s.getId(), view)) {
                	item.add(new ShowMembersPanel("memberList", view));
                } else {
                	item.add(new EmptyPanel("memberList"));
                }

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
	private class SitesDataProvider implements IDataProvider<Site> {
	   
		private List<Site> list;
		private String search;
		
		public SitesDataProvider(String search) {
			this.search = search;
		}

		private List<Site> getData() {
			if(list == null) {
				//get list of sites with optional search
				list = sakaiProxy.getSites(search);
			}
			return list;
		}
		
		
		@Override
		public Iterator<Site> iterator(int first, int count){
			return getData().subList(first, first + count).iterator();
		}

		@Override
		public int size(){
			return getData().size();
		}

		@Override
		public IModel<Site> model(Site object){
			return new DetachableSiteModel(object);
		}

		@Override
		public void detach(){
			list = null;
		}
	}
	
	/**
	 * Detachable model to wrap a Site
	 * 
	 */
	private class DetachableSiteModel extends LoadableDetachableModel<Site>{

		private final String siteId;
		
		/**
		 * @param s Site
		 */
		public DetachableSiteModel(Site s){
			this.siteId = s.getId();
		}
		
		/**
		 * @see org.apache.wicket.model.LoadableDetachableModel#load()
		 */
		protected Site load(){
			
			// get the user
			return sakaiProxy.getSite(siteId);
		}
	}
	
	/**
	 * For for searching sites
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
                    setResponsePage(new MembershipPage());
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
				setResponsePage(new MembershipPage(new PageParameters(params), 0));
			}
			
		}
		
	}
	
	//link for viewing the list of members
	private class ViewMembersLink extends SubmitLink {
	
		private String siteId;
		
		public ViewMembersLink(String id, String siteId) {
			super(id);
			this.siteId=siteId;
		}
		
		@Override
		public void onSubmit() {
			
			System.out.println("siteId: " + siteId);
			
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("view", siteId);
			
			//refresh page with the data
			setResponsePage(new MembershipPage(new PageParameters(params), 0));
			
		}

	
		
	}

	
	
}
	
	
	