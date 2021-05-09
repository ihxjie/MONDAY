package com.ihxjie.monday.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ihxjie.monday.R;
import com.ihxjie.monday.adapter.NoticeAdapter;
import com.ihxjie.monday.common.Constants;
import com.ihxjie.monday.entity.Notice;
import com.ihxjie.monday.service.NoticeService;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationsFragment extends Fragment {

    private static final String TAG = "NotificationsFragment";

    private RecyclerView mRecyclerView;
    private Retrofit retrofit;
    private NoticeService noticeService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        mRecyclerView = root.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.host)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
        noticeService = retrofit.create(NoticeService.class);
        Call<List<Notice>> call = noticeService.getStuNotice();
        call.enqueue(new Callback<List<Notice>>() {
            @Override
            public void onResponse(@NotNull Call<List<Notice>> call, @NotNull Response<List<Notice>> response) {
                List<Notice> noticeList = response.body();
                NoticeAdapter adapter = new NoticeAdapter(getContext(), noticeList);
                mRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NotNull Call<List<Notice>> call, @NotNull Throwable t) {

            }
        });

        return root;
    }
}