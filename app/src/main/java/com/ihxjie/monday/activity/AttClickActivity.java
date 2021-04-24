package com.ihxjie.monday.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.ihxjie.monday.R;
import com.ihxjie.monday.entity.Attendance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AttClickActivity extends AppCompatActivity {
    private static final String TAG = "AttClickActivity";

    private Toolbar toolbar;

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

}