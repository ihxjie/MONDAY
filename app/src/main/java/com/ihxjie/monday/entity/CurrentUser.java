package com.ihxjie.monday.entity;

import java.io.Serializable;

public class CurrentUser implements Serializable {

    public String userid;

    public String name;

    public String avatar;

    public String email;

    public String phone;

    public String signature;

    public String title;

    public String group;

    public String notifyCount;

    public String unreadCount;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getNotifyCount() {
        return notifyCount;
    }

    public void setNotifyCount(String notifyCount) {
        this.notifyCount = notifyCount;
    }

    public String getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(String unreadCount) {
        this.unreadCount = unreadCount;
    }

    @Override
    public String toString() {
        return "CurrentUser{" +
                "userid='" + userid + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", signature='" + signature + '\'' +
                ", title='" + title + '\'' +
                ", group='" + group + '\'' +
                ", notifyCount='" + notifyCount + '\'' +
                ", unreadCount='" + unreadCount + '\'' +
                '}';
    }
}
