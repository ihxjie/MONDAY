package com.ihxjie.monday.service;

import com.ihxjie.monday.entity.Clazz;
import com.ihxjie.monday.entity.ClazzInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClazzService {
    @GET("/api/classes")
    Call<List<ClazzInfo>> queryClasses();

    @GET("/api/mobile/getClazzInfo")
    Call<Clazz> getClazzInfo(@Query("clazzId") String clazzId);
}
