package com.dimas.android.login.data.remote;

import com.dimas.android.login.data.model.Post;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("/api/account/signup")
    @FormUrlEncoded
    Call<Post> savePost(@Field("login")String login,
                         @Field("password")String password);
}
