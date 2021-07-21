package com.example.myapplication;

public class Commenter {
    private String id, comment, name;
    private double rating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Commenter(String id, String comment, String name, double rating) {
        this.id = id;
        this.comment = comment;
        this.name = name;
        this.rating = rating;
    }
    public Commenter(String id, String comment,double rating) {
        this.id = id;
        this.comment = comment;
        this.rating = rating;
    }
}
