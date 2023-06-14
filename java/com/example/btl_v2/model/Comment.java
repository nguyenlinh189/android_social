package com.example.btl_v2.model;

import java.io.Serializable;

public class Comment implements Serializable {
    private String content;
    private long commentAt;
    private String commentBy;
    private String postId;
    private String commentId;

    public Comment(String content, long commentAt, String commentBy) {
        this.content = content;
        this.commentAt = commentAt;
        this.commentBy = commentBy;
    }

    public Comment() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCommentAt() {
        return commentAt;
    }

    public void setCommentAt(long commentAt) {
        this.commentAt = commentAt;
    }

    public String getCommentBy() {
        return commentBy;
    }

    public void setCommentBy(String commentBy) {
        this.commentBy = commentBy;
    }
}
