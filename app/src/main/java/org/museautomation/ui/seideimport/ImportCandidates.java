package org.museautomation.ui.seideimport;

import org.museautomation.core.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ImportCandidates
    {
    public static ImportCandidates build(MuseProject project, File... import_files)
        {
        ImportCandidates candidates = new ImportCandidates();
        for (File file : import_files)
        	if (file.getName().toLowerCase().endsWith(".html"))
                candidates.add(new ImportCandidate(file, project));
            else if (file.getName().toLowerCase().endsWith(".side"))
		        {
		        int index = 0;
		        boolean keep_going = true;
		        do
			        {
			        try
				        {
				        candidates.add(new ImportCandidate(file, index++, project));
				        }
			        catch (IllegalArgumentException e)
				        {
				        keep_going = false;
				        }
			        } while (keep_going);
		        }
        return candidates;
        }

    public ImportCandidate get(String filename)
        {
        for (ImportCandidate candidate : _candidates)
            if (candidate._file.getName().equals(filename))
                return candidate;
        return null;
        }

    private void add(ImportCandidate candidate)
        {
        _candidates.add(candidate);
        candidate.onChange(() ->
            {
            if (_listener != null)
                _listener.changed();
            });
        }

    public int size()
        {
        return _candidates.size();
        }

    public List<ImportCandidate> all()
        {
        return Collections.unmodifiableList(_candidates);
        }

    public List<ImportCandidate> allEnabledCandidates()
        {
        List<ImportCandidate> enabled = new ArrayList<>();
        for (ImportCandidate candidate : _candidates)
            if (candidate.getEnabled() != null && candidate.getEnabled())
                enabled.add(candidate);
        return enabled;
        }

    public int getEnabledCount()
        {
        int count = 0;
        for (ImportCandidate candidate : _candidates)
            {
            Boolean enabled = candidate.getEnabled();
            if (enabled != null && enabled)
                count++;
            }
        return count;
        }

    public void onChange(OnChangeListener listener)
        {
        _listener = listener;
        }

    public interface OnChangeListener
        {
        void changed();
        }

    private List<ImportCandidate> _candidates = new ArrayList<>();

    private transient OnChangeListener _listener;
    }


