package com.example.myapplication;


public class Chat implements Comparable<Chat>{
    private String receiver;
    private String sender;
    private String taskID;
    private String message;
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Chat(String sender, String receiver, String message, String task) {
        this.taskID = task;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
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

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int compareTo(Chat o) {
        if (o.getTaskID().equals(this.getTaskID()) && o.getSender().equals(this.getSender())
        && o.getReceiver().equals(this.getReceiver()) && o.getMessage().equals(this.getMessage())) {
            return 0;
        }
        return 1;
    }
}
