package org.musetest.ui.valuesource;

import org.musetest.core.values.*;

/**
 * Listen for changes to a ValueSourceEditor
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface ValueSourceEditorListener
    {
    void sourceChanged(ValueSourceEditor editor, ValueSourceConfiguration old_value, ValueSourceConfiguration new_value);
    }


