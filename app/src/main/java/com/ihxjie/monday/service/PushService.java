package com.ihxjie.monday.service;

import com.ihxjie.monday.entity.PushVo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PushService {
    @POST("/api/push/updateRegId")
    Call<String> updateRegId(@Body PushVo pushVo);
}
