package com.example.demo.domain;

import java.time.LocalDateTime;


public class LastLogin extends Entity<Long> {

    public LocalDateTime date;
    long id_user;

    @Override
    public String toString() {
        return "LastLogin{" +
                "date=" + date +
                ", id=" + this.getId() +
                ", id_user=" + id_user +
                '}';
    }

    public LastLogin(long id_user, LocalDateTime date) {
        this.id_user = id_user;
        this.date = date;

    }

    /**
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    public long getId_user() {
        return id_user;
    }

    public void setId_user(long id_user) {
        this.id_user = id_user;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
