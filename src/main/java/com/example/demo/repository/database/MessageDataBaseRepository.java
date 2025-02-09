package com.example.demo.repository.database;

import com.example.demo.domain.Message;
import com.example.demo.domain.User;
import com.example.demo.repository.Repository;
import com.example.demo.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessageDataBaseRepository implements Repository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> validator;

    public MessageDataBaseRepository(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Message> findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        Message message = null;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM message WHERE id = ?")) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                message = extractMessageFromResultSet(resultSet);
                message.setTo(findRecipientsForMessage(id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(message);
    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();

        String query = """
        SELECT m.id, m.from_user_id, m.message, m.date, m.reply_to_message_id, u.username AS from_username,
               COALESCE(r.recipient_id, 0) AS recipient_id, ru.username AS recipient_username
        FROM message m
        LEFT JOIN message_recipients r ON m.id = r.message_id
        LEFT JOIN users u ON m.from_user_id = u.id
        LEFT JOIN users ru ON r.recipient_id = ru.id
        ORDER BY m.id
    """;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            Map<Long, Message> messageMap = new HashMap<>();

            while (resultSet.next()) {
                Long messageId = resultSet.getLong("id");

                // Check if the message is already constructed
                Message message = messageMap.get(messageId);
                if (message == null) {
                    message = extractMessageFromResultSet(resultSet);

                    // Set the reply message if available
                    Long replyToId = resultSet.getObject("reply_to_message_id", Long.class);
                    if (replyToId != null) {

                        message.setReply(findOne(replyToId).orElse(null));
                    }

                    messageMap.put(messageId, message);
                    messages.add(message);
                }

                // Add recipient to the message
                Long recipientId = resultSet.getLong("recipient_id");
                if (recipientId != 0) {
                    User recipient = new User(resultSet.getString("recipient_username"), "");
                    recipient.setId(recipientId);
                    message.getTo().add(recipient);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }


    @Override
    public Optional<Message> save(Message entity) {
        validator.validate(entity);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO message (from_user_id, message, date, reply_to_message_id) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, entity.getFrom().getId());
            statement.setString(2, entity.getMessage());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            if (entity.getReply() != null) {
                statement.setLong(4, entity.getReply().getId());
            } else {
                statement.setNull(4, Types.BIGINT);
            }

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet keys = statement.getGeneratedKeys();
                if (keys.next()) {
                    entity.setId(keys.getLong(1));
                }
                saveRecipients(entity.getId(), entity.getTo());
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.of(entity);
    }

    @Override
    public Optional<Message> delete(Long id) {
        Optional<Message> message = findOne(id);
        if (message.isEmpty()) return Optional.empty();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement deleteRecipients = connection.prepareStatement("DELETE FROM message_recipients WHERE message_id = ?");
             PreparedStatement deleteMessage = connection.prepareStatement("DELETE FROM message WHERE id = ?")) {

            deleteRecipients.setLong(1, id);
            deleteRecipients.executeUpdate();

            deleteMessage.setLong(1, id);
            int rowsDeleted = deleteMessage.executeUpdate();

            if (rowsDeleted > 0) return message;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        validator.validate(entity);

        Optional<Message> existing = findOne(entity.getId());
        if (existing.isEmpty()) return Optional.of(entity);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement updateStatement = connection.prepareStatement(
                     "UPDATE message SET message = ?, date = ?, reply_to_message_id = ? WHERE id = ?")) {

            updateStatement.setString(1, entity.getMessage());
            updateStatement.setTimestamp(2, Timestamp.valueOf(entity.getDate()));
            if (entity.getReply() != null) {
                updateStatement.setLong(3, entity.getReply().getId());
            } else {
                updateStatement.setNull(3, Types.BIGINT);
            }
            updateStatement.setLong(4, entity.getId());

            int rowsUpdated = updateStatement.executeUpdate();
            if (rowsUpdated > 0) {
                deleteRecipients(entity.getId());
                saveRecipients(entity.getId(), entity.getTo());
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.of(entity);
    }

    private Message extractMessageFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Long fromUserId = resultSet.getLong("from_user_id");
        String text = resultSet.getString("message");
        LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
        Long replyToId = resultSet.getObject("reply_to_message_id", Long.class);

        User fromUser = new User(resultSet. getString("from_username"), "");
        System.out.println("From username: " + resultSet.getString("from_username"));
        fromUser.setId(fromUserId);

        Message replyTo = replyToId != null ? findOne(replyToId).orElse(null) : null;

        Message message = new Message(fromUser, new ArrayList<>(), text, date);
        message.setId(id);
        message.setReply(replyTo);
        return message;
    }

    private List<User> findRecipientsForMessage(Long messageId) throws SQLException {
        List<User> recipients = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT u.id, u.username FROM message_recipients mr JOIN users u ON mr.recipient_id = u.id WHERE mr.message_id = ?")) {

            statement.setLong(1, messageId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String username = resultSet.getString("username");

                User recipient = new User(username, "");
                recipient.setId(id);
                recipients.add(recipient);
            }
        }
        return recipients;
    }

    private void saveRecipients(Long messageId, List<User> recipients) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO message_recipients (message_id, recipient_id) VALUES (?, ?)")) {

            for (User recipient : recipients) {
                statement.setLong(1, messageId);
                statement.setLong(2, recipient.getId());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void deleteRecipients(Long messageId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM message_recipients WHERE message_id = ?")) {

            statement.setLong(1, messageId);
            statement.executeUpdate();
        }
    }
}
