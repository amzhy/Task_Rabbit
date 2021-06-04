package com.example.myapplication;

import android.media.Image;
import android.view.View;

import java.sql.Time;
import java.util.Date;

public class NewTask {
    private String title, description, location;
    private double price;
    private String date, id;
    //private View image;

    public NewTask(String title, String description, String location, double price, String date, String id){
        this.date = date;
        this.description = description;
        this.title = title;
        this.location = location;
        this.price = price;
        this.id = id;
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

    public void setPrice(double price) {
        this.price = price;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public double getPrice() {
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
}
