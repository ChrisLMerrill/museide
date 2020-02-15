package org.museautomation.ui.valuesource;

import org.museautomation.ui.valuesource.map.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface NamedValueSourceEditor extends ValueSourceEditor
    {
    void setName(String name);
    String getName();

    void addNameChangeListener(NameChangeListener listener);
    void removeNameChangeListener(NameChangeListener listener);

    void setNameValidator(SubsourceNameValidator validator);
    }

