package com.ihxjie.monday.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.ihxjie.monday.R;
import com.ihxjie.monday.adapter.ClazzAdapter;
import com.ihxjie.monday.common.Constants;
import com.ihxjie.monday.entity.ClazzInfo;
import com.ihxjie.monday.service.ClazzService;
import com.ihxjie.monday.zxing.android.CaptureActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";

    private static final int REQUEST_CODE_SCAN = 0x0000;
    private static final String DECODED_CONTENT_KEY = "codedContent";

    private Retrofit retrofit;
    private ClazzService clazzService;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View root;
    private MaterialButton button;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mRecyclerView = root.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mSwipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        queryClasses();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.teal_700);
        mSwipeRefreshLayout.setOnRefreshListener(this::queryClasses);
        button = root.findViewById(R.id.scan_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
            }
        });

        return root;
    }


    private void queryClasses(){

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.host)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

        clazzService = retrofit.create(ClazzService.class);
        Call<List<ClazzInfo>> call = clazzService.queryStuClazz();
        call.enqueue(new Callback<List<ClazzInfo>>() {
            @Override
            public void onResponse(@NotNull Call<List<ClazzInfo>> call, @NotNull Response<List<ClazzInfo>> response) {
                try {
                    List<ClazzInfo> clazzInfoList = response.body();
                    ClazzAdapter adapter = new ClazzAdapter(getContext(), clazzInfoList);
                    mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(root, "获取班级信息成功", Snackbar.LENGTH_SHORT).show();

                }catch (Exception e){
                    e.printStackTrace();
                    Snackbar.make(root, "获取班级信息失败，请稍后重试", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<ClazzInfo>> call, @NotNull Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String url = data.getStringExtra(DECODED_CONTENT_KEY);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                goClass(url);
            }
        }
    }
    private void goClass(String url){

        Runnable runnable = () -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                okhttp3.Response response = client.newCall(request).execute();
            }catch (Exception e){
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
        }catch (Exception e){
            e.printStackTrace();
        }
        queryClasses();
    }
}