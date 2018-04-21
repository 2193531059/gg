package com.ezreal.chat.video_player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MyCustomGSYPlayer extends NormalGSYVideoPlayer {
    private static final String TAG = "MyCustomGSYPlayer";
    private Context mContext;

    public MyCustomGSYPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
        mContext = context;
    }

    public MyCustomGSYPlayer(Context context) {
        super(context);
        mContext = context;
    }

    public MyCustomGSYPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        VideoOptionModel videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);
        GSYVideoManager.instance().setOptionModelList(list);
    }

    public void myHideAllWidget(){
        super.hideAllWidget();
    }

    public void mychangeUiToNormal(){
        super.changeUiToNormal();
    }

    public void changeRefreshUI(){
        mStartButton.setVisibility(View.VISIBLE);
    }
}
