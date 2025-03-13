package com.steffenboe.codesmellfinder;

import java.io.IOException;

import com.squareup.okhttp.OkHttpClient;

// TODO pass language to use as parameter
public class Main {
    public static void main(String[] args) throws IOException {
        GitHub githubScanner = new GitHub(
            new RestClient(
                new OkHttpClient()), 
                new PMDStaticCodeAnalyzer());
        githubScanner.findRandomWithRuleViolations("java");
    }
}