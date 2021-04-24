package com.ihxjie.monday.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ihxjie.monday.MainActivity;
import com.ihxjie.monday.R;
import com.ihxjie.monday.activity.AttClickActivity;
import com.ihxjie.monday.activity.AttFaceActivity;
import com.ihxjie.monday.activity.AttLocationActivity;
import com.ihxjie.monday.activity.AttQrcodeActivity;
import com.ihxjie.monday.activity.ClazzActivity;
import com.ihxjie.monday.activity.FaceLocationActivity;
import com.ihxjie.monday.activity.LocationActivity;
import com.ihxjie.monday.activity.LocationQrcodeActivity;
import com.ihxjie.monday.entity.Attendance;
import com.ihxjie.monday.face.activity.FaceRecognizeActivity;
import com.ihxjie.monday.face.activity.RegisterAndRecognizeActivity;
import com.ihxjie.monday.zxing.android.CaptureActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    public Context context;
    private List<Attendance> mAttendanceList;
    private static final int REQUEST_CODE_SCAN = 0x0000;
    private static final int REQUEST_CODE_FACE = 0x0001;

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
                    Intent intent = new Intent(view.getContext(), AttClickActivity.class);
                    intent.putExtra("attendance", attendance);
                    view.getContext().startActivity(intent);
                }else if (attendance.getAttendanceType() == 2){
                    goScan(view.getContext());
                }else if (attendance.getAttendanceType() == 3) {
                    goLocation(view.getContext());
                } else if (attendance.getAttendanceType() == 4) {
                    goFace(view.getContext());
                } else if (attendance.getAttendanceType() == 5) {
                    Intent intent = new Intent(view.getContext(), FaceLocationActivity.class);
                    intent.putExtra("attendance", attendance);
                    view.getContext().startActivity(intent);
                } else if (attendance.getAttendanceType() == 6) {
                    Intent intent = new Intent(view.getContext(), LocationQrcodeActivity.class);
                    intent.putExtra("attendance", attendance);
                    view.getContext().startActivity(intent);
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

    private void goScan(Context context){
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(((Activity)context), new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            Intent intent = new Intent(context, CaptureActivity.class);
            ((Activity)context).startActivityForResult(intent, REQUEST_CODE_SCAN);
        }
    }
    private void goFace(Context context){
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(((Activity)context), new String[]{Manifest.permission.CAMERA}, 2);
        } else {
            Intent intent = new Intent(context, FaceRecognizeActivity.class);
            ((Activity)context).startActivityForResult(intent, REQUEST_CODE_FACE);
        }
    }
    private void goLocation(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(((Activity)context), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
        } else {
            Intent intent = new Intent(context, LocationActivity.class);
            ((Activity)context).startActivityForResult(intent, REQUEST_CODE_FACE);
        }
    }

}
