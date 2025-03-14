package com.steffenboe.codesmellfinder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RepositoryTest {

    private static final String TMP_DIRECTORY = "./tmp";
    private static final String GIT_REPOSITORY_URL = "https://github.com/steffenb91/library.git";

    @BeforeEach
    void setUp() {
        cloneRepository();
    }

    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(FileUtils.getFile(TMP_DIRECTORY));
    }

    private void cloneRepository() {
        try {
            Git.cloneRepository()
                    .setURI(GIT_REPOSITORY_URL)
                    .setDirectory(new File(TMP_DIRECTORY))
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            fail("Failed to clone repository");
        }
    }

   

    @Test
    void shouldFindCodeSmell() {
        GitRepository repository = new GitRepository("library",
                GIT_REPOSITORY_URL, new PMDStaticCodeAnalyzer());
        assertThat(repository.scan(TMP_DIRECTORY).size(), greaterThan(0));
    }
}
