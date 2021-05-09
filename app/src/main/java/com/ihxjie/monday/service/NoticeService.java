package com.ihxjie.monday.service;

import com.ihxjie.monday.entity.Notice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NoticeService {
    @GET("/api/mobile/getStuNotice")
    Call<List<Notice>> getStuNotice();
}
