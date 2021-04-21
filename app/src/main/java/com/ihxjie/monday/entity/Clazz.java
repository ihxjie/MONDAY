package com.ihxjie.monday.entity;

import java.time.LocalDateTime;

public class Clazz {
    private Integer clazzId;

    private String clazzName;

    private String clazzLogo;

    private String clazzDescription;

    private String createdAt;

    private Integer isFinish;

    private Integer activeLevel;

    public Integer getClazzId() {
        return clazzId;
    }

    public void setClazzId(Integer clazzId) {
        this.clazzId = clazzId;
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public String getClazzLogo() {
        return clazzLogo;
    }

    public void setClazzLogo(String clazzLogo) {
        this.clazzLogo = clazzLogo;
    }

    public String getClazzDescription() {
        return clazzDescription;
    }

    public void setClazzDescription(String clazzDescription) {
        this.clazzDescription = clazzDescription;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(Integer isFinish) {
        this.isFinish = isFinish;
    }

    public Integer getActiveLevel() {
        return activeLevel;
    }

    public void setActiveLevel(Integer activeLevel) {
        this.activeLevel = activeLevel;
    }

    @Override
    public String toString() {
        return "Clazz{" +
                "clazzId=" + clazzId +
                ", clazzName='" + clazzName + '\'' +
                ", clazzLogo='" + clazzLogo + '\'' +
                ", clazzDescription='" + clazzDescription + '\'' +
                ", createdAt=" + createdAt +
                ", isFinish=" + isFinish +
                ", activeLevel=" + activeLevel +
                '}';
    }
}
