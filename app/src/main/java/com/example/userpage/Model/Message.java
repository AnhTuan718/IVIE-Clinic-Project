package com.example.userpage.Model;

public class Message {
    private String content;
    private boolean isUser; // true if sent by user, false if from doctor
    private String time;

    public Message(String content, boolean isUser, String time) {
        this.content = content;
        this.isUser = isUser;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public boolean isUser() {
        return isUser;
    }

    public String getTime() {
        return time;
    }
}