package com.example.demo.service;

import com.example.demo.domain.Entity;

import java.util.List;

public interface AbstractService<ID, E extends Entity<ID>> {
    public void addEntity(E entity);
    public void removeEntity(ID id);

    public Iterable<E> getEntities();
}
