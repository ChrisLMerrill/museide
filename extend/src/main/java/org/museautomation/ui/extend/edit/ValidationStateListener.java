package org.museautomation.ui.extend.edit;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface ValidationStateListener
    {
    void validationStateChanged(ValidationStateSource source, boolean valid);
    }