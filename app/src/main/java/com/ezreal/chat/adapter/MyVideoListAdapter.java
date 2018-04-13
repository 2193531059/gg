package com.ezreal.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ezreal.chat.bean.VideoItem;
import com.ezreal.ezchat.R;

import java.util.List;

/**
 * Created by Administrator on 2018/4/13.
 */

public class MyVideoListAdapter extends RecyclerView.Adapter{

    private LayoutInflater inflater;
    private List<VideoItem> mData;
    private Context mContext;
    private MyClickListener mListener;

    public MyVideoListAdapter(List<VideoItem> mData, Context mContext) {
        inflater = LayoutInflater.from(mContext);
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.video_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        VideoItem item = mData.get(position);
        String title = item.getTitle();
        String createTime = item.getCreatTime();

        ((ViewHolder)holder).tv_file_name.setText(title);
        ((ViewHolder)holder).time.setText(createTime);
        ((ViewHolder)holder).bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDeleteClick(position);
                }
            }
        });
        ((ViewHolder)holder).bt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onUploadClick(position);
                }
            }
        });
    }

    public List<VideoItem> getmData(){
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void registerListener(MyClickListener listener){
        this.mListener = listener;
    }

    public interface MyClickListener{
        void onDeleteClick(int position);
        void onUploadClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_file_name;
        private TextView time;
        private ImageButton bt_upload;
        private ImageButton bt_delete;
        private ProgressBar progress_scan;
        private TextView tv_percent;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_file_name = itemView.findViewById(R.id.tv_file_name);
            time = itemView.findViewById(R.id.time);
            bt_upload = itemView.findViewById(R.id.bt_upload);
            bt_delete = itemView.findViewById(R.id.bt_delete);
            progress_scan = itemView.findViewById(R.id.progress_scan);
            tv_percent = itemView.findViewById(R.id.tv_percent);
        }
    }
}
