package com.example.btl_v2.model;

import java.io.Serializable;

public class UserStories implements Serializable {
    private String image;
    private long storyAt;

    public UserStories() {
    }

    public UserStories(String image, long storyAt) {
        this.image = image;
        this.storyAt = storyAt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }
}
