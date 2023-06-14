package com.example.btl_v2.model;

import java.io.Serializable;

public class Message implements Serializable {
    private String uId,message;
    private Long timestamp;
    private String MessId;
    private int feeling=-1;
    private String image;
    private String file;

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    private String nameFile;

    public Message(String uId, String message, Long timestamp) {
        this.uId = uId;
        this.message = message;
        this.timestamp = timestamp;
        this.feeling=-1;
    }

    public Message(String uId, String message) {
        this.uId = uId;
        this.message = message;
        this.feeling=-1;
    }

    public String getMessId() {
        return MessId;
    }

    public void setMessId(String messId) {
        MessId = messId;
    }

    public Message() {
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
