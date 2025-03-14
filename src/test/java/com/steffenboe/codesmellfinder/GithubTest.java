package com.steffenboe.codesmellfinder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GithubTest {

    @Mock
    private RestClient restClient;

    @Test
    void shouldFindRandomProjectWithLanguage() throws IOException {
        when(restClient.execute(any())).thenReturn(mockItem().toString());
        GitHub github = new GitHub(restClient, new PMDStaticCodeAnalyzer());
        List<GitRepository> repositories = github.findRandomGitRepository("java");
        assertThat(repositories, not(empty()));
    }

    @Test
    void shouldNotFindRandomProjectWithNonMatchingLanguage() throws IOException {
        when(restClient.execute(any())).thenReturn(mockItem().toString());
        GitHub github = new GitHub(restClient, new PMDStaticCodeAnalyzer());
        List<GitRepository> repositories = github.findRandomGitRepository("javascript");
        assertThat(repositories, empty());
    }

    private JSONObject mockItem() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total_count", 2938);
        jsonObject.put("incomplete_results", true);
        List<JSONObject> items = new ArrayList<>();
        items.add(new JSONObject().put("name", "testrepo")
                .put("clone_url", "testrepo.git")
                .put("language", "Java"));
        jsonObject.put("items", new JSONArray(items));
        return jsonObject;
    }
}
