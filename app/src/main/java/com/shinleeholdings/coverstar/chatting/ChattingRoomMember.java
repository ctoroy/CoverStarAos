package com.shinleeholdings.coverstar.chatting;

public class ChattingRoomMember {
    private String userId;
    private String userNickName;
    private String userPhoto;

    public String getUserId() {
        return userId;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }
}
