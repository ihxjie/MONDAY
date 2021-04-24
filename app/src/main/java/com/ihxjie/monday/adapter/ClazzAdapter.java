package com.ihxjie.monday.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.ihxjie.monday.R;
import com.ihxjie.monday.activity.ClazzActivity;
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

public class ClazzAdapter extends RecyclerView.Adapter<ClazzAdapter.ViewHolder> {
    public Context context;
    private List<ClazzInfo> mClazzInfoList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View clazzView;
        ImageView clazzLogo;
        TextView clazzTitle;
        TextView clazzDescription;
        TextView teacher;
        public ViewHolder(View view){
            super(view);
            clazzView = view;
            clazzLogo = (ImageView) view.findViewById(R.id.clazzLogo);
            clazzTitle = (TextView) view.findViewById(R.id.clazzTitle);
            clazzDescription = (TextView) view.findViewById(R.id.clazzDescription);
            teacher = (TextView) view.findViewById(R.id.teacher);
        }
    }

    public ClazzAdapter(Context context, List<ClazzInfo> clazzInfoList){
        this.context = context;
        this.mClazzInfoList = clazzInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clazz_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.clazzView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getLayoutPosition();
                ClazzInfo clazzInfo = mClazzInfoList.get(position);
                // Toast.makeText(view.getContext(), "clicked: " + clazz.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), ClazzActivity.class);
                intent.putExtra("clazzId", clazzInfo.id);
                view.getContext().startActivity(intent);
            }

        });
        holder.clazzView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getLayoutPosition();
                ClazzInfo clazzInfo = mClazzInfoList.get(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                dialog.setTitle("是否退出班级");
                dialog.setMessage("重新加入班级需重新联系授课教师");
                dialog.setCancelable(false);
                dialog.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        quitClazz(clazzInfo.getId());
                        mClazzInfoList.remove(position);
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();

                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClazzInfo clazzInfo = mClazzInfoList.get(position);
        Glide.with(context)
                .load(clazzInfo.getLogo())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(holder.clazzLogo);
        holder.clazzTitle.setText(clazzInfo.getTitle());
        holder.clazzDescription.setText(clazzInfo.getDescription());
        holder.teacher.setText(clazzInfo.getTeacher());
    }

    @Override
    public int getItemCount() {
        return mClazzInfoList.size();
    }

    private void quitClazz(String clazzId){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.host)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
        ClazzService clazzService = retrofit.create(ClazzService.class);
        Call<String> call = clazzService.quitClazz(clazzId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                if (response.body().equals("success")){
                    Toast.makeText(context, "已退出班级", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "退出失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
            }
        });

    }
}
