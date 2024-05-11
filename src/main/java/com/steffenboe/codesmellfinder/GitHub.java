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

class GitHub {

    private RestClient restClient;
    private static final Logger LOG = LoggerFactory.getLogger(GitHub.class);

    GitHub(RestClient restClient) {
        this.restClient = restClient;
    }

    private static final String GITHUB_PAT = "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";

    Optional<GitRepository> findRandom() throws IOException {
        LOG.info("Starting repository scan...");
        String url = buildSearchUrl();
        Request request = buildRequest(url);
        JSONArray items = getResponse(restClient, request);
        List<GitRepository> repositories = getRepositoriesFromResponse(items);
        for (GitRepository gitRepository : repositories) {
            LOG.info("Scanning repo {} for codesmells...", gitRepository.name());
            if (gitRepository.find().size() > 0) {
                LOG.info("Found repo with code smells: {}", gitRepository.name());
                return Optional.of(gitRepository);
            }
        }
        return Optional.empty();

    }

    private List<GitRepository> getRepositoriesFromResponse(JSONArray items) {
        List<GitRepository> repositories = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            addRepositoryFromResponse(items, repositories, i);
        }
        return repositories;
    }

    private void addRepositoryFromResponse(JSONArray items, List<GitRepository> repositories, int i) {
        JSONObject repository = items.getJSONObject(i);
        repositories.add(new GitRepository(repository.getString("name"), repository.getString("clone_url")));
    }

    private JSONArray getResponse(RestClient client, Request request) throws IOException {
        String response = client.execute(request);
        LOG.debug("Calling {}", request.urlString());
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray items = jsonResponse.getJSONArray("items");
        return items;
    }

    private Request buildRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "token " + GITHUB_PAT)
                .build();
        return request;
    }

    private String buildSearchUrl() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.github.com/search/repositories").newBuilder();
        urlBuilder.addQueryParameter("q", "language:java");
        urlBuilder.addQueryParameter("per_page", "10");
        return urlBuilder.build().toString();
    }
}
