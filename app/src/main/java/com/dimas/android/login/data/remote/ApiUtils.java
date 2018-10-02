package com.dimas.android.login.data.remote;

public class ApiUtils {
    private ApiUtils() {
    }
    private static final String BASE_URL = "http://junior.balinasoft.com/";

    public static APIService getApiService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
