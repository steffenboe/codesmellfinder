package com.steffenboe.codesmellfinder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import org.junit.jupiter.api.Test;

class RepositoryTest {

    @Test
    void shouldFindCodeSmell() {
        GitRepository repository = new GitRepository("library",
                "https://github.com/steffenb91/library.git", new PMDStaticCodeAnalyzer());
        assertThat(repository.findAndScan().size(), greaterThan(0));

    }
}
