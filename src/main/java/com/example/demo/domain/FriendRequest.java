package com.example.demo.domain;

import java.time.LocalDateTime;


public class FriendRequest extends Entity<Tuple<Long,Long>> {

    private LocalDateTime date;
    long idUserFrom;
    long idUserTo;

    public FriendRequest(long idUserFrom, long idUserTo,LocalDateTime date) {
        this.idUserTo = idUserTo;
        this.idUserFrom = idUserFrom;
        this.date = date;
        this.setId(new Tuple<>(idUserFrom, idUserTo));
    }

    public long getIdUserFrom() {
        return idUserFrom;
    }

    public long getIdUserTo() {
        return idUserTo;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return idUserFrom +" "+ idUserTo +" "+date.getDayOfMonth()+" "+date.getMonth()+" "+date.getYear()+" "+date.getHour()+":"+date.getMinute();
    }
}
