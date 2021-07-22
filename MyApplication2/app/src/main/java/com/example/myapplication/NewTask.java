package com.example.myapplication;

import android.media.Image;
import android.view.View;

import java.sql.Time;
import java.util.Date;

public class NewTask {
    private String title, description, location, price, category;
    private String date, userId, time, taskId, taskerId;
    // "-1" = incomplete, "0" = progress, "1" = completed
    private String tag;

    public NewTask(String title, String description, String location, String price, String date,
                   String time, String userId, String taskId, String tag, String taskerId, String category){
        this.date = date;
        this.description = description;
        this.title = title;
        this.location = location;
        this.price = price;
        this.taskId = taskId;
        this.userId = userId;
        this.time = time;
        this.tag = tag;
        this.taskerId = taskerId;
        this.category = category;
    }

    public NewTask(){ }
    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTaskerId() {
        return taskerId;
    }

    public void setTaskerId(String taskerId) {
        this.taskerId = taskerId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public String getPrice() {
        return price;
    }

//    public View getImage() {
//        return image;
//    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getTitle() {
        return title;
    }

    public String getUserId() {
        return userId;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTime() {
        return time;
    }

    public String getCategory() {
        return category;
    }
}
