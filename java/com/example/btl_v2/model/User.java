package com.example.btl_v2.model;

import java.io.Serializable;

public class User implements Serializable {
    private String name,profession,email;
    private String coverPhoto,profile_image;
    private String userID;
    private int followerCount;
    private boolean status;
    private String token;

    public User() {
        this.status=true;
    }
    public User(String name, String profession, String email) {
        this.name = name;
        this.profession = profession;
        this.email = email;
    }

    public User(String name, String profession, String email, String coverPhoto, String profile_image, String userID) {
        this.name = name;
        this.profession = profession;
        this.email = email;
        this.coverPhoto = coverPhoto;
        this.profile_image = profile_image;
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
