package com.ihxjie.monday.bean;

import org.jetbrains.annotations.NotNull;

public class Course {
    private String courseName;//课程名称

    private String courseTime;//第几节上课

    private String coursePlace;//在哪里上课

    private String courseTeacher;//老师

    private String courseWeek;//哪几周上课

    private String campus;//校区

    private String xqj;//星期几

    @Override
    public String toString() {
        return "Course{" +
                "courseName='" + courseName + '\'' +
                ", courseTime='" + courseTime + '\'' +
                ", coursePlace='" + coursePlace + '\'' +
                ", courseTeacher='" + courseTeacher + '\'' +
                ", courseWeek='" + courseWeek + '\'' +
                ", campus='" + campus + '\'' +
                ", xqj='" + xqj + '\'' +
                '}';
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
    }

    public String getCoursePlace() {
        return coursePlace;
    }

    public void setCoursePlace(String coursePlace) {
        this.coursePlace = coursePlace;
    }

    public String getCourseTeacher() {
        return courseTeacher;
    }

    public void setCourseTeacher(String courseTeacher) {
        this.courseTeacher = courseTeacher;
    }

    public String getCourseWeek() {
        return courseWeek;
    }

    public void setCourseWeek(String courseWeek) {
        this.courseWeek = courseWeek;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getXqj() {
        return xqj;
    }

    public void setXqj(String xqj) {
        this.xqj = xqj;
    }
}
