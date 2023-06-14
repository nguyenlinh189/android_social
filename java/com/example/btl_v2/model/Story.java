package com.example.btl_v2.model;

import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.util.ArrayList;

// mot cai thi co the co nhieu story
public class Story implements Serializable {
    private String storyBy;
    private long storyAt;
    private ArrayList<UserStories> stories;

    public Story() {
    }

    public String getStoryBy() {
        return storyBy;
    }

    public void setStoryBy(String storyBy) {
        this.storyBy = storyBy;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }

    public ArrayList<UserStories> getStories() {
        return stories;
    }

    public void setStories(ArrayList<UserStories> stories) {
        this.stories = stories;
    }
}
