package com.example.demo.repository.database;

import com.example.demo.domain.LastLogin;
import com.example.demo.repository.Repository;
import com.example.demo.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class LastLoginDataBaseRepository implements Repository<Long, LastLogin> {
    private String url;
    private String username;
    private String password;
    private Validator<LastLogin> validator;

    public LastLoginDataBaseRepository(String url, String username, String password, Validator<LastLogin> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<LastLogin> findOne(Long aLong) {
        // Ensure the id is not null before proceeding
        if (aLong == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        LastLogin lastLogin = null;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM last_logins WHERE id = ?")) {

            statement.setLong(1, aLong); // Set the ID parameter
            ResultSet resultSet = statement.executeQuery();

            // Check if the result set has at least one record
            if (resultSet.next()) {
                Long userId = resultSet.getLong("user_id");
                LocalDateTime date = resultSet.getTimestamp("login_time").toLocalDateTime();


                // Create a new Utilizator object
                lastLogin = new LastLogin(userId,date);
                lastLogin.setId(aLong); // Set the ID for the utilizator
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(lastLogin);
    }

    @Override
    public Iterable<LastLogin> findAll() {
        Set<LastLogin> logins = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from last_logins");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long userId = resultSet.getLong("user_id");
                LocalDateTime date = resultSet.getTimestamp("login_time").toLocalDateTime();


                LastLogin lastLogin = new LastLogin(userId, date);

                lastLogin.setId(id);

                logins.add(lastLogin);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logins;

    }

    @Override
    public Optional<LastLogin> save(LastLogin entity) {
        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO last_logins ( user_id, login_time) VALUES (?, ?)")
        ) {
            statement.setLong(1, entity.getId_user());
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return Optional.empty();
        else
            return Optional.of(entity);
    }

    @Override
    public Optional<LastLogin> delete(Long aLong) {
        Optional<LastLogin> loginToDelete = findOne(aLong);
        if (loginToDelete.isEmpty()) {
            return Optional.empty();
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM last_logins WHERE id=?")) {

            statement.setLong(1, aLong);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                return loginToDelete; // Return the deleted friendship
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<LastLogin> update(LastLogin entity) {
        validator.validate(entity);

        Optional<LastLogin> loginToUpdate = findOne(entity.getId());
        if (loginToUpdate.isEmpty()) {
            return Optional.of(entity); // Friendship does not exist, return the entity as failure response
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE last_logins SET login_time = ? WHERE id=?")) {

            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.setLong(2, entity.getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                return Optional.empty(); // Update successful
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(entity); // Update failed
    }

}