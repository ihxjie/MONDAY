package com.ihxjie.monday.service;

import com.ihxjie.monday.entity.CurrentUser;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserService {
    @GET("/api/currentUser")
    Call<CurrentUser> getCurrentUser();

    @POST("/api/updateUser")
    Call<String> updateUser(@Body CurrentUser currentUser);
}
