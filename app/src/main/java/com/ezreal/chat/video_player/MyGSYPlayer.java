package com.ezreal.chat.video_player;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ezreal.ezchat.R;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.io.File;
import java.util.Date;

public class MyGSYPlayer {
    private static final String TAG = "MyGSYPlayer";
    private OrientationUtils orientationUtils;
    private MyCustomGSYPlayer gsyVideoPlayer;
    private ImageView backBtn;
    private ImageView startBtn;
    private boolean isPlay = false;
    private boolean isFullScreen = false;
    private ImageView fullScreenView;
    private Activity activity;
    private RefreshListener mRefreshListener;
    private String vidUrl;
    private String vidName;
    private boolean isRefreshNet = false;
//    private boolean isStartFullScreen = false;
//    private boolean isShirenkScreen = false;

    public void initPlayer(final Activity activity,final MyCustomGSYPlayer gsyVideoPlayer){
        if(gsyVideoPlayer == null || activity == null){
            return;
        }
        this.activity = activity;
        this.gsyVideoPlayer = gsyVideoPlayer;
        orientationUtils = new OrientationUtils(activity, gsyVideoPlayer);
        orientationUtils.setEnable(false);

        gsyVideoPlayer.getFullscreenButton().setVisibility(View.VISIBLE);//隐藏与显示全屏功能

        gsyVideoPlayer.setEnlargeImageRes(R.drawable.full_screen);
        gsyVideoPlayer.setShrinkImageRes(R.drawable.small_screen);
        backBtn = gsyVideoPlayer.getBackButton();
        backBtn.setVisibility(View.GONE);
        startBtn = (ImageView) gsyVideoPlayer.getStartButton();
        startBtn.setVisibility(View.GONE);

        gsyVideoPlayer.setNeedLockFull(true);
        gsyVideoPlayer.setShowFullAnimation(false);
        gsyVideoPlayer.setLockLand(false);
        gsyVideoPlayer.setIsTouchWiget(true);
        gsyVideoPlayer.setRotateViewAuto(false);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentState = gsyVideoPlayer.getCurrentState();
                if(currentState == GSYVideoPlayer.CURRENT_STATE_PAUSE){
                    gsyVideoPlayer.onVideoResume();

                }else if(currentState == GSYVideoPlayer.CURRENT_STATE_PLAYING){
                    gsyVideoPlayer.onVideoPause();
                }else if(currentState == GSYVideoPlayer.CURRENT_STATE_AUTO_COMPLETE){
                    gsyVideoPlayer.startPlayLogic();

                }else{
                    if(isRefreshNet){
                        mRefreshListener.onRefreshVideo();
                        isRefreshNet = false;
                    }else{
                        if(vidUrl != null && vidName != null){
                            gsyVideoPlayer.setUp(vidUrl,false,vidName);
                            gsyVideoPlayer.startPlayLogic();
                        }
                    }

                }
            }
        });

        gsyVideoPlayer.setStandardVideoAllCallBack(new SampleListener() {
            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                //开始播放了才能旋转和全屏
                orientationUtils.setEnable(true);
                isPlay = true;
                gsyVideoPlayer.myHideAllWidget();
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                super.onEnterFullscreen(url, objects);
                if(backBtn != null){
                    backBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
            }

            @Override
            public void onClickStartError(String url, Object... objects) {
                super.onClickStartError(url, objects);
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
//                isStartFullScreen = true;
                if(backBtn != null){
                    backBtn.setVisibility(View.GONE);
                }

                if (orientationUtils != null) {
                    orientationUtils.backToProtVideo();
                }
                isFullScreen = false;
            }
        });
        gsyVideoPlayer.setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils.setEnable(!lock);
                }
            }
        });

        fullScreenView = gsyVideoPlayer.getFullscreenButton();
        fullScreenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFullScreen();
            }
        });
    }

    public boolean isFullScreen(){
        return isFullScreen;
    }

    public View getFullScreenView(){
        return fullScreenView;
    }

    public void startFullScreen(){
//        isStartFullScreen = true;

        gsyVideoPlayer.setIfCurrentIsFullscreen(true);
        orientationUtils.resolveByClick();
        //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
        gsyVideoPlayer.startWindowFullscreen(activity, true, true);
    }

    public void setRefresh(){
        isRefreshNet = true;
        gsyVideoPlayer.changeRefreshUI();


    }

    public void onConfigurationChanged(Configuration newConfig){
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            isFullScreen = true;
        }else {
            isFullScreen = false;
        }
        //如果旋转了就全屏
        if (isPlay) {
            if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
                gsyVideoPlayer.setIfCurrentIsFullscreen(isFullScreen);
            }
            gsyVideoPlayer.onConfigurationChanged(activity, newConfig, orientationUtils);
        }
    }

    public void startPlay(String vidName,String vidUrl){
        this.vidUrl = vidUrl;
        this.vidName = vidName;
        if(startBtn != null){
            startBtn.setVisibility(View.VISIBLE);
        }
        clearCacheFileTime();
        //加入视频的缓存，下次播放直接播放缓存文件，不在消耗流量
        gsyVideoPlayer.setUp(vidUrl,true, new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "seawidow" + File.separator + "CACHE_VIDEO" + File.separator),vidName);
        gsyVideoPlayer.startPlayLogic();
    }

    //清除缓存目录下的过时的，命名错误的文件
    private void clearCacheFileTime(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "seawidow" + File.separator + "CACHE_VIDEO" + File.separator);

        long millSecondsNow = System.currentTimeMillis();
        if (file.exists()) {
            File[] fileList = file.listFiles();
            for(int i = 0;i<fileList.length;i++){

                if (fileList[i].exists()) {
                    String fileName = fileList[i].getName();

                    if (!fileName.endsWith(".mp4")) {//删除格式异常的缓存文件
                        fileList[i].delete();
                        continue;
                    }

                    Date date = new Date(fileList[i].lastModified());
                    long millSeconds = date.getTime();
                    long differenceMillSeconds = millSecondsNow - millSeconds;
                    if (differenceMillSeconds > 2 * 24 * 60 * 60 * 1000) {//删除两天以前的缓存文件，避免缓存文件夹占用太大
                        fileList[i].delete();
                    }
                }
            }
            long unUsed = getmem_UNUSED();
            if (unUsed < 500) {
                deleteDir(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "ipdvhero" + File.separator + "CACHE_VIDEO" + File.separator);
            }
        }
    }

    private long getmem_UNUSED(){
        long MEM_UNUSED;
        // 得到ActivityManager
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        // 创建ActivityManager.MemoryInfo对象
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // 取得剩余的内存空间
        MEM_UNUSED = mi.availMem / 1024;
        return MEM_UNUSED;
    }

    private void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirWihtFile(dir);
    }

    private void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public boolean backToProtvideo(){
        if(isFullScreen){
            if(orientationUtils != null){
                orientationUtils.backToProtVideo();
                return true;
            }
        }
        return false;
    }

    public void close(){
        if(gsyVideoPlayer != null){
            GSYVideoPlayer.releaseAllVideos();
        }
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    public void registerListener(RefreshListener refreshListener){
        mRefreshListener = refreshListener;
    }

    public interface RefreshListener{
        void onRefreshVideo();
    }
}
