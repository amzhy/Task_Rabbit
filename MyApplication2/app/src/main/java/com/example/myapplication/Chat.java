package com.example.myapplication;


public class Chat {
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
}
