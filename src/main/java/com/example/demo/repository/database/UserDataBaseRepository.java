package com.example.demo.repository.database;


import com.example.demo.domain.User;
import com.example.demo.repository.Repository;
import com.example.demo.validators.Validator;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDataBaseRepository implements Repository<Long, User> {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;

    public UserDataBaseRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;

    }
    @Override
    public Optional<User> findOne(Long id) {
        // Ensure the id is not null before proceeding
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        User user = null;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {

            statement.setLong(1, id); // Set the ID parameter
            ResultSet resultSet = statement.executeQuery();

            // Check if the result set has at least one record
            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                // Create a new Utilizator object
                user = new User(firstName, lastName);
                user.setId(id); // Set the ID for the utilizator
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(user);
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("username");
                String lastName = resultSet.getString("password");

                User user = new User(firstName, lastName);
                user.setId(id);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Optional<User> save(User entity) {
        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username,password) VALUES (?, ?)");
        ) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return null;
        else
            return Optional.of(entity);
    }

    @Override
    public Optional<User> delete(Long id) {
        Optional<User> userToDelete = findOne(id);
        if (userToDelete.isEmpty()) {

            return Optional.empty();
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                return userToDelete; // Return the deleted user
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> update(User entity) {
        validator.validate(entity); // Ensure the entity is valid before updating

        // Check if the user exists in the database
        Optional<User> userToUpdate = findOne(entity.getId());
        if (userToUpdate.isEmpty()) {
            return Optional.of(entity); // If user doesn't exist, return the entity as failure response
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET first_name = ?, last_name = ? WHERE id = ?")) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setLong(3, entity.getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                return Optional.empty(); // Update successful, return empty
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(entity); // Update failed
    }

}
