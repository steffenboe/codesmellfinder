package com.steffenboe.codesmellfinder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
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
        Optional<GitRepository> repository = githubScanner.findRandomWithRuleViolations("java");
        assertThat(repository.isPresent(), is(true));
    }
}
