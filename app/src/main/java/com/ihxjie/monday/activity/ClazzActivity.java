package com.ihxjie.monday.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
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
import com.ihxjie.monday.R;
import com.ihxjie.monday.adapter.AttendanceAdapter;
import com.ihxjie.monday.common.Constants;
import com.ihxjie.monday.entity.Attendance;
import com.ihxjie.monday.entity.Clazz;
import com.ihxjie.monday.entity.ClazzInfo;
import com.ihxjie.monday.face.activity.FaceRecognizeActivity;
import com.ihxjie.monday.service.AttendanceService;
import com.ihxjie.monday.service.ClazzService;
import com.ihxjie.monday.util.BlurTransformation;
import com.ihxjie.monday.zxing.android.CaptureActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                //返回的文本内容
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
                //返回的BitMap图像
                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);

                Toast.makeText(this, "你扫描到的内容是：" + content, Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_CODE_FACE && resultCode == RESULT_OK) {
            if (data != null) {
                Toast.makeText(this, "FACE RECOGNIZE", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startActivity(new Intent(context, CaptureActivity.class));
                }else {
                    Toast.makeText(this, "相机权限被拒绝!", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startActivity(new Intent(context, FaceRecognizeActivity.class));
                }else {
                    Toast.makeText(this, "相机权限被拒绝!", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startActivity(new Intent(context, LocationActivity.class));
                }else {
                    Toast.makeText(this, "定位权限被拒绝!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}