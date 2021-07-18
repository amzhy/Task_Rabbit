package com.example.myapplication;

import androidx.annotation.Nullable;

public class ChatBox {
    private String taskID;
    private String senderID;
    private String receiverID;
    private int unread;
    private int alsoUnread;
    private boolean isAlsoLast = false;
    private boolean isLast = false;

    public ChatBox(String taskID, String senderID, String receiverID) {
        this.taskID = taskID;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.unread = 0;
        this.alsoUnread = 0;
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        if (obj instanceof ChatBox) {
            ChatBox box = (ChatBox) obj;
            Boolean sameTask = this.taskID.equals(box.taskID);
            Boolean sameUser = this.senderID.equals(box.senderID)
                    ? this.receiverID.equals(box.receiverID)
                    : this.senderID.equals(box.receiverID)
                        ? this.receiverID.equals(box.senderID)
                        : false;

            return sameTask&&sameUser;
        } else {
            return false;
        }
    }

    public String getTaskID() {
        return taskID;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public int getUnread() {
        return unread;
    }

    public boolean isAlsoLast() {
        return isAlsoLast;
    }

    public void setAlsoLast() {
        isAlsoLast = true;
    }

    public int getAlsoUnread() {
        return alsoUnread;
    }

    public void setLast() {
        isLast = true;
    }

    public boolean getLast() {
        return isLast;
    }

    public void addUnread(){
        this.unread+=1;
    }

    public void addAlsoUnread(){
        this.alsoUnread+=1;
    }
}
