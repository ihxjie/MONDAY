package com.ihxjie.monday.service;

import com.ihxjie.monday.entity.Notice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NoticeService {
    @GET("/api/mobile/getStuNotice")
    Call<List<Notice>> getStuNotice(@Query("userId") String userId);
}
