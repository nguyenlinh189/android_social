package com.example.btl_v2.model;

import java.io.Serializable;

public class Notification implements Serializable {
    private String notificationBy;
    private long notificationAt;
    private String type;
    private String postID;
    private String notificationID;
    private String postedBy;
    private boolean checkOpen;
    private boolean checknotification;

    public Notification() {
        checkOpen=false;
    }

    public Notification(String notificationBy, long notificationAt, String type, String postID, String postedBy, boolean checkOpen) {
        this.notificationBy = notificationBy;
        this.notificationAt = notificationAt;
        this.type = type;
        this.postID = postID;
        this.postedBy = postedBy;
        this.checkOpen = checkOpen;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getNotificationBy() {
        return notificationBy;
    }

    public void setNotificationBy(String notificationBy) {
        this.notificationBy = notificationBy;
    }

    public long getNotificationAt() {
        return notificationAt;
    }

    public void setNotificationAt(long notificationAt) {
        this.notificationAt = notificationAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public boolean isCheckOpen() {
        return checkOpen;
    }

    public void setCheckOpen(boolean checkOpen) {
        this.checkOpen = checkOpen;
    }

    public boolean isChecknotification() {
        return checknotification;
    }

    public void setChecknotification(boolean checknotification) {
        this.checknotification = checknotification;
    }
}
