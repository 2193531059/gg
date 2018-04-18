package com.ezreal.chat.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import com.ezreal.chat.HttpUtils;
import com.ezreal.chat.adapter.MyCloudVideoListAdapter;
import com.ezreal.chat.adapter.MyVideoListAdapter;
import com.ezreal.chat.bean.VideoItem;
import com.ezreal.ezchat.R;
import com.suntek.commonlibrary.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Response;

import static com.ezreal.chat.HttpUtils.HOST;

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
                int id = item.getId();
                deleteCloudVideo(id);
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

    @SuppressLint("StaticFieldLeak")
    private void getCloudVideo(){
        new AsyncTask<Void, Void, Void>() {
            String json = null;

            @Override
            protected Void doInBackground(Void... voids) {
                Response response = HttpUtils.getInstance().request(HOST + "findAllVideoServlet");
                try {
                    if (response != null) {
                        json = response.body().string();
                        Log.e(TAG, "doInBackground:getCloudVideo json = " + json);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground:getCloudVideo e = " + e);
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (!TextUtils.isEmpty(json)) {
                    try {
                        JSONArray array = new JSONArray(json);
                        for (int i = 0; i<array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            VideoItem item = new VideoItem();
                            item.setTitle("视频" + (i + 1));
                            item.setCreatTime("2013-01-01");
                            item.setFilePath(object.optString("url"));
                            item.setId(object.optInt("id"));
                            itemList.add(item);
                        }
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void deleteCloudVideo(final int id){
        new AsyncTask<Void, Void, Void>() {
            String json = null;

            @Override
            protected Void doInBackground(Void... voids) {
                Log.e(TAG, "doInBackground: url = " + HOST + "deleteByIdServlet?id=" + id);
                Response response = HttpUtils.getInstance().request(HOST + "deleteByIdServlet?id=" + id);
                try {
                    if (response != null) {
                        json = response.body().string();
                        Log.e(TAG, "doInBackground:getCloudVideo json = " + json);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground:getCloudVideo e = " + e);
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        List<VideoItem> data = mAdapter.getmData();
                        VideoItem oldItem = null;
                        for (VideoItem item :data) {
                            int oldId = item.getId();
                            if (oldId == id) {
                                Log.e(TAG, "run: oldId = " + id);
                                oldItem = item;
                            }
                        }
                        if (oldItem != null) {
                            data.remove(oldItem);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }.execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
