package com.ihxjie.monday.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ihxjie.monday.R;
import com.ihxjie.monday.entity.Attendance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    public Context context;
    private List<Attendance> mAttendanceList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View clazzView;
        ImageView attendanceType;
        TextView startTime;
        TextView endTime;
        TextView attendanceText;
        TextView attDate;
        public ViewHolder(View view){
            super(view);
            clazzView = view;
            attendanceType = (ImageView) view.findViewById(R.id.attendanceType);
            startTime = (TextView) view.findViewById(R.id.startTime);
            endTime = (TextView) view.findViewById(R.id.endTime);
            attendanceText = (TextView) view.findViewById(R.id.attendanceText);
            attDate = (TextView) view.findViewById(R.id.att_date);
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
                if (attendance.getAttendanceType() == 1){

                }


            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.ViewHolder holder, int position) {
        Attendance attendance = mAttendanceList.get(position);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter df2 = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(attendance.getStartTime(), df);
        LocalDateTime endTime = LocalDateTime.parse(attendance.getEndTime(), df);

        holder.startTime.setText(df2.format(startTime));
        holder.endTime.setText(df2.format(endTime));
        holder.attDate.setText(startTime.toLocalDate().toString());
        if (attendance.getAttendanceType() == 1){
            holder.attendanceType.setImageResource(R.drawable.att_click);
            holder.attendanceText.setText("点击签到");
        }else if (attendance.getAttendanceType() == 2){
            holder.attendanceType.setImageResource(R.drawable.att_qrcode);
            holder.attendanceText.setText("二维码签到");
        }else if (attendance.getAttendanceType() == 3) {
            holder.attendanceType.setImageResource(R.drawable.att_location);
            holder.attendanceText.setText("地理位置签到");
        } else if (attendance.getAttendanceType() == 4) {
            holder.attendanceType.setImageResource(R.drawable.att_face);
            holder.attendanceText.setText("人脸识别签到");
        } else if (attendance.getAttendanceType() == 5) {
            holder.attendanceType.setImageResource(R.drawable.face_location);
            holder.attendanceText.setText("人脸+地理签到");
        } else if (attendance.getAttendanceType() == 6) {
            holder.attendanceType.setImageResource(R.drawable.location_qrcode);
            holder.attendanceText.setText("地理+二维码签到");
        }
    }

    @Override
    public int getItemCount() {
        return mAttendanceList.size();
    }
}
