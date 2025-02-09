package com.example.demo.service;

import com.example.demo.domain.Entity;
import com.example.demo.domain.LastLogin;
import com.example.demo.repository.database.LastLoginDataBaseRepository;

import java.util.Optional;

public class ServiceLastLogin implements AbstractService{
    private LastLoginDataBaseRepository repo;

    public ServiceLastLogin(LastLoginDataBaseRepository repo) {
        this.repo = repo;
    }

    @Override
    public void addEntity(Entity entity) {
        repo.save((LastLogin) entity);
    }

    @Override
    public void removeEntity(Object o) {
        repo.delete(((LastLogin) o).getId());
    }

    @Override
    public Iterable getEntities() {
        return repo.findAll();
    }
    public Optional<LastLogin> getLastLogin(Long id) {
     return repo.findOne(id);
    }
    public Optional<LastLogin> update(LastLogin entity) {
        return repo.update(entity);
    }
}
