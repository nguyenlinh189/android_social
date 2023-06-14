package com.example.btl_v2.model;

import java.io.Serializable;

public class Post implements Serializable {
    private String postId;
    private String postImgae;
    private String postedBy;
    private String postContent;
    private long postAt;
    private int postLike;
    private int commentCount;

    public int getPostLike() {
        return postLike;
    }

    public void setPostLike(int postLike) {
        this.postLike = postLike;
    }

    public Post() {
    }

    public Post(String postId, String postImgae, String postedBy, String postContent, long postAt) {
        this.postId = postId;
        this.postImgae = postImgae;
        this.postedBy = postedBy;
        this.postContent = postContent;
        this.postAt = postAt;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImgae() {
        return postImgae;
    }

    public void setPostImgae(String postImgae) {
        this.postImgae = postImgae;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public long getPostAt() {
        return postAt;
    }

    public void setPostAt(long postAt) {
        this.postAt = postAt;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
