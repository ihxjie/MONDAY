package com.ihxjie.monday.service;

import com.ihxjie.monday.entity.CurrentUser;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserService {
    @GET("/api/currentUser")
    Call<CurrentUser> getCurrentUser();
}
