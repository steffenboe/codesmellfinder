package com.steffenboe.codesmellfinder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.squareup.okhttp.OkHttpClient;

class CodeSmellFinderTest {

    @Test
    void shouldFindCodeSmellsFromRepository() throws IOException {
        GitHub githubScanner = new GitHub(
                new RestClient(
                        new OkHttpClient()),
                new PMDStaticCodeAnalyzer("src/main/resources/rulesets/custom-ruleset.xml"));
        CodeSmellScanner codeSmellScanner = new CodeSmellScanner();
        List<GitRepository> repositories = githubScanner.findRandomGitRepository("java");
        Optional<GitRepository> gitRepositories = codeSmellScanner.searchForCodeSmells(repositories);
        assertThat(gitRepositories.isPresent(), is(true));
    }
}
