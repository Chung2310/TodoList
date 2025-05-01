package com.example.todolist.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Task implements Serializable {
    private String ID;
    private String userId;
    private String title;
    private String description;
    private String status;
    private Timestamp dueDateTime;


    // Getters and setters

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getDueDateTime() {
        return dueDateTime;
    }

    public void setDueDateTime(Timestamp dueDateTime) {
        this.dueDateTime = dueDateTime;
    }
}
