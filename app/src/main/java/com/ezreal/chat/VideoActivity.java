package com.ezreal.chat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ezreal.chat.adapter.MyVideoListAdapter;
import com.ezreal.chat.adapter.MyViewPagerAdapter;
import com.ezreal.chat.bean.VideoItem;
import com.ezreal.chat.fragment.CloudVideoFragment;
import com.ezreal.chat.fragment.PhoneVideoFragment;
import com.ezreal.chat.widgets.CommonTitleBar;
import com.ezreal.chat.widgets.NoScrollViewPager;
import com.ezreal.ezchat.R;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.internal.operators.observable.ObservableElementAt;
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

public class VideoActivity extends YWActivity {
    private static final String TAG = "VideoActivity";
    private static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
            "aixinyuan" + File.separator + "VIDEO" + File.separator;
    private Unbinder unbinder;

    @BindView(R.id.ct_play_video)
    public CommonTitleBar titleBar;

    private ArrayList<Fragment> fragments;
    @BindView(R.id.exchange_tab)
    public TabLayout mTabLayout;
    private CloudVideoFragment cloudVideoFragment;
    private PhoneVideoFragment phoneVideoFragment;
    @BindView(R.id.view_pager)
    public NoScrollViewPager viewPager;
    private MyViewPagerAdapter mViewPagerAdapter;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        unbinder = ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        titleBar.setTitleTxt("我的视频");
        titleBar.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setRightButtonImg(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_videocam).sizeDp(20).color(Color.WHITE));
        titleBar.setRightBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord_2();
            }
        });
        fragments = new ArrayList<>();
        cloudVideoFragment = new CloudVideoFragment();
        phoneVideoFragment = new PhoneVideoFragment();
        fragments.add(phoneVideoFragment);
        fragments.add(cloudVideoFragment);
        mViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(viewPager);
        mTabLayout.getTabAt(0).setText("手机存储");
        mTabLayout.getTabAt(1).setText("云空间");
        viewPager.setNoScroll(false);
    }

    private void startRecord_2(){
        Intent intent = new Intent();
        intent.setAction("android.media.action.VIDEO_CAPTURE");
        intent.addCategory("android.intent.category.DEFAULT");
        long time = System.currentTimeMillis();
        File file = new File(FILE_PATH, time + ".mp4" );
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        phoneVideoFragment.setFilePath(file.getPath());
        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult: record finish " + requestCode + " ," + resultCode);
        if (requestCode == 3000) {
            if (viewPager.getCurrentItem() == 0) {
                phoneVideoFragment.showEditWindow();
            }
        }
    }
}
