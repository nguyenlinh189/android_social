package com.example.btl_v2.model;

import java.io.Serializable;

public class Chat implements Serializable {
    private String chatId;
    private String friendId;

    public Chat(String chatId, String friendId) {
        this.chatId = chatId;
        this.friendId = friendId;
    }

    public Chat() {
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }
}
