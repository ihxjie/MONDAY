package com.ihxjie.monday.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ihxjie.monday.R;
import com.ihxjie.monday.activity.ClazzActivity;
import com.ihxjie.monday.entity.ClazzInfo;

import java.util.List;

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
}
