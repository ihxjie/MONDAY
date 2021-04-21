package com.ihxjie.monday.service;

import com.ihxjie.monday.entity.Attendance;
import com.ihxjie.monday.entity.Clazz;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AttendanceService {
    @GET("/api/mobile/getAttendanceByClazzId")
    Call<List<Attendance>> getAttendanceByClazzId(@Query("clazzId") String clazzId);
}
