package com.steffenboe.codesmellfinder;

import java.io.IOException;

import com.squareup.okhttp.OkHttpClient;

public class Main {
    public static void main(String[] args) throws IOException {
        // Determine language to search, default to Java
        String language = args.length > 0 ? args[0] : "java";
        RepositoryScanner githubScanner = new RepositoryScanner(
            new RestClient(
                new OkHttpClient()),
            new PMDStaticCodeAnalyzer(),
            language);
        githubScanner.findRandomWithRuleViolations();
    }
}