package org.museautomation.ui.seideimport;

import org.museautomation.ui.ide.navigation.resources.actions.*;
import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.museautomation.ui.extend.actions.*;
import org.slf4j.*;

import java.io.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ImportSeleniumIdeTestsAction extends CompoundAction
    {
    public ImportSeleniumIdeTestsAction(ImportCandidates candidates, boolean delete_source_files, MuseProject project)
        {
        _candidates = candidates;
        _delete_source_files = delete_source_files;

        for (ImportCandidate candidate : candidates.allEnabledCandidates())
            {
            ResourceToken existing_token = project.getResourceStorage().findResource(candidate.getResourceId());
            if (existing_token == null)
                addAction(new AddResourceAction(candidate.getTest(), project));
            else
                {
                addAction(new DeleteResourceAction(existing_token, project));
                addAction(new AddResourceAction(candidate.getTest(), project));
                }
            }
        }

    public boolean isDeleteSourceFilesEnabled()
        {
        return _delete_source_files;
        }

    @Override
    protected boolean executeImplementation()
        {
        boolean success = super.executeImplementation();
        if (success && _delete_source_files)
            {
            for (ImportCandidate candidate : _candidates.allEnabledCandidates())
                {
                File file = candidate.getFile();
                boolean deleted = file.delete();
                if (!deleted)
                    LOG.error("Unable to delete file: " + file.getAbsolutePath());
                }
            }
        return success;
        }

    private final boolean _delete_source_files;
    private final ImportCandidates _candidates;

    private final static Logger LOG = LoggerFactory.getLogger(ImportSeleniumIdeTestsAction.class);
    }


