package com.ezreal.chat.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezreal.chat.adapter.MyCloudVideoListAdapter;
import com.ezreal.chat.adapter.MyVideoListAdapter;
import com.ezreal.chat.bean.VideoItem;
import com.ezreal.ezchat.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/4/16.
 */

public class CloudVideoFragment extends Fragment{
    private static final String TAG = "CloudVideoFragment";
    private Unbinder unbinder;

    @BindView(R.id.video_recycler)
    public RecyclerView mRecyclerView;
    @BindView(R.id.video_refresher)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    private MyCloudVideoListAdapter mAdapter;
    private List<VideoItem> itemList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cloud_video_fragment_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(lm);
        itemList = new ArrayList<>();
        mAdapter = new MyCloudVideoListAdapter(itemList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        mAdapter.registerListener(new MyCloudVideoListAdapter.MyClickListener() {
            @Override
            public void onDeleteClick(int position) {
                List<VideoItem> items = mAdapter.getmData();
                VideoItem item = items.get(position);
                deleteCloudVideo();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e(TAG, "onRefresh: .....");
            }
        });

        getCloudVideo();
    }

    private void getCloudVideo(){

    }

    private void deleteCloudVideo(){

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
