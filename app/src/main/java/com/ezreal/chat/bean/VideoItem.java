package com.ezreal.chat.bean;

import com.ezreal.chat.utils.UploadState;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/13.
 */

public class VideoItem implements Serializable{
    private String title;
    private String filePath;
    private String creatTime;
    private UploadState uploadState;

    public UploadState getUploadState() {
        return uploadState;
    }

    public void setUploadState(UploadState uploadState) {
        this.uploadState = uploadState;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }
}
