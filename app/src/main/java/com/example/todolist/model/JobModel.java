package com.example.todolist.model;

import java.util.List;

public class JobModel {
    private boolean success;
    private String message;
    private List<Task> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Task> getResult() {
        return result;
    }

    public void setResult(List<Task> result) {
        this.result = result;
    }
}
