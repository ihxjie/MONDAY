package com.ihxjie.monday.api;

import com.ihxjie.monday.bean.Course;

import java.util.List;

public interface CourseSpider {
    List<Course> QueryFJUTCourses(String username, String password);
}
