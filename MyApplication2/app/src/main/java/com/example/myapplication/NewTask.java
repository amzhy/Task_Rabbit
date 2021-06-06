package com.example.myapplication;

import android.media.Image;
import android.view.View;

import java.sql.Time;
import java.util.Date;

public class NewTask {
    private String title, description, location;
    private String price;
    private String date, id, time;
    //private View image;

    public NewTask(String title, String description, String location, String price, String date, String time, String id){
        this.date = date;
        this.description = description;
        this.title = title;
        this.location = location;
        this.price = price;
        this.id = id;
        this.time = time;
        //this.image = image;
    }

    public NewTask(){}
    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public void setImage(View image) {
//        this.image = image;
//    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }
}
