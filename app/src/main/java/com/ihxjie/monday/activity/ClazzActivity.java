package com.ihxjie.monday.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.ihxjie.monday.R;
import com.ihxjie.monday.adapter.AttendanceAdapter;
import com.ihxjie.monday.common.Constants;
import com.ihxjie.monday.entity.Attendance;
import com.ihxjie.monday.entity.Clazz;
import com.ihxjie.monday.entity.ClazzInfo;
import com.ihxjie.monday.service.AttendanceService;
import com.ihxjie.monday.service.ClazzService;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClazzActivity extends AppCompatActivity {

    private static final String TAG = "ClazzActivity";

    private Retrofit retrofit;
    private ClazzService clazzService;
    private AttendanceService attendanceService;

    private ImageView imageView;
    private RecyclerView mRecyclerView;
    private Toolbar toolbar;
    private Context context;
    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clazz);
        context = this;
        imageView = findViewById(R.id.clazzLogo);
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String clazzId = getIntent().getStringExtra("clazzId");
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.host)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
        clazzService = retrofit.create(ClazzService.class);
        attendanceService = retrofit.create(AttendanceService.class);
        // 获取班级信息
        Call<Clazz> call = clazzService.getClazzInfo(clazzId);
        call.enqueue(new Callback<Clazz>() {
            @Override
            public void onResponse(@NotNull Call<Clazz> call, @NotNull Response<Clazz> response) {
                try {
                    Clazz clazz = response.body();
                    Glide.with(context)
                            .load(clazz.getClazzLogo())
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    imageView.setImageDrawable(resource);
                                }
                            });
                    collapsingToolbar.setTitle(clazz.getClazzName());

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<Clazz> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });

        Log.d(TAG, "onCreate: is here?");

        // 获取签到信息
        Call<List<Attendance>> listCall = attendanceService.getAttendanceByClazzId(clazzId);
        listCall.enqueue(new Callback<List<Attendance>>() {
            @Override
            public void onResponse(@NotNull Call<List<Attendance>> call, @NotNull Response<List<Attendance>> response) {
                List<Attendance> attendanceList = response.body();
                AttendanceAdapter adapter = new AttendanceAdapter(context, attendanceList);
                mRecyclerView.setAdapter(adapter);

            }

            @Override
            public void onFailure(@NotNull Call<List<Attendance>> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}