package com.example.demo.service;

import com.example.demo.domain.Entity;
import com.example.demo.domain.User;
import com.example.demo.repository.database.UserDataBaseRepository;

import java.util.Optional;

public class UserService implements AbstractService {
    private UserDataBaseRepository repo;

    public UserService(UserDataBaseRepository repo)  {
        this.repo = repo;
    }

    @Override
    public void addEntity(Entity entity) {
        repo.save((User) entity);
    }

    @Override
    public void removeEntity(Object o) {
        repo.delete((Long) o);
    }

    @Override
    public Iterable getEntities() {
        return repo.findAll();
    }
    public Optional<Entity> searchByName(String name,String password) {
        for(Entity entity : repo.findAll()){
            //entity=(Utilizator) entity;
            if(((User) entity).getFirstName().equals(name) && ((User) entity).getLastName().equals(password)){
                return Optional.of(entity);
            }

        }
        return Optional.empty();
    }
    public Optional<Entity> searchByID(Long id) {
        for(Entity entity : repo.findAll()){
            //entity=(Utilizator) entity;
            if(((User) entity).getId()==id){
                return Optional.of(entity);
            }

        }
        return Optional.empty();
    }
    @Override
    public String toString() {
        String stringOfUsernames = "1";
        for(Object u:getEntities()){
            stringOfUsernames.concat(u.toString());
            stringOfUsernames.concat(" ");
        }
        return stringOfUsernames;
    }

    public Long searchGetIdByName(String name) {
        for(Entity entity : repo.findAll()){
            //entity=(Utilizator) entity;
            if(((User) entity).getFirstName().equals(name)){
                return ((User) entity).getId();
            }

        }
        return 0L;
    }
}
