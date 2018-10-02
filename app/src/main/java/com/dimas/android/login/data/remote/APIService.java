package com.dimas.android.login.data.remote;

import com.dimas.android.login.data.model.Post;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers("Content-Type: application/json; charset=UTF-8")
    @POST("/api/account/signup")
    Call<Post> savePost(@Body String body);
}
