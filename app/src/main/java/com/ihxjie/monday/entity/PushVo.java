package com.ihxjie.monday.entity;

import java.io.Serializable;

public class PushVo implements Serializable {
    /**
     * 用户id
     */
    private Integer userId;

    /**
     * regId
     */
    private String regId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    @Override
    public String toString() {
        return "PushVo{" +
                "userId=" + userId +
                ", regId='" + regId + '\'' +
                '}';
    }
}
