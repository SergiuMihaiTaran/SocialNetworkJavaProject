package com.example.demo.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long>{
    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime date;
    private Message reply;
    public Message(User from, List<User> to, String message, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply = null;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public User getFrom() {
        return from;
    }

    public List<User> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }
    public String toString(){
        return getMessage();
    }
}
