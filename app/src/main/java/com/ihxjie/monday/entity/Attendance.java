package com.ihxjie.monday.entity;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author xjie
 * @date 2021/4/20 15:20
 */
public class Attendance implements Serializable {
    /**
     * 签到id
     */
    private Long attendanceId;

    /**
     * 签到班级
     */
    private Integer clazzId;

    /**
     * 签到开始时间
     */
    private String startTime;

    /**
     * 签到结束时间
     */
    private String endTime;

    /**
     * 签到方式（普通签到：1；二维码签到：2；地理位置签到：3；人脸识别签到：4；人脸+地理位置签到：5；地理位置+二维码签到：6）
     */
    private Integer attendanceType;

    /**
     * 签到经度
     */
    private String attLongitude;

    /**
     * 签到纬度
     */
    private String attLatitude;

    /**
     * 签到精度
     */
    private String attAccuracy;

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Integer getClazzId() {
        return clazzId;
    }

    public void setClazzId(Integer clazzId) {
        this.clazzId = clazzId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(Integer attendanceType) {
        this.attendanceType = attendanceType;
    }

    public String getAttLongitude() {
        return attLongitude;
    }

    public String getAttLatitude() {
        return attLatitude;
    }

    public String getAttAccuracy() {
        return attAccuracy;
    }

    @NotNull
    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", clazzId=" + clazzId +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", attendanceType=" + attendanceType +
                ", attLongitude='" + attLongitude + '\'' +
                ", attLatitude='" + attLatitude + '\'' +
                ", attAccuracy='" + attAccuracy + '\'' +
                '}';
    }
}
