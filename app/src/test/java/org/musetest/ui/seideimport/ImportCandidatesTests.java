package org.musetest.ui.seideimport;

import net.christophermerrill.testfx.*;
import org.junit.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.resource.storage.*;
import org.musetest.ui.seideimport.*;

import java.io.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ImportCandidatesTests
    {
    @Test
    public void candidates()
        {
        final String folder = "projects/import";
        File import_folder = TestResources.getFile(folder, getClass());
        File[] import_files = import_folder.listFiles((dir, name) -> (name.endsWith(".html")));

        MuseProject project = new SimpleProject(new FolderIntoMemoryResourceStorage(TestResources.getFile(folder, getClass())));
        ImportCandidates candidates = ImportCandidates.build(project, import_files);

        Assert.assertEquals(5, candidates.size());

        ImportCandidate import_ok = candidates.get("import-ok.html");
        Assert.assertEquals(ImportCandidate.Status.Ready, import_ok.getStatus());

        ImportCandidate import_bad = candidates.get("import-unreadable.html");
        Assert.assertEquals(ImportCandidate.Status.Fail, import_bad.getStatus());

        ImportCandidate import_duplicate = candidates.get("test1.html");
        Assert.assertEquals(ImportCandidate.Status.DuplicateId, import_duplicate.getStatus());

        // Duplicate should take precedence over unrecognized commands
        ImportCandidate import_duplicate_and_unrecognized = candidates.get("test2.html");
        Assert.assertEquals(ImportCandidate.Status.DuplicateId, import_duplicate_and_unrecognized.getStatus());

        ImportCandidate import_unrecognized = candidates.get("import-unrecognized.html");
        Assert.assertEquals(ImportCandidate.Status.Warning, import_unrecognized.getStatus());
        Assert.assertEquals(1, import_unrecognized.getUnrecognizedCommandCount());
        }

    @Test
    public void sideFormat()
        {
        File import_folder = TestResources.getFile("projects/import", getClass());
        MuseProject project = new SimpleProject();
        ImportCandidates candidates = ImportCandidates.build(project, new File(import_folder, "import-new.side"));

        Assert.assertEquals(ImportCandidate.Status.Ready, candidates.all().get(0).getStatus());
        }
    }


