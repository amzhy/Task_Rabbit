package com.example.myapplication;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class StoreSetting {

    private boolean leaderboard, task_status, inbox;
    private String tasker_alert;

    public StoreSetting(boolean leaderboard, boolean task_status, boolean inbox, String tasker_alert) {
        this.leaderboard = leaderboard;
        this.task_status = task_status;
        this.inbox = inbox;
        this.tasker_alert = tasker_alert;
    }


    //default setting
    public StoreSetting() {
        this.leaderboard = true;
        this.task_status = true;
        this.inbox = true;
        this.tasker_alert = "10min";
    }

    public boolean isLeaerboard() {
        return leaderboard;
    }

    public void setLeaerboard(boolean leaerboard) {
        this.leaderboard = leaerboard;
    }

    public boolean isTask_status() {
        return task_status;
    }

    public void setTask_status(boolean task_status) {
        this.task_status = task_status;
    }

    public boolean isInbox() {
        return inbox;
    }

    public void setInbox(boolean inbox) {
        this.inbox = inbox;
    }

    public String getTasker_alert() {
        return tasker_alert;
    }

    public void setTasker_alert(String tasker_alert) {
        this.tasker_alert = tasker_alert;
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "store settings";
    }
}
