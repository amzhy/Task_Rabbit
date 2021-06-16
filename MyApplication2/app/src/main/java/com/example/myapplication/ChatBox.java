package com.example.myapplication;

import androidx.annotation.Nullable;

public class ChatBox {
    private String taskID;
    private String senderID;
    private String receiverID;

    public ChatBox(String taskID, String senderID, String receiverID) {
        this.taskID = taskID;
        this.senderID = senderID;
        this.receiverID = receiverID;
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
//            Boolean sameSender = this.senderID.equals(box.senderID) || this.senderID.equals(box.receiverID);
//            Boolean sameReceiver = this.receiverID.equals(box.senderID) || this.receiverID.equals(box.receiverID);

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
}
