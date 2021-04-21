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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ihxjie.monday.R;
import com.ihxjie.monday.activity.ClazzActivity;
import com.ihxjie.monday.entity.Attendance;
import com.ihxjie.monday.entity.ClazzInfo;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    public Context context;
    private List<Attendance> mAttendanceList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View clazzView;
        ImageView attendanceType;
        TextView startTime;
        TextView endTime;
        public ViewHolder(View view){
            super(view);
            clazzView = view;
            attendanceType = (ImageView) view.findViewById(R.id.attendanceType);
            startTime = (TextView) view.findViewById(R.id.startTime);
            endTime = (TextView) view.findViewById(R.id.endTime);
        }
    }

    public AttendanceAdapter(Context context, List<Attendance> attendanceList){
        this.context = context;
        this.mAttendanceList = attendanceList;
    }

    @NonNull
    @Override
    public AttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_item, parent, false);
        AttendanceAdapter.ViewHolder holder = new AttendanceAdapter.ViewHolder(view);
        holder.clazzView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getLayoutPosition();
                Attendance attendance = mAttendanceList.get(position);

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.ViewHolder holder, int position) {
        Attendance attendance = mAttendanceList.get(position);
        holder.startTime.setText(attendance.getStartTime().toString());
        holder.endTime.setText(attendance.getEndTime().toString());
        if (attendance.getAttendanceType() == 1){
            holder.attendanceType.setImageResource(R.drawable.click);
        }else if (attendance.getAttendanceType() == 2){
            holder.attendanceType.setImageResource(R.drawable.qrcode);
        }else if (attendance.getAttendanceType() == 3) {
            holder.attendanceType.setImageResource(R.drawable.position);
        } else if (attendance.getAttendanceType() == 4) {
            holder.attendanceType.setImageResource(R.drawable.face);
        }
    }

    @Override
    public int getItemCount() {
        return mAttendanceList.size();
    }
}
