package com.example.demo.service;

import com.example.demo.domain.Entity;
import com.example.demo.domain.FriendRequest;
import com.example.demo.domain.Tuple;
import com.example.demo.repository.database.FriendRequestDataBaseRepository;
import com.example.demo.Observer;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestService implements AbstractService {
    private FriendRequestDataBaseRepository repo;
    private final List<Observer> observers=new ArrayList<>();
    public FriendRequestService(FriendRequestDataBaseRepository repo)  {
        this.repo = repo;
    }
    // Observer-related methods
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
    @Override
    public void addEntity(Entity entity) {

        repo.save((FriendRequest) entity);
        notifyObservers();
    }

    @Override
    public void removeEntity(Object o) {
        repo.delete((Tuple<Long,Long>) o);
        notifyObservers();
    }

    @Override
    public Iterable getEntities() {
        return repo.findAll();
    }
    public ArrayList<FriendRequest> getFriendRequestsById(Long id) {
        ArrayList<FriendRequest> friendships = new ArrayList<>();
        for (FriendRequest p : repo.findAll()) {
            if (p.getIdUserTo()==id) {
                friendships.add(p);
            }
        }
        return friendships;
    }
}
