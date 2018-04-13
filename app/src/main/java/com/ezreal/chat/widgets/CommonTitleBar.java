package com.ezreal.chat.widgets;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ezreal.chat.listener.GlobalLimitClickOnClickListener;
import com.ezreal.ezchat.R;

/**
 * 首页标题栏
 * Created by Administrator on 2017/5/12.
 */
public class CommonTitleBar extends RelativeLayout {

    // 防重复点击时间
    private static final int BTN_LIMIT_TIME = 500;

    private TextView leftButton;
    private ImageView leftButtonImg;
    private TextView middleButton;
    private TextView rightButton;
    private ImageView rightButtonImg;
    public int leftBtnIconId;
    private String leftBtnStr;
    private String titleTxtStr;
    private String rightBtnStr;
    public int rightBtnIconId;
    public LinearLayout right_area;
    public LinearLayout left_area;

    public CommonTitleBar(Context context) {
        super(context);
    }

    public CommonTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBar);
        // 如果后续有文字按钮，可使用该模式设置
        leftBtnStr = arr.getString(R.styleable.CommonTitleBar_leftBtnText);
        leftBtnIconId = arr.getResourceId(R.styleable.CommonTitleBar_leftBtnIcon, 0);
        titleTxtStr = arr.getString(R.styleable.CommonTitleBar_titleText);
        rightBtnStr = arr.getString(R.styleable.CommonTitleBar_rightBtnText);
        rightBtnIconId = arr.getResourceId(R.styleable.CommonTitleBar_rightBtnIcon, 0);
        if (isInEditMode()) {
            LayoutInflater.from(context).inflate(R.layout.view_title_bar, this);
            return;
        }
        right_area = (LinearLayout) findViewById(R.id.title_right_area);
        left_area = (LinearLayout) findViewById(R.id.title_left_area);
        LayoutInflater.from(context).inflate(R.layout.view_title_bar, this);
//        findViewById(R.id.title_out_frame).setBackgroundResource(R.color.blue);
        arr.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) {
            return;
        }
        leftButtonImg = (ImageView) findViewById(R.id.title_left_btn);
        leftButton = (TextView) findViewById(R.id.title_left);
        middleButton = (TextView) findViewById(R.id.title_middle);
        rightButtonImg = (ImageView) findViewById(R.id.title_right_btn);
        rightButton = (TextView) findViewById(R.id.title_right);

        setLeftButtonImg(leftBtnIconId);
        setRightButtonImg(rightBtnIconId);
        setLeftTxtBtn(leftBtnStr);
        setTitleTxt(titleTxtStr);
        setRightTxtBtn(rightBtnStr);
    }

    public void setLeftButtonImg(int resourceId) {
        if (resourceId != 0) {
            leftButtonImg.setImageResource(resourceId);
            leftButtonImg.setVisibility(View.VISIBLE);
        } else {
            leftButtonImg.setVisibility(View.GONE);
        }
    }

    public String getRightBtnStr() {
        return rightBtnStr;
    }

    public void setRightButtonImg(int resourceId) {
        if (resourceId != 0) {
            rightButtonImg.setImageResource(resourceId);
            rightButtonImg.setVisibility(View.VISIBLE);
        } else {
            rightButtonImg.setVisibility(View.GONE);
        }
        rightBtnIconId = resourceId;
    }

    public void setRightButtonImg(Drawable drawable) {
        if (drawable != null) {
            rightButtonImg.setBackground(drawable);
            rightButtonImg.setVisibility(View.VISIBLE);
        } else {
            rightButtonImg.setVisibility(View.GONE);
        }
    }

    public String getRightTxt(){
        return rightButton.getText().toString();
    }

    public void setRightTxtBtn(String btnTxt) {
        if (!TextUtils.isEmpty(btnTxt)) {
            rightButton.setText(btnTxt);
            rightButton.setVisibility(View.VISIBLE);
        } else {
            rightButton.setVisibility(View.GONE);
        }
    }

    public void setLeftTxtBtn(String leftBtnStr) {
        if (!TextUtils.isEmpty(leftBtnStr)) {
            leftButton.setText(leftBtnStr);
            leftButton.setVisibility(View.VISIBLE);
        } else {
            leftButton.setVisibility(View.GONE);
        }
    }

    public void setTitleTxt(String title) {
        if (!TextUtils.isEmpty(title)) {
            middleButton.setText(title);
            middleButton.setVisibility(View.VISIBLE);
        } else {
            middleButton.setVisibility(View.GONE);
        }
    }

    //    //隐藏左边按钮
    public void hideLeftBtn() {
        findViewById(R.id.title_left_area).setVisibility(View.GONE);
    }

    //    //隐藏右边按钮
    public void hideRightBtn() {
        findViewById(R.id.title_right_area).setVisibility(View.GONE);
    }

    public void showRightBtn(){
        findViewById(R.id.title_right_area).setVisibility(View.VISIBLE);
    }

    public void setLeftBtnOnclickListener(OnClickListener listener) {
        OnClickListener myListener = new GlobalLimitClickOnClickListener(listener, BTN_LIMIT_TIME);
        findViewById(R.id.title_left_area).setOnClickListener(myListener);
    }

    public void setRightBtnOnclickListener(OnClickListener listener) {
        OnClickListener myListener = new GlobalLimitClickOnClickListener(listener, BTN_LIMIT_TIME);
        findViewById(R.id.title_right_area).setOnClickListener(myListener);
    }

}