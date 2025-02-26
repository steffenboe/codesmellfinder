package com.steffenboe.codesmellfinder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    void shouldFindRandomProject() throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total_count", 2938);
        jsonObject.put("incomplete_results", true);
        List<JSONObject> items = new ArrayList<>();
        items.add(new JSONObject().put("name", "testrepo"));
        items.add(new JSONObject().put("clone_url", "testrepo.git"));
        jsonObject.put("items", new JSONArray(items));
        when(restClient.execute(any())).thenReturn(jsonObject.toString());
        RepositoryScanner github = new RepositoryScanner(restClient, new PMDStaticCodeAnalyzer());
        Optional<GitRepository> repository = github.findRandomWithRuleViolations();
        assertThat(repository.get().name(), not(blankOrNullString()));
    }
}
