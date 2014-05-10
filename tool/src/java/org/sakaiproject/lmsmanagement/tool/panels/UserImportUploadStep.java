package org.sakaiproject.lmsmanagement.tool.panels;

import java.io.IOException;
import java.util.List;

import lombok.Getter;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.sakaiproject.lmsmanagement.logic.ProjectLogic;
import org.sakaiproject.lmsmanagement.logic.SakaiProxy;
import org.sakaiproject.lmsmanagement.model.ImportedUser;

/**
 * Panel to handle file upload and processing
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class UserImportUploadStep extends Panel {

	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.SakaiProxy")
	private SakaiProxy sakaiProxy;
	
	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.ProjectLogic")
	private ProjectLogic projectLogic;
	
	private String panelId;
	
	
	
	/*
	 * Constructor
	 */
	public UserImportUploadStep(String id) {
		super(id);
		this.panelId=id;
		
		add(new UploadForm("form"));
	
	}

	/*
	 * Upload form
	 */
	private class UploadForm extends Form<Void> {
		
		FileUploadField fileUploadField;
		
		public UploadForm(String id) {
			super(id);
			
			setMultiPart(true);
			setMaxSize(Bytes.megabytes(2));
			
			fileUploadField = new FileUploadField("upload");
			add(fileUploadField);
		}
			
		@Override
        public void onSubmit(){
						
			FileUpload upload = fileUploadField.getFileUpload();
            if (upload != null) {
                
            	try {
					//turn file into list
            		List<ImportedUser> importedUsers = projectLogic.parseImportedUserFile(upload.getInputStream(), upload.getContentType());
					
            		//if null, the file was of the incorrect type
            		//if empty there are no users
            		if(importedUsers == null || importedUsers.isEmpty()) {
            			error(getString("error.parse.upload"));
            		} else {
            			//GO TO NEXT PAGE
                		System.out.println(importedUsers.size());
                		
                		//repaint panel
    					Component newPanel = new UserImportConfirmationStep(panelId, importedUsers);
    					newPanel.setOutputMarkupId(true);
    					UserImportUploadStep.this.replaceWith(newPanel);
    					/*
    					if(target != null) {
    						target.addComponent(newPanel);
    						//resize iframe
    						target.appendJavascript("setMainFrameHeight(window.name);");
    					}
                		*/
                		
                		
                		

            		}
            		
					
				} catch (IOException e) {
					e.printStackTrace();
				}
         
            }
          
        }
		
	}
	
	
	
}
