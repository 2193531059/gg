package com.ezreal.chat.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.ezreal.chat.VideoActivity;
import com.ezreal.chat.VideoInfoActivity;
import com.ezreal.chat.adapter.MyVideoListAdapter;
import com.ezreal.chat.bean.VideoItem;
import com.ezreal.ezchat.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import static com.ezreal.chat.HttpUtils.HOST;

/**
 * Created by Administrator on 2018/4/16.
 */

public class PhoneVideoFragment extends Fragment{
    private static final String TAG = "PhoneVideoFragment";
    private static final MediaType MEDIA_OBJECT_STREAM = MediaType.parse("application/json; charset=utf-8");


    private String filePath;

    private Unbinder unbinder;
    @BindView(R.id.video_recycler)
    public RecyclerView mRecyclerView;
    @BindView(R.id.video_refresher)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    private MyVideoListAdapter mAdapter;
    private List<VideoItem> itemList;
    private AlertDialog editRecorderDialog;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phone_video_fragment_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    public void setFilePath(String filePath){
        this.filePath = filePath;
        Log.e(TAG, "setFilePath: filePath = " + filePath);
    }

    private void init(){
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(lm);
        itemList = new ArrayList<>();
        mAdapter = new MyVideoListAdapter(itemList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        mAdapter.registerListener(new MyVideoListAdapter.MyClickListener() {
            @Override
            public void onDeleteClick(int position) {
                List<VideoItem> items = mAdapter.getmData();
                VideoItem item = items.get(position);
                String filePath = item.getFilePath();
                File deleteFile = new File(filePath);
                if (deleteFile.exists()) {
                    deleteFile.delete();
                    items.remove(item);
                    mAdapter.setDeleteClick(false);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onUploadClick(int position) {
                List<VideoItem> items = mAdapter.getmData();
                VideoItem item = items.get(position);

                HashMap<String, Object> params = new HashMap<>();
                File file = new File(item.getFilePath());
                if (file.exists()) {
                    params.put("file", file);
                }

                upload(HOST + "videoUploadServlet", params, new ReqProgressCallBack() {
                    @Override
                    public void onProgress(double progress) {
                        Log.e(TAG, "onProgress: progress = " + progress);
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(getActivity(), "上传完成！", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onSuccess:");
                        mAdapter.setUploadClick(false);
                    }

                    @Override
                    public void onFailer() {
                        Toast.makeText(getActivity(), "上传失败！", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailer: ");
                        mAdapter.setUploadClick(false);
                    }
                });
            }

            @Override
            public void onPlayClick(int position) {
                List<VideoItem> items = mAdapter.getmData();
                VideoItem item = items.get(position);
                String url = item.getFilePath();
                String title = item.getTitle();
                Log.e(TAG, "onPlayClick: url = " + url);
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                bundle.putString("title", title);
                Intent intent = new Intent(getActivity(), VideoInfoActivity.class);
                intent.putExtra("bundle", bundle);
                getActivity().startActivity(intent);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e(TAG, "onRefresh: .....");
            }
        });

        getVideoFileList();
    }

    private void getVideoFileList(){
        File fileRecorder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                "aixinyuan" + File.separator, "VIDEO");
        HashMap<String, String> hashMapRecorder = getFile(fileRecorder);
        for (Map.Entry e : hashMapRecorder.entrySet()) {
            String key = (String) e.getKey();
            String value = (String) e.getValue();

            if (key.endsWith(".mp4")) {
                String name = key.substring(0, key.lastIndexOf("."));
                if (!TextUtils.isEmpty(name)) {
                    VideoItem bean = new VideoItem();
                    bean.setTitle(name);
                    bean.setFilePath(value);

                    File f = new File(value);
                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .format(new Date(f.lastModified()));

                    bean.setCreatTime(time);

                    itemList.add(bean);
                }
            }
        }
        final SimpleDateFormat dff1 = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        Collections.sort(itemList, new Comparator<VideoItem>() {
            @Override
            public int compare(VideoItem o1, VideoItem o2) {
                String time1 = o1.getCreatTime();
                String time2 = o2.getCreatTime();
                long t1 = 0;
                long t2 = 0;
                try {
                    t1 = dff1.parse(time1).getTime();
                    t2 = dff1.parse(time2).getTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t1 > t2) {
                    return -1;
                } else if (t1 < t2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    private HashMap<String, String> getFile(File file){
        HashMap<String, String> fileList = new HashMap<>();
        getFileList(file, fileList);
        return fileList;
    }

    private void getFileList(File path,HashMap fileList){
        //如果是文件夹的话
        if (path.isDirectory()){
            File[] files = path.listFiles();
            //先判断下有没有权限，如果没有权限的话，就不执行了
            if(null == files){
                return;
            }
            for (int i = 0; i < files.length; i++) {
                getFileList(files[i],fileList);
            }
        }else{
            String filePath = path.getAbsolutePath();
            String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
            fileList.put(fileName, filePath);
        }
    }

    public void showEditWindow(){
        View viewone = View.inflate(getActivity(), R.layout.record_edit, null);
        editRecorderDialog = new AlertDialog.Builder(getActivity())
                .setView(viewone)
                .setPositiveButton(R.string.confirm, null)
                .setNegativeButton(R.string.cancel, null)
                .create();
        editRecorderDialog.show();

        final EditText et_name = viewone.findViewById(R.id.edit_name);
        editRecorderDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString().trim();
                if (name.length() == 0){
                    Toast.makeText(getActivity(),"名称不能为空！",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (name.length() > 10){
                    Toast.makeText(getActivity(),"名称太长！",Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean isReNameSuccess = renameFile(name);
                if (isReNameSuccess) {
                    editRecorderDialog.dismiss();
                }
            }
        });

        editRecorderDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File newFile = new File(filePath);
                long fileTime = newFile.lastModified();
                String[] pathSplit = filePath.split("/");
                String lastPath = pathSplit[pathSplit.length - 1];
                VideoItem recorderBean = new VideoItem();
                recorderBean.setTitle(lastPath.substring(0,lastPath.length()-4));
                recorderBean.setFilePath(filePath);
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new Date(fileTime));
                recorderBean.setCreatTime(time);

                List<VideoItem> adapterData = mAdapter.getmData();

                adapterData.add(0, recorderBean);

                mAdapter.notifyDataSetChanged();

                editRecorderDialog.dismiss();
            }
        });
    }

    private boolean renameFile(String name){
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        String[] pathSplit = filePath.split("/");
        String lastPath = pathSplit[pathSplit.length - 1];
        String replaceStr = lastPath.substring(0, lastPath.length() - 4);
        Log.e(TAG, "renameFile: replaceStr = " + replaceStr);
        File file = new File(filePath);
        String newPath = filePath.replace(replaceStr, name);

        File newFile = new File(newPath);
        if (newFile.exists()) {
            Toast.makeText(getActivity(),"文件已经存在！",Toast.LENGTH_SHORT).show();
            return false;
        }

        file.renameTo(new File(newPath));

        long fileTime = newFile.lastModified();
        VideoItem recorderBean = new VideoItem();
        recorderBean.setTitle(name);
        recorderBean.setFilePath(newPath);
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date(fileTime));
        recorderBean.setCreatTime(time);

        List<VideoItem> adapterData = mAdapter.getmData();

        adapterData.add(0, recorderBean);

        mAdapter.notifyDataSetChanged();
        return true;
    }

    private void upload(String actionUrl, HashMap<String, Object> paramsMap, final ReqProgressCallBack callBack){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (String key : paramsMap.keySet()) {
            Object object = paramsMap.get(key);
            if (!(object instanceof File)) {
                builder.addFormDataPart(key, object.toString());
            } else {
                File file = (File) object;
                builder.addFormDataPart(key, file.getName(), createProgressRequestBody(MEDIA_OBJECT_STREAM, file, callBack));
            }
        }

        RequestBody body = builder.build();

        final Request request = new Request.Builder().url(actionUrl).post(body).build();

        OkHttpClient mOkHttpClient = new OkHttpClient();

        final Call call = mOkHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: e1 = " + e.getMessage());
                failedCallBack(callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    Log.e(TAG, "response ----->" + string);
                } else {
                    failedCallBack(callBack);
                }
            }
        });
    }

    private RequestBody createProgressRequestBody(final MediaType contentType, final File file, final ReqProgressCallBack callBack) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    Buffer buf = new Buffer();
                    long remaining = contentLength();
                    long current = 0;
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        current += readCount;
                        Log.e(TAG, "current------>" + current);
                        progressCallBack(remaining, current, callBack);
                        if (current == remaining) {
                            successCallBack(callBack);
                        }
                    }
                } catch (Exception e) {
                    failedCallBack(callBack);
                    e.printStackTrace();
                }
            }
        };
    }

    private void progressCallBack(final long total, final long current, final ReqProgressCallBack callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    Log.e(TAG, "run: current = " + current + " total = " + total);
                    double progress = current / total;
                    Log.e(TAG, "run: progress = " + progress);
                    callBack.onProgress(progress);
                }
            }
        });
    }

    private void successCallBack(final ReqProgressCallBack callBack){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    Log.e(TAG, "run: successCallBack");
                    callBack.onSuccess();
                }
            }
        });
    }

    private void failedCallBack(final ReqProgressCallBack callBack){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    Log.e(TAG, "run: failedCallBack");
                    callBack.onFailer();
                }
            }
        });
    }

    interface ReqProgressCallBack{
        void onProgress(double progress);
        void onSuccess();
        void onFailer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
