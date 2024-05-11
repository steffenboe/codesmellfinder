package com.steffenboe.codesmellfinder;

import java.io.IOException;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

class RestClient {

    private OkHttpClient okHttpClient;

    RestClient(OkHttpClient okHttpClient){
        this.okHttpClient = okHttpClient;
    }

    public String execute(Request request) throws IOException {
        return okHttpClient.newCall(request).execute().body().string();
    }
}
