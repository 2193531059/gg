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

public class MyCloudVideoListAdapter extends RecyclerView.Adapter{

    private LayoutInflater inflater;
    private List<VideoItem> mData;
    private Context mContext;
    private MyClickListener mListener;

    public MyCloudVideoListAdapter(List<VideoItem> mData, Context mContext) {
        inflater = LayoutInflater.from(mContext);
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.video_item_cloud, parent, false);
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
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_file_name;
        private TextView time;
        private ImageButton bt_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_file_name = itemView.findViewById(R.id.tv_file_name);
            time = itemView.findViewById(R.id.time);
            bt_delete = itemView.findViewById(R.id.bt_delete);
        }
    }
}
