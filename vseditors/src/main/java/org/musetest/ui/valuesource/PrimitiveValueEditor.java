package org.musetest.ui.valuesource;

import javafx.scene.*;
import org.musetest.ui.extend.edit.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface PrimitiveValueEditor extends ValidationStateSource
    {
    Node getNode();

    Object getValue();

    void setValue(Object value);

    boolean isValid();

    void addValidationStateListener(ValidationStateListener listener);

    void removeValidationStateListener(ValidationStateListener listener);

    void setChangeListener(PrimitiveValueEditorField.ChangeListener listener);
    }

