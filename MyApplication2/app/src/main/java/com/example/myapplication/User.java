package com.example.myapplication;

import android.widget.ImageView;

import java.util.List;

public class User {
    private String userId;
    List<NewTask> tasks;

    public User(String userId, List<NewTask> tasks) {
        this.userId = userId;
        this.tasks = tasks;
    }

    public String getUserId() {
        return userId;
    }

    public List<NewTask> getTasks() {
        return tasks;
    }

}
