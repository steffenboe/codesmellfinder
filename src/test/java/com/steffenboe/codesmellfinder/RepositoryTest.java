package com.steffenboe.codesmellfinder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.nio.file.Files;

import org.junit.jupiter.api.Test;

class RepositoryTest {

    @Test
    void shouldFindCodeSmell() {
        GitRepository repository = new GitRepository("library",
                "https://github.com/steffenb91/library.git");
        assertThat(repository.find().size(), greaterThan(0));
        
    }
}
