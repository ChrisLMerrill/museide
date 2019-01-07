package org.musetest.ui.valuesource.actions;

import org.musetest.core.values.*;
import org.musetest.ui.extend.actions.*;
import org.musetest.ui.valuesource.list.*;
import org.musetest.ui.valuesource.map.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class MergeValueSourceAction extends CompoundAction
    {
    public MergeValueSourceAction(ValueSourceConfiguration source, ValueSourceConfiguration target)
        {
        _source = source;
        _target = target;
        }

    @Override
    protected boolean executeImplementation()
        {
        if (getSize() == 0)
            {
            if (!Objects.equals(_source.getType(), _target.getType()))
                addAction(new ChangeSourceTypeAction(_target, _source.getType()));

            if (!Objects.equals(_source.getValue(), _target.getValue()))
                addAction(new ChangeSourceValueAction(_target, _source.getValue()));

            if (!Objects.equals(_source.getSource(), _target.getSource()))
                addAction(new ChangeSourceSubsourceAction(_target, _source.getSource()));

            if (!Objects.equals(_source.getSourceList(), _target.getSourceList()))
                {
                if (_target.getSourceList() == null || _target.getSourceList().size() == 0)
                    {
                    // existing list is empty, add all from existing list
                    for (int i = 0; i < _source.getSourceList().size(); i++)
                        addAction(new AddIndexedSubsourceAction(_target, i, _source.getSource(i)));
                    }
                else if (_source.getSourceList() == null || _source.getSourceList().size() == 0)
                    {
                    // new list is empty, remove all from existing list
                    for (int i = _target.getSourceList().size() - 1; i >= 0; i--)
                        addAction(new RemoveIndexedSubsourceAction(_target, i));
                    }
                else // need to merge
                    {
                    // first, replace up to the length of the shorter list
                    int max = Math.min(_source.getSourceList().size(), _target.getSourceList().size());
                    for (int i = 0; i < max; i++)
                        if (!Objects.equals(_source.getSourceList().get(i), _target.getSourceList().get(i)))
                            addAction(new ReplaceIndexedSubsourceAction(_target, i, _source.getSourceList().get(i)));
                    if (_source.getSourceList().size() > _target.getSourceList().size())
                        {
                        // add the rest to the existing list
                        for (int i = max; i < _source.getSourceList().size(); i++)
                            addAction(new AddIndexedSubsourceAction(_target, i, _source.getSource(i)));
                        }
                    else if (_source.getSourceList().size() < _target.getSourceList().size())
                        {
                        // remove the rest from the existing list
                        for (int i = _target.getSourceList().size() - 1; i >= max; i--)
                            addAction(new RemoveIndexedSubsourceAction(_target, i));
                        }
                    }
                }

            if (!Objects.equals(_source.getSourceMap(), _target.getSourceMap()))
                for (String name : _source.getSourceNames())
                    if (_target.getSource(name) == null)
                        addAction(new AddNamedSubsourceAction(_target, _source.getSource(name), name));
                    else if (!Objects.equals(_target.getSource(name), _source.getSource(name)))
                        addAction(new ChangeNamedSubsourceAction(_target, name, _source.getSource(name)));
            }
        return super.executeImplementation();
        }

    private final ValueSourceConfiguration _source;
    private final ValueSourceConfiguration _target;
    }


