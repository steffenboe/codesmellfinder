package com.steffenboe.codesmellfinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Request;

class RepositoryScanner {

    private static final Logger LOG = LoggerFactory.getLogger(RepositoryScanner.class);
    
    private final RestClient restClient;
    private final PMDStaticCodeAnalyzer pmdStaticCodeAnalyzer;

    RepositoryScanner(RestClient restClient, PMDStaticCodeAnalyzer pmdStaticCodeAnalyzer) {
        this.restClient = restClient;
        this.pmdStaticCodeAnalyzer = pmdStaticCodeAnalyzer;
    }

    /**
     * @return a {@link GitRepository} containing at least one Rule Violation, or empty
     *         if not found
     * @throws IOException
     */
    Optional<GitRepository> findRandomWithRuleViolations() throws IOException {
        LOG.info("Searching repositories...");
        String url = searchUrl();
        Request request = request(url);
        JSONArray items = response(request);
        List<GitRepository> gitRepositories = getRepositoriesFromResponse(items);
        return searchForCodeSmells(gitRepositories);
    }

    private Optional<GitRepository> searchForCodeSmells(List<GitRepository> repositories) {
        for (GitRepository gitRepository : repositories) {
            LOG.info("Scanning repo {} for rule violations...", gitRepository.name());
            if (gitRepository.scan().size() > 0) {
                LOG.info("Found repo with rule violations: {}", gitRepository.url());
                return Optional.of(gitRepository);
            }
        }
        return Optional.empty();
    }

    private List<GitRepository> getRepositoriesFromResponse(JSONArray items) {
        List<GitRepository> repositories = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            repositories.add(repositoryFromResponse(items, i));
        }
        return repositories;
    }

    private GitRepository repositoryFromResponse(JSONArray items, int i) {
        JSONObject repository = items.getJSONObject(i);
        return new GitRepository(repository.getString("name"), repository.getString("clone_url"), pmdStaticCodeAnalyzer);
    }

    private JSONArray response(Request request) throws IOException {
        String response = restClient.execute(request);
        LOG.debug("Calling {}", request.urlString());
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray items = jsonResponse.getJSONArray("items");
        return items;
    }

    private Request request(String url) {
        return new Request.Builder()
                .url(url)
                .build();
    }

    private String searchUrl() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.github.com/search/repositories").newBuilder();
        urlBuilder.addQueryParameter("q", "language:java");
        urlBuilder.addQueryParameter("per_page", "30");
        urlBuilder.addQueryParameter("sort", "stars");
        urlBuilder.addQueryParameter("order", "desc");
        int randomPage = (int) (Math.random() * 10) + 1;
        urlBuilder.addQueryParameter("page", String.valueOf(randomPage));
        return urlBuilder.build().toString();
    }
}
