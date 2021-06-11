package com.ihxjie.monday.service;

import com.ihxjie.monday.entity.CurrentUser;
import com.ihxjie.monday.entity.MobileUser;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UserService {
    @GET("/api/mobile/currentUser")
    Call<CurrentUser> getCurrentUser(@Query("userId") String userId);

    @POST("/api/updateUser")
    Call<String> updateUser(@Body CurrentUser currentUser);

    @POST("/api/updateAvatar")
    Call<String> updateAvatar(@Query("userId") String userId, @Query("avatar") String avatar);

    @POST("/api/login/mobile")
    Call<String> login(@Body MobileUser mobileUser);

}
