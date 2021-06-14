package com.example.myapplication;

import java.util.UUID;

public class Chat {
    private String publisherId;
    private String taskerId;
    private String chatId;

    public Chat(String publisherId, String taskerId) {
        this.chatId = UUID.randomUUID().toString();
        this.publisherId = publisherId;
        this.taskerId = taskerId;
    }

    public String getChatId() {
        return chatId;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public String getTaskerId() {
        return taskerId;
    }
}
