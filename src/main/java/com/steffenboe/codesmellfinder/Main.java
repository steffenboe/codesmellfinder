package com.steffenboe.codesmellfinder;

import java.io.IOException;
import java.util.List;

import com.squareup.okhttp.OkHttpClient;

public class Main {
    public static void main(String[] args) throws IOException {
        GitHub githubScanner = new GitHub(
                new RestClient(
                        new OkHttpClient()),
                new PMDStaticCodeAnalyzer());
        CodeSmellScanner codeSmellScanner = new CodeSmellScanner();
        List<GitRepository> repositories = githubScanner.findRandomGitRepository(args[0]);
        codeSmellScanner.searchForCodeSmells(repositories);
    }
}