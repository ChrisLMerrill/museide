package org.musetest.ui.editors.driver;

import org.musetest.selenium.providers.*;
import org.musetest.ui.extend.actions.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ChangeLocalDriverProviderPath extends UndoableAction
    {
    public ChangeLocalDriverProviderPath(BaseLocalDriverProvider provider, String path, LocalBinaryDriverProviderEditor.PathType path_type)
        {
        _provider = provider;
        _new_path = path;
        _new_path_type = path_type;
        }

    @Override
    protected boolean undoImplementation()
        {
        _provider.setAbsolutePath(_old_absolute_path);
        _provider.setRelativePath(_old_relative_path);
        return true;
        }

    @Override
    protected boolean executeImplementation()
        {
        _old_absolute_path = _provider.getAbsolutePath();
        _old_relative_path = _provider.getRelativePath();

        if (LocalBinaryDriverProviderEditor.PathType.ABSOLUTE.equals(_new_path_type))
            {
            _provider.setAbsolutePath(_new_path);
            _provider.setRelativePath(null);
            }
        else
            {
            _provider.setAbsolutePath(null);
            _provider.setRelativePath(_new_path);
            }
        return true;
        }

    private BaseLocalDriverProvider _provider;
    private String _new_path;
    private LocalBinaryDriverProviderEditor.PathType _new_path_type;
    private String _old_relative_path;
    private String _old_absolute_path;
    }


