package com.ihxjie.monday.service;

import com.ihxjie.monday.entity.Clazz;
import com.ihxjie.monday.entity.ClazzInfo;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ClazzService {
    @GET("/api/mobile/queryStuClazz")
    Call<List<ClazzInfo>> queryStuClazz(@Query("userId") String userId);

    @GET("/api/mobile/getClazzInfo")
    Call<Clazz> getClazzInfo(@Query("clazzId") String clazzId);

    @GET("/api/mobile/quitClazz")
    Call<String> quitClazz(@Query("clazzId") String clazzId, @Query("userId") String userId);

    @GET("/mobile/go-class/{clazzId}")
    Call<Map<String, Object>> goClass(@Path("clazzId") String clazzId);
}
