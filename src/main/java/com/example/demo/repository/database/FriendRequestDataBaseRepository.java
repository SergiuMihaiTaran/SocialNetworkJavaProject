package com.example.demo.repository.database;

import com.example.demo.domain.FriendRequest;
import com.example.demo.repository.Repository;
import com.example.demo.validators.Validator;
import com.example.demo.domain.Tuple;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendRequestDataBaseRepository implements Repository<Tuple<Long, Long>, FriendRequest> {
    private String url;
    private String username;
    private String password;
    private Validator<FriendRequest> validator;

    public FriendRequestDataBaseRepository(String url, String username, String password, Validator<FriendRequest> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<FriendRequest> findOne(Tuple<Long, Long> id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        FriendRequest friendRequest = null;

        String query = "SELECT * FROM friend_requests WHERE user_from = ? AND user_to = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                long idUserFrom = resultSet.getLong("user_from");
                long idUserTo = resultSet.getLong("user_to");

                friendRequest = new FriendRequest(idUserFrom, idUserTo, date);
                friendRequest.setId(id); // Set the tuple ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(friendRequest);
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        Set<FriendRequest> friendRequests = new HashSet<>();

        String query = "SELECT * FROM friend_requests";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                long userFrom = resultSet.getLong("user_from");
                long userTo = resultSet.getLong("user_to");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                FriendRequest friendRequest = new FriendRequest(userFrom, userTo, date);
                friendRequest.setId(new Tuple<>(userFrom, userTo));
                friendRequests.add(friendRequest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friendRequests;
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {
        String query = "INSERT INTO friend_requests (user_from, user_to, date) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, entity.getIdUserFrom());
            statement.setLong(2, entity.getIdUserTo());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return Optional.empty(); // Save successful
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(entity); // Save failed
    }

    @Override
    public Optional<FriendRequest> delete(Tuple<Long, Long> id) {
        Optional<FriendRequest> friendRequestToDelete = findOne(id);
        if (friendRequestToDelete.isEmpty()) {
            return Optional.empty();
        }

        String query = "DELETE FROM friend_requests WHERE user_from = ? AND user_to = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return friendRequestToDelete; // Return the deleted friend request
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        validator.validate(entity); // Ensure the entity is valid before updating

        // In the case of friend requests, updates might not be needed, but you can extend the logic as per your requirements.
        return Optional.of(entity); // If update not needed, simply return the entity
    }

    // Helper method to check if a friend request already exists
    public boolean isFriendRequestExist(long userFrom, long userTo) {
        String query = "SELECT COUNT(*) FROM friend_requests WHERE user_from = ? AND user_to = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userFrom);
            statement.setLong(2, userTo);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
