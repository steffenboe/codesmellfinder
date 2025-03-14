package com.steffenboe.codesmellfinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Request;

class GitHub {

    private static final Logger LOG = LoggerFactory.getLogger(GitHub.class);

    private final RestClient restClient;
    private final PMDStaticCodeAnalyzer pmdStaticCodeAnalyzer;

    GitHub(RestClient restClient, PMDStaticCodeAnalyzer pmdStaticCodeAnalyzer) {
        this.restClient = restClient;
        this.pmdStaticCodeAnalyzer = pmdStaticCodeAnalyzer;
    }

    /**
     * @param string
     * @return a {@link GitRepository} containing at least one Rule Violation, or
     *         empty
     *         if not found
     * @throws IOException
     */
    List<GitRepository> findRandomGitRepository(String language) throws IOException {
        LOG.info("Searching repositories...");
        String url = searchUrl(language);
        Request request = request(url);
        JSONArray items = response(request);
        return getRepositoriesFromResponse(items, language);
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

    private String searchUrl(String language) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.github.com/search/repositories").newBuilder();
        urlBuilder.addQueryParameter("q", "language:" + language);
        urlBuilder.addQueryParameter("per_page", "30");
        urlBuilder.addQueryParameter("sort", "stars");
        urlBuilder.addQueryParameter("order", "desc");
        int randomPage = (int) (Math.random() * 10) + 1;
        urlBuilder.addQueryParameter("page", String.valueOf(randomPage));
        return urlBuilder.build().toString();
    }

    private List<GitRepository> getRepositoriesFromResponse(JSONArray items, String language) {
        List<GitRepository> repositories = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            if (repositoryIsOfLanguage(items.getJSONObject(i), language)) {
                repositories.add(repositoryFromResponse(items, i));
            }
        }
        return repositories;
    }

    private boolean repositoryIsOfLanguage(JSONObject jsonObject, String language) {
        return jsonObject.getString("language").equalsIgnoreCase(language);
    }

    private GitRepository repositoryFromResponse(JSONArray items, int i) {
        JSONObject repository = items.getJSONObject(i);
        return new GitRepository(repository.getString("name"), repository.getString("clone_url"),
                pmdStaticCodeAnalyzer);
    }

}
