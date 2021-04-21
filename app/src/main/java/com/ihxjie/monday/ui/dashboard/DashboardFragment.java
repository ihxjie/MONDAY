package com.ihxjie.monday.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.ihxjie.monday.R;
import com.ihxjie.monday.adapter.ClazzAdapter;
import com.ihxjie.monday.common.Constants;
import com.ihxjie.monday.entity.ClazzInfo;
import com.ihxjie.monday.service.ClazzService;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";

    private DashboardViewModel dashboardViewModel;
    private Retrofit retrofit;
    private ClazzService clazzService;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mRecyclerView = root.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        queryClasses();
        mSwipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.teal_700);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryClasses();
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
        Call<List<ClazzInfo>> call = clazzService.queryClasses();
        call.enqueue(new Callback<List<ClazzInfo>>() {
            @Override
            public void onResponse(@NotNull Call<List<ClazzInfo>> call, @NotNull Response<List<ClazzInfo>> response) {
                try {
                    List<ClazzInfo> clazzInfoList = response.body();
                    ClazzAdapter adapter = new ClazzAdapter(getContext(), clazzInfoList);
                    mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "获取班级信息成功", Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getContext(), "获取班级信息失败，请稍后重试", Toast.LENGTH_SHORT).show();
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
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}