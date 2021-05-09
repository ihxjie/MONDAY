package com.ihxjie.monday.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ihxjie.monday.R;
import com.ihxjie.monday.entity.Notice;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    public Context context;
    private List<Notice> mNoticeList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View noticeView;
        TextView noticeTitle;
        TextView noticeTime;
        TextView attClazz;
        TextView attType;
        TextView attTime;
        public ViewHolder(View view){
            super(view);
            noticeView = view;
            noticeTitle = (TextView) view.findViewById(R.id.notice_title);
            noticeTime = (TextView) view.findViewById(R.id.notice_time);
            attClazz = (TextView) view.findViewById(R.id.att_clazz);
            attType = (TextView) view.findViewById(R.id.att_type);
            attTime = (TextView) view.findViewById(R.id.att_time);
        }
    }

    public NoticeAdapter(Context context, List<Notice> noticeList){
        this.context = context;
        this.mNoticeList = noticeList;
    }

    @NonNull
    @Override
    public NoticeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item, parent, false);
        NoticeAdapter.ViewHolder holder = new NoticeAdapter.ViewHolder(view);
        holder.noticeView.setOnClickListener(v -> {
            int position = holder.getLayoutPosition();
            Notice notice = mNoticeList.get(position);
            // Toast.makeText(view.getContext(), "clicked: " + clazz.getTitle(), Toast.LENGTH_SHORT).show();
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeAdapter.ViewHolder holder, int position) {
        Notice notice = mNoticeList.get(position);
        String[] strings = notice.getNoticeContent().split("\\^");
        String attClazz = strings[0];
        String attType = strings[1];
        String attTime = strings[2];

        holder.noticeTitle.setText(notice.getNoticeTitle());
        holder.noticeTime.setText(notice.getNoticeTime());
        holder.attClazz.setText(attClazz);
        holder.attType.setText(attType);
        holder.attTime.setText(attTime);
    }

    @Override
    public int getItemCount() {
        return mNoticeList.size();
    }

}
