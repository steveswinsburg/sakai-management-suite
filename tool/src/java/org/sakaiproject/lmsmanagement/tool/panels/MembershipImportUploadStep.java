package org.sakaiproject.lmsmanagement.tool.panels;

import java.io.IOException;
import java.util.List;

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
import org.sakaiproject.lmsmanagement.model.ImportedMember;

/**
 * Panel to handle file upload and processing
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class MembershipImportUploadStep extends Panel {

	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.SakaiProxy")
	private SakaiProxy sakaiProxy;
	
	@SpringBean(name="org.sakaiproject.lmsmanagement.logic.ProjectLogic")
	private ProjectLogic projectLogic;
	
	private String panelId;
	
	/*
	 * Constructor
	 */
	public MembershipImportUploadStep(String id) {
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
            		List<ImportedMember> importedMembers = projectLogic.parseImportedMemberFile(upload.getInputStream(), upload.getContentType());
					
            		//if null, the file was of the incorrect type
            		//if empty there are no users
            		if(importedMembers == null || importedMembers.isEmpty()) {
            			error(getString("error.parse.upload"));
            		} else {
            			//GO TO NEXT PAGE
                		
                		//repaint panel
    					Component newPanel = new MembershipImportConfirmationStep(panelId, importedMembers);
    					newPanel.setOutputMarkupId(true);
    					MembershipImportUploadStep.this.replaceWith(newPanel);
    					
            		}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
         
            }
          
        }
		
	}
	
	
	
}
