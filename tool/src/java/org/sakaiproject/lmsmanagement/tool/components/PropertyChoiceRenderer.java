package org.sakaiproject.lmsmanagement.tool.components;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 * Simple property-based ChoiceRenderer.
 * Use the given property names as the source to obtain the
 * respective ID and LABEL strings used to render the Choice
 * to the Hosting component.
 *
 * Example:
 * 
 *   //somewhere, in SomePersistentData.java file...
 *   public class SomePersistentData implements Serializable {
 *      private Integer id;
 *      private String  description;
 *      //plus, getters and setters....
 *   }
 *
 *   //extract Objects from DB
 *   List values = myService.findDataElements();
 *
 *   add( new DropDownChoice( "myWicketId",
 *            new PropertyModel(myDataObject, "propertyName" ),
 *            values,
 *            new PropertyChoiceRenderer( "id", "description" ) ))
 * 
 * @author Davide Alberto Molin (davide.molin@gmail.com)
 * @see http://developme.wordpress.com/2010/03/24/simple-wicket-choicerenderer-for-any-kind-of-object/
 */
public class PropertyChoiceRenderer implements IChoiceRenderer {

    //name of the property used to extract the ID for the choice option
    private String idProperty;
    //name of the property used to extract the LABEL for the choice option
    private String valueProperty;

    public PropertyChoiceRenderer(String idProperty, String valueProperty) {
        this.idProperty = idProperty;
        this.valueProperty = valueProperty;
    }

    @Override
    public Object getDisplayValue(Object object) {
        return getPropertyValue(object, valueProperty);
    }

    @Override
    public String getIdValue(Object object, int index) {
        return getPropertyValue(object, idProperty).toString();
    }

    private Object getPropertyValue(Object object, String property) {
        try {
            return BeanUtils.getProperty(object, property);
        }
        catch(Exception err) {
            //in case of exception, fall back to simple toString...
            return object.toString();
        }
    }
}
