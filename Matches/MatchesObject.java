package com.src.magakim.dogpark.Matches;

public class MatchesObject {
    private String userId;
    private String name;
    private String profileImageUrl;
    private String unreadMessage;
    private int pos;
    public MatchesObject (String userId, String name, String profileImageUrl, String unreadMessage) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.unreadMessage = unreadMessage;
    }

    public int getPos() {return pos;}
    public void setPos(int pos) {this.pos = pos;}

    public String getUnreadMessage() {return unreadMessage;}
    public void setUnreadMessage(String unreadMessage) {this.unreadMessage = unreadMessage;}

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = userId;
    }

}
