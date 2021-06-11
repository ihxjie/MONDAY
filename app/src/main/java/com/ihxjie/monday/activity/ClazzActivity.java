package com.ihxjie.monday.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ihxjie.monday.R;
import com.ihxjie.monday.adapter.AttendanceAdapter;
import com.ihxjie.monday.common.Constants;
import com.ihxjie.monday.entity.Attendance;
import com.ihxjie.monday.entity.Clazz;
import com.ihxjie.monday.entity.ClazzInfo;
import com.ihxjie.monday.entity.PositionInfo;
import com.ihxjie.monday.face.activity.FaceRecognizeActivity;
import com.ihxjie.monday.face.widget.ProgressDialog;
import com.ihxjie.monday.service.AttendanceService;
import com.ihxjie.monday.service.ClazzService;
import com.ihxjie.monday.service.RecordService;
import com.ihxjie.monday.util.BlurTransformation;
import com.ihxjie.monday.zxing.android.CaptureActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClazzActivity extends AppCompatActivity {

    private static final String TAG = "ClazzActivity";

    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";
    private static final int REQUEST_CODE_SCAN = 0x0000;
    private static final int REQUEST_CODE_FACE = 0x0001;
    private static final int REQUEST_CODE_LOCATION = 0x0002;
    private static final int REQUEST_CODE_LOCATION_FACE = 0x003;


    private Retrofit retrofit;
    private ClazzService clazzService;
    private AttendanceService attendanceService;
    private RecordService recordService;

    private ImageView imageView;
    private RecyclerView mRecyclerView;
    private Toolbar toolbar;
    private Context context;
    private CollapsingToolbarLayout collapsingToolbar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        //设置沉浸式状态栏，在MIUI系统中，状态栏背景透明。原生系统中，状态栏背景半透明。
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //设置沉浸式虚拟键，在MIUI系统中，虚拟键背景透明。原生系统中，虚拟键背景半透明。
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        setContentView(R.layout.activity_clazz);
        context = this;
        imageView = findViewById(R.id.clazzLogo);
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        mRecyclerView = findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
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
                            .apply(RequestOptions.bitmapTransform(new BlurTransformation(context, 25, 8)))
                            .into(imageView);
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在完成签到");
        progressDialog.setMessage("请等待");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SCAN:
                if (resultCode == RESULT_OK && data != null){
                    String content = data.getStringExtra(DECODED_CONTENT_KEY);
                    submitAttQrcode(content);
                }
                break;
            case REQUEST_CODE_FACE:
                if (resultCode == RESULT_OK && data != null){
                    Attendance attendance = (Attendance) data.getSerializableExtra("attendance");
                    submitAttFace(attendance.getAttendanceId());
                }
                break;
            case REQUEST_CODE_LOCATION_FACE:
                if (resultCode == RESULT_OK && data != null){
                    Intent intent = new Intent(this, FaceLocationActivity.class);
                    Attendance attendance = (Attendance) data.getSerializableExtra("attendance");
                    intent.putExtra("attendance", attendance);
                    startActivity(intent);
                }
                break;
            default:
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "已获取相机权限", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "相机权限被拒绝!", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "已获取定位权限", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "定位权限被拒绝!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void submitAttQrcode(String url){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = sharedPreferences.getString("userId", "");
        String path = url + "&userId=" + userId;
        Log.d(TAG, "submitAttQrcode: " + path);

        progressDialog.show();

        Runnable runnable = () -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(path)
                    .build();
            try {
                okhttp3.Response response = client.newCall(request).execute();
                Log.d(TAG, "submitAttQrcode: " + response);
                showResponse(response.body().string());
            }catch (Exception e){
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    private void showResponse(final String string) {
        runOnUiThread(() -> {
            //进行UI操作
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
        });
    }

    private void submitAttFace(Long attendanceId){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = sharedPreferences.getString("userId", "");

        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.host)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        recordService = retrofit.create(RecordService.class);
        Call<String> call = recordService.attFace(userId, attendanceId);
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