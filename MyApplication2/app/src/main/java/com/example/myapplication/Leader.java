package com.example.myapplication;

import java.util.Objects;

public class Leader {
    private String photo, name;
    private double earning;
    private String userID;

    public Leader(String photo, String name, double earning, String userID) {
        this.photo = photo;
        this.name = name;
        this.earning = earning;
        this.userID = userID;
    }
    public Leader(double earning, String userID) {
        this.photo = photo;
        this.name = name;
        this.earning = earning;
        this.userID = userID;
    }

    public void addEarning(double i) {
        this.earning = earning + i;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getEarning() {
        return earning;
    }

    public void setEarning(double earning) {
        this.earning = earning;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leader leader = (Leader) o;
        return Objects.equals(userID, leader.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID);
    }

    @Override
    public String toString() {
        return "Leader{" +
                "earning=" + earning +
                ", userID='" + userID + '\'' +
                '}';
    }
}
