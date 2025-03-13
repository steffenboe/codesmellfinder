package com.steffenboe.codesmellfinder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
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
    // TODO the class is too large to test the individual parts of it, but maybe
    // this is not necessary and covered by the integration test?
    // should we divide the class into smaller classes, possibly separating
    // searching and cloning, and scanning?
    Optional<GitRepository> findRandomWithRuleViolations(String language) throws IOException {
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
            String repositoryDirectory = cloneRepo(gitRepository.name(), gitRepository.url());
            LOG.info("Cloned " + repositoryDirectory);
            if (gitRepository.scan(repositoryDirectory).size() > 0) {
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
        return new GitRepository(repository.getString("name"), repository.getString("clone_url"),
                pmdStaticCodeAnalyzer);
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
        // TODO: make language configurable
        urlBuilder.addQueryParameter("q", "language:java");
        urlBuilder.addQueryParameter("per_page", "30");
        urlBuilder.addQueryParameter("sort", "stars");
        urlBuilder.addQueryParameter("order", "desc");
        int randomPage = (int) (Math.random() * 10) + 1;
        urlBuilder.addQueryParameter("page", String.valueOf(randomPage));
        return urlBuilder.build().toString();
    }

    private String cloneRepo(String name, String url) {
        String tmpdir = System.getProperty("java.io.tmpdir");
        File file = Path.of(tmpdir, name).toFile();

        if (file.exists()) {
            delete(file);
        }
        executeCloneCommand(file, url);
        return file.getPath();
    }

    private void executeCloneCommand(File file, String url) {
        CloneCommand cloneCommand = Git
                .cloneRepository()
                .setDirectory(file)
                .setURI(url);
        call(cloneCommand);
    }

    private void call(CloneCommand cloneCommand) {
        try (Git git = cloneCommand.call()) {

        } catch (InvalidRemoteException invalidRemoteException) {
            LOG.error(invalidRemoteException.getMessage());
        } catch (TransportException transportException) {
            LOG.error(transportException.getMessage());
        } catch (GitAPIException gitAPIException) {
            LOG.error(gitAPIException.getMessage());
        }
    }

    private void delete(File file) {
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
