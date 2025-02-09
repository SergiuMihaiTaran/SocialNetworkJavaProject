package com.example.demo.repository.database;

import com.example.demo.domain.FriendShip;
import com.example.demo.domain.Tuple;
import com.example.demo.repository.Repository;
import com.example.demo.validators.Validator;
import com.example.demo.Utils.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class FriendShipDataBaseRepository implements Repository<Tuple<Long, Long>, FriendShip> {
    private String url;
    private String username;
    private String password;
    private Validator<FriendShip> validator;

    public FriendShipDataBaseRepository(String url, String username, String password, Validator<FriendShip> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<FriendShip> findOne(Tuple<Long, Long> id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        FriendShip friendShip = null;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships WHERE id1 = ? AND id2 = ?")) {

            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                long id1 = resultSet.getLong("id1");
                long id2 = resultSet.getLong("id2");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                friendShip = new FriendShip(id1, id2,date);
                friendShip.date = date;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(friendShip);
    }

    @Override
    public Iterable<FriendShip> findAll() {
        Set<FriendShip> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                long id1 = resultSet.getLong("id1");
                long id2 = resultSet.getLong("id2");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                FriendShip friendShip = new FriendShip(id1, id2,date);
                friendShip.date = date;
                friendships.add(friendShip);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Optional<FriendShip> save(FriendShip entity) {
        validator.validate(entity);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO friendships (id1, id2, date) VALUES (?, ?, ?)")) {

            statement.setLong(1, entity.getId1());
            statement.setLong(2, entity.getId2());
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                return Optional.empty(); // Successful insertion
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(entity); // Insertion failed
    }

    @Override
    public Optional<FriendShip> delete(Tuple<Long, Long> id) {
        Optional<FriendShip> friendshipToDelete = findOne(id);
        if (friendshipToDelete.isEmpty()) {
            return Optional.empty();
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships WHERE id1 = ? AND id2 = ?")) {

            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                return friendshipToDelete; // Return the deleted friendship
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<FriendShip> update(FriendShip entity) {
        validator.validate(entity);

        Optional<FriendShip> friendshipToUpdate = findOne(entity.getId());
        if (friendshipToUpdate.isEmpty()) {
            return Optional.of(entity); // Friendship does not exist, return the entity as failure response
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE friendships SET date = ? WHERE id1 = ? AND id2 = ?")) {

            statement.setTimestamp(1, Timestamp.valueOf(entity.getDate()));
            statement.setLong(2, entity.getId1());
            statement.setLong(3, entity.getId2());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                return Optional.empty(); // Update successful
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(entity); // Update failed
    }

    private int count() {
        int i=0;
        for(FriendShip friendship : findAll()) {
            i++;
        }
        return i;
    }
    public List<FriendShip> findAllOnPage(Connection connection, Pageable pageable, Long id) throws SQLException {
        List<FriendShip> friendships = new ArrayList<>();
        String query = "SELECT * FROM friendships WHERE id1=?";
        query += " limit ? offset ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(2, pageable.getPageSize());
            statement.setLong(3, pageable.getPageNumber());
            statement.setLong(1, id);

        try(ResultSet resultSet=statement.executeQuery()) {
            while (resultSet.next()) {
                long id1 = resultSet.getLong("id1");
                long id2 = resultSet.getLong("id2");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                FriendShip friendship = new FriendShip(id1, id2,date);
                friendships.add(friendship);
            }
        }
    }
        return friendships;
    }

    public Page<FriendShip> findAllOnPage(Pageable pageable, Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            int totalNumberOfFriendships = count();
            List<FriendShip> friendShipsOnPage;
            if (totalNumberOfFriendships > 0) {
                friendShipsOnPage = findAllOnPage(connection, pageable,id);
            } else {
                friendShipsOnPage = new ArrayList<>();
            }
            return new Page<>(totalNumberOfFriendships,friendShipsOnPage);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

