package com.example.myapplication;

public class StoreProfile {
    private String name, hp, address;

    public StoreProfile() { }

    public StoreProfile(String name, String hp, String address) {
        this.name = name;
        this.hp = hp;
        this.address = address;
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

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }
}
