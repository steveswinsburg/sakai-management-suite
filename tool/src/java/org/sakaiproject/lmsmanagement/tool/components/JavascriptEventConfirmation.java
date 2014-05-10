package org.sakaiproject.lmsmanagement.tool.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.Model;

/**
 * Class to add a confirm javascript window as needed.
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class JavascriptEventConfirmation extends AttributeModifier {
	public JavascriptEventConfirmation(String event, String msg) {
		super(event, new Model<String>(msg));
	}

	protected String newValue(final String currentValue, final String replacementValue) {
		String prefix = "var conf = confirm('" + replacementValue + "'); " + "if (!conf) return false; ";
		String result = prefix;
		if (currentValue != null) {
			result = prefix + currentValue;
		}
		return result;
	}
}
