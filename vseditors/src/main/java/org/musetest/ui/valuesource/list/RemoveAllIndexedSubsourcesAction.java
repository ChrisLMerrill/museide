package org.musetest.ui.valuesource.list;

import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class RemoveAllIndexedSubsourcesAction extends UndoableAction
    {
    public RemoveAllIndexedSubsourcesAction(ValueSourceConfiguration source)
        {
        _source = source;
        }

    @Override
    protected boolean executeImplementation()
        {
        _removed_list = _source.getSourceList();
        _source.setSourceList(null);
        return true;
        }

    @Override
    protected boolean undoImplementation()
        {
        _source.setSourceList(_removed_list);
        return true;
        }

    private ValueSourceConfiguration _source;
    private List<ValueSourceConfiguration> _removed_list;
    }


