package com.ihxjie.monday.service;

import com.ihxjie.monday.entity.Attendance;
import com.ihxjie.monday.entity.PositionInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RecordService {

    @POST("/api/record/attClick")
    Call<String> attClick(@Query("userId") String userId, @Query("attendanceId") Long attendanceId);

    @POST("/api/record/attQrcode")
    Call<String> attQrcode(@Query("attendanceId") Long attendanceId);

    @POST("/api/record/attGps")
    Call<String> attGps(@Query("userId") String userId, @Body PositionInfo positionInfo);

    @POST("/api/record/attFace")
    Call<String> attFace(@Query("userId") String userId, @Query("attendanceId") Long attendanceId);

    @POST("/api/record/faceGps")
    Call<String> faceGps(@Query("userId") String userId, @Body PositionInfo positionInfo);
}
