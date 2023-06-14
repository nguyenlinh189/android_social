package com.example.btl_v2.model;

import java.io.Serializable;

public class Friend implements Serializable {
    private String friendId;
    private String isFriend;
    private long timestamp;

    public Friend() {
    }

    public Friend(String isFriend, long timestamp) {
        this.isFriend = isFriend;
        this.timestamp = timestamp;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String isFriend() {
        return isFriend;
    }

    public void setFriend(String friend) {
        isFriend = friend;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
