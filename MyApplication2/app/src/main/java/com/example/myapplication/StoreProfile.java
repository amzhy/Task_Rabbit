package com.example.myapplication;

public class StoreProfile {
    private String name, hp, tasker_interval;


    public StoreProfile() { }

    public StoreProfile(String name, String hp) {
        this.name = name;
        this.hp = hp;
    }

    public String getTasker_interval() {
        return tasker_interval;
    }

    public void setTasker_interval(String tasker_interval) {
        this.tasker_interval = tasker_interval;
    }

    public String getHp() {
        return hp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }

    public String getName() {
        return name;
    }
}

