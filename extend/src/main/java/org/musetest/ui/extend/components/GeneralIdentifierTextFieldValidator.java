package org.musetest.ui.extend.components;

import javafx.scene.control.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class GeneralIdentifierTextFieldValidator
	{
	public GeneralIdentifierTextFieldValidator(TextField field)
		{
		_field = field;
		_field.textProperty().addListener((observable, old_value, new_value) ->
			InputValidation.setValid(_field, isValid(new_value)));
		}

	public static boolean isValid(String identifier)
		{
		boolean valid = true;
		for (int i = 0; i < identifier.length(); i++)
			{
			char c = identifier.charAt(i);
			if (!(Character.isLetterOrDigit(c)
				|| c == '-'
				|| c == '_'))
				valid = false;
			}
		return valid;
		}

	private final TextField _field;
	}


