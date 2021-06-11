package com.ihxjie.monday.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ihxjie.monday.R;
import com.ihxjie.monday.common.Constants;
import com.ihxjie.monday.entity.Attendance;
import com.ihxjie.monday.service.AttendanceService;
import com.ihxjie.monday.service.ClazzService;
import com.ihxjie.monday.service.RecordService;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AttClickActivity extends AppCompatActivity {
    private static final String TAG = "AttClickActivity";

    private Toolbar toolbar;
    private MaterialButton button;

    private Retrofit retrofit;
    private RecordService recordService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Attendance attendance = (Attendance) getIntent().getSerializableExtra("attendance");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter df1 = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter df2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startTimeDate = LocalDateTime.parse(attendance.getStartTime(), df);
        LocalDateTime endTimeDate = LocalDateTime.parse(attendance.getEndTime(), df);

        setContentView(R.layout.activity_att_click);

        toolbar = findViewById(R.id.toolbar);
        button = findViewById(R.id.materialButton);

        button.setOnClickListener(v -> {
            submitAttClick(attendance.getAttendanceId());
        });

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(String.format(getResources().getString(R.string.att_date_time),
                    startTimeDate.format(df1), endTimeDate.format(df1)));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void submitAttClick(Long attendanceId){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = sharedPreferences.getString("userId", "");
        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.host)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        recordService = retrofit.create(RecordService.class);
        Call<String> call = recordService.attClick(userId, attendanceId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                showToast(response.body());
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                showToast(t.getMessage());
            }
        });
    }

}