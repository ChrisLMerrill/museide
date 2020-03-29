package org.museautomation.ui.seideimport;

import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.resource.storage.*;

import java.io.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class ImportCandidatesTests
    {
    @Test
    void candidates()
        {
        final String folder = "projects/import";
        File import_folder = TestResources.getFile(folder, getClass());
        File[] import_files = import_folder.listFiles((dir, name) -> (name.endsWith(".html")));
        if (import_files == null)
            Assertions.fail("no files to import");

        MuseProject project = new SimpleProject(new FolderIntoMemoryResourceStorage(TestResources.getFile(folder, getClass())));
        ImportCandidates candidates = ImportCandidates.build(project, import_files);

        Assertions.assertEquals(5, candidates.size());

        ImportCandidate import_ok = candidates.get("import-ok.html");
        Assertions.assertEquals(ImportCandidate.Status.Ready, import_ok.getStatus());

        ImportCandidate import_bad = candidates.get("import-unreadable.html");
        Assertions.assertEquals(ImportCandidate.Status.Fail, import_bad.getStatus());

        ImportCandidate import_duplicate = candidates.get("test1.html");
        Assertions.assertEquals(ImportCandidate.Status.DuplicateId, import_duplicate.getStatus());

        // Duplicate should take precedence over unrecognized commands
        ImportCandidate import_duplicate_and_unrecognized = candidates.get("test2.html");
        Assertions.assertEquals(ImportCandidate.Status.DuplicateId, import_duplicate_and_unrecognized.getStatus());

        ImportCandidate import_unrecognized = candidates.get("import-unrecognized.html");
        Assertions.assertEquals(ImportCandidate.Status.Warning, import_unrecognized.getStatus());
        Assertions.assertEquals(1, import_unrecognized.getUnrecognizedCommandCount());
        }

    @Test
    void sideFormat()
        {
        File import_folder = TestResources.getFile("projects/import", getClass());
        MuseProject project = new SimpleProject();
        ImportCandidates candidates = ImportCandidates.build(project, new File(import_folder, "import-new.side"));

        Assertions.assertEquals(ImportCandidate.Status.Ready, candidates.all().get(0).getStatus());
        }
    }