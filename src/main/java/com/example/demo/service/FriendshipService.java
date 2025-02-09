package com.example.demo.service;

import com.example.demo.Utils.Page;
import com.example.demo.Utils.Pageable;
import com.example.demo.domain.Entity;
import com.example.demo.domain.FriendShip;
import com.example.demo.domain.Tuple;
import com.example.demo.repository.database.FriendShipDataBaseRepository;
import com.example.demo.Observer;
import java.util.ArrayList;
import java.util.List;

public class FriendshipService implements AbstractService {
    private FriendShipDataBaseRepository repo;
    private final List<Observer> observers = new ArrayList<>();
    public FriendshipService(FriendShipDataBaseRepository repo)  {
        this.repo = repo;
    }
    // Observer-related methods
    public void addObserver(Observer observer) {
        observers.add(observer);
        System.out.println("Observer added: " + observer);
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
        System.out.println("1");
        repo.save((FriendShip) entity);
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
    public ArrayList<FriendShip> getFriendshipsById(Long id) {
        ArrayList<FriendShip> friendships = new ArrayList<>();
        for (FriendShip p : repo.findAll()) {
            if (p.getId1()==id) {
                friendships.add(p);
            }
        }
        return friendships;
    }
    public Page<FriendShip> findAllOnPage(Pageable pageable, Long id) {
        return repo.findAllOnPage(pageable,id);
    }
}
