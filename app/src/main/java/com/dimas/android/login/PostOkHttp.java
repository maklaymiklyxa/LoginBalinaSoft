package com.dimas.android.login;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostOkHttp extends AsyncTask<String, String, String> {

    private static final String url = "http://junior.balinasoft.com/api/account/signup";
    private String json;
    private OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    public PostOkHttp(String json) {
        this.json = json;
    }

    private String doPostRequest() throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return doPostRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}