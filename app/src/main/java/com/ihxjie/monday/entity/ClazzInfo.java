package com.ihxjie.monday.entity;

/**
 * <p>
 * 班级
 * </p>
 *
 * @author xjie
 * @since 2021-04-13
 */
public class ClazzInfo {

    public String id;

    public String title;

    public String logo;

    public String description;

    public String updatedAt;

    public String teacher;

    public String href;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return "Clazz{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", logo='" + logo + '\'' +
                ", description='" + description + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", teacher='" + teacher + '\'' +
                ", href='" + href + '\'' +
                '}';
    }
}
