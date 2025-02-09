package com.example.demo.service;

import com.example.demo.Observer;
import com.example.demo.domain.Entity;
import com.example.demo.domain.Message;
import com.example.demo.domain.User;
import com.example.demo.repository.database.MessageDataBaseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceMessage implements AbstractService{
    private MessageDataBaseRepository repo;
    private final List<Observer> observers=new ArrayList<>();
    public ServiceMessage(MessageDataBaseRepository repo) {
        this.repo = repo;
    }
    @Override
    public void addEntity(Entity entity) {
        repo.save((Message) entity);
    }
    @Override
    public void removeEntity(Object o) {
        repo.delete((Long) o);
    }

    @Override
    public Iterable getEntities() {
        return repo.findAll();
    }
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
    public Optional<Message> findOne(Long id){
        return repo.findOne(id);
    }
    public List<Message> getMessagesBetweenUsers(Long userID, Long friendID) {
        List<Message> messages = new ArrayList<>();
        for(Message m : repo.findAll()) {
            List<User> messageUsers=m.getTo();
            List<Long> messageUsersID = new ArrayList<>();
            for(User u : messageUsers) {
                messageUsersID.add(u.getId());
            }
            if(m.getFrom().getId().equals(userID) && messageUsersID.contains(friendID)) {
                messages.add(m);
            }
            if(m.getFrom().getId().equals(friendID) && messageUsersID.contains(userID)) {
                messages.add(m);
            }

        }
        return messages;
    }
}
