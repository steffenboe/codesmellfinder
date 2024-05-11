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
        GitHub githubScanner = new GitHub(new RestClient(new OkHttpClient()));
        Optional<GitRepository> repository = githubScanner.findRandom();
        assertThat(repository.isPresent(), is(true));
    }
}
