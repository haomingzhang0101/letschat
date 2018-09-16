package com.haoming.vo;

/**
 * Value object for the sender of a request.
 * @author zhanghm
 * @date 2018-09-15 21:07
 */
public class FriendRequestVO {
    private String sendUserId;
    private String sendUserName;
    private String sendFaceImage;
    private String sendNickName;

    public String getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(String sendUserId) {
        this.sendUserId = sendUserId;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public String getSendFaceImage() {
        return sendFaceImage;
    }

    public void setSendFaceImage(String sendFaceImage) {
        this.sendFaceImage = sendFaceImage;
    }

    public String getSendNickName() {
        return sendNickName;
    }

    public void setSendNickName(String sendNickName) {
        this.sendNickName = sendNickName;
    }
}