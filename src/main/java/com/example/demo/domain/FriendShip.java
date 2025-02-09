package com.example.demo.domain;

import java.time.LocalDateTime;


public class FriendShip extends Entity<Tuple<Long,Long>> {

    public LocalDateTime date;
    long id1;
    long id2;

    public FriendShip(long id1, long id2, LocalDateTime date) {
        this.id2 = id2;
        this.id1 = id1;
        this.date = date;
        this.setId(new Tuple<>(id1, id2));
    }

    public long getId1() {
        return id1;
    }

    public long getId2() {
        return id2;
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
        return id1+" "+id2+" "+date.getDayOfMonth()+" "+date.getMonth()+" "+date.getYear()+" "+date.getHour()+":"+date.getMinute();
    }
}
