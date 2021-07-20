package com.example.myapplication;


public class Chat implements Comparable<Chat>{
    private String receiver;
    private String sender;
    private String taskID;
    private String message;
    private String tag;
    private String isLast;
    private String isAlsoLast;
    private boolean Admin;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Chat(String sender, String receiver, String message, String task, String isLast, String isAlsoLast, boolean Admin) {
        this.taskID = task;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isLast = isLast;
        this.isAlsoLast = isAlsoLast;
        this.Admin = Admin;
    }

    public Chat(){ }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public String getTaskID() {
        return taskID;
    }

    public String getMessage() {
        return message;
    }

    public String getIsLast() {
        return isLast;
    }

    public boolean isAdmin() {
        return Admin;
    }

    public void setAdmin(boolean admin) {
        Admin = admin;
    }

    public String getIsAlsoLast() {
        return isAlsoLast;
    }
    public boolean getAlsoLast() {
        return Boolean.parseBoolean(isAlsoLast);
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public void setIsLast(String isLast) {
        this.isLast = isLast;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getLast() {
        return Boolean.parseBoolean(isLast);
    }

    public void setIsAlsoLast(String isAlsoLast) {
        this.isAlsoLast = isAlsoLast;
    }

    @Override
    public int compareTo(Chat o) {
        if (o.getTaskID().equals(this.getTaskID()) && o.getSender().equals(this.getSender())
        && o.getReceiver().equals(this.getReceiver()) && o.getMessage().equals(this.getMessage())) {
            return 0;
        }
        return 1;
    }


    @Override
    public String toString() {
        return "Chat{" +
                "receiver='" + receiver + '\'' +
                ", sender='" + sender + '\'' +
                ", taskID='" + taskID + '\'' +
                ", message='" + message + '\'' +
                ", tag='" + tag + '\'' +
                ", isLast='" + isLast + '\'' +
                ", isAlsoLast='" + isAlsoLast + '\'' +
                ", Admin=" + Admin +
                '}';
    }
}
