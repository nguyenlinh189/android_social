package com.example.btl_v2.model;

import java.io.Serializable;

public class Reel implements Serializable {
    private String videoUrl;
    private String key;
    private String content;
    private long reelAt;
    private String reelBy;

    public Reel() {
    }

    public Reel(String videoUrl, String key) {
        this.videoUrl = videoUrl;
        this.key = key;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getReelAt() {
        return reelAt;
    }

    public void setReelAt(long reelAt) {
        this.reelAt = reelAt;
    }

    public String getReelBy() {
        return reelBy;
    }

    public void setReelBy(String reelBy) {
        this.reelBy = reelBy;
    }
}
