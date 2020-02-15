package org.museautomation.ui.extend.edit;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface ValidationStateSource extends Validatable
    {
    void addValidationStateListener(ValidationStateListener listener);
    void removeValidationStateListener(ValidationStateListener listener);
    }