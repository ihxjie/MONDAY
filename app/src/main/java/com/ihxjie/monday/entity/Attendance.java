package com.ihxjie.monday.entity;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author xjie
 * @date 2021/4/20 15:20
 */
public class Attendance {
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

    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", clazzId=" + clazzId +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", attendanceType=" + attendanceType +
                '}';
    }
}
