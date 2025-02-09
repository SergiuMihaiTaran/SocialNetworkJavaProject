package com.example.demo;

import com.example.demo.domain.Message;
import com.example.demo.domain.User;
import com.example.demo.repository.database.MessageDataBaseRepository;
import com.example.demo.service.UserService;
import com.example.demo.service.ServiceMessage;
import com.example.demo.validators.MessageValidator;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageController implements Observer{
    public Label friendNameLabel;
    public HBox replyPreview;
    public Label replyToLabel;
    public ListView userListView;
    private Long friendID;
    private Long userID;
    private ServiceMessage serviceMessage;
    private UserService serviceUsers;
    private String messageToReplyTo;
    private int numberOfMessage;
    @FXML
    private VBox messagesContainer;

    @FXML
    private TextField messageInput;

    @FXML
    private ScrollPane messageScrollPane;
    public void closeProgram() {
        // Retrieve the current stage from the button's scene
        serviceMessage.removeObserver(this);
        Stage stage = (Stage) messagesContainer.getScene().getWindow();
        stage.close();
    }
    /**
     * Initializes the chat between the current user and a friend.
     *
     * @param idFriend the ID of the friend.
     * @param idUser   the ID of the current user.
     * @param service  the service to handle messages.
     */

    public void initializeChat(Long idFriend, Long idUser, UserService service) {
        this.friendID = idFriend;
        this.userID = idUser;
        this.serviceUsers = service;
        this.numberOfMessage=-1;
        if(serviceUsers.searchByID(userID).isEmpty() || serviceUsers.searchByID(friendID).isEmpty()) {
            return;
        }
        friendNameLabel.setText(((User)serviceUsers.searchByID(friendID).get()).getFirstName());
        MessageDataBaseRepository repository=new MessageDataBaseRepository("jdbc:postgresql://localhost:5432/socialnetwork",
                "postgres", // PostgreSQL username
                "postgres", // leave password empty
                new MessageValidator());
        this.serviceMessage = new ServiceMessage(repository);
        // Load existing messages between the two users
        serviceMessage.addObserver(this);
        loadMessages();
        messageInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onSendButtonClicked();
            }
        });
    }

    /**
     * Handles sending a message.
     */
    @FXML
    private void onSendButtonClicked() {
        String messageText = messageInput.getText().trim();
        if (messageText.isEmpty()) {
            return; // Ignore empty messages
        }


        List<User> userList=new ArrayList<>();
        userList.add((User) serviceUsers.searchByID(friendID).get());
        User currentUser = (User) serviceUsers.searchByID(userID).get();

        // Create and save the message using the service
        Message message=new Message(currentUser,userList,messageText,LocalDateTime.now());
        if(!serviceMessage.getMessagesBetweenUsers(userID, friendID).isEmpty() && numberOfMessage!=-1)
            message.setReply((serviceMessage.getMessagesBetweenUsers(userID,friendID).get(numberOfMessage)));
        numberOfMessage=-1;
        serviceMessage.addEntity(message);
        // Add the message to the UI
        addMessageToUI("You", message,numberOfMessage);
        update();
        clearReply();
        messageInput.clear();
    }

    /**
     * Loads the conversation between the current user and their friend.
     */
    private void loadMessages() {
        messagesContainer.getChildren().clear();

        // Fetch messages between the two users from the service
        List<Message> messages = serviceMessage.getMessagesBetweenUsers(userID, friendID);
        int i=0;
        for (Message message : messages) {
            //System.out.println(message.getReply()==null);
            String senderName = message.getFrom().getId().equals(userID) ? "You" : ((User)serviceUsers.searchByID(friendID).get()).getFirstName();
            //System.out.println(message.getMessage());

            addMessageToUI(senderName, message,i);
            i++;
        }
        //System.out.println(messagesContainer.getChildren().size());
    }

    /**
     * Adds a message to the UI.
     *
     * @param sender  the sender of the message.
     * @param message the message text.
     */
    private void addMessageToUI(String sender, Message message,int index) {
        HBox messageBox = new HBox(10);
        Label senderLabel = new Label(sender + ":");
        senderLabel.setStyle("-fx-font-weight: bold;");
        String fullMessage=message.getMessage();
        //System.out.println(message.getReply()==null);
        if(message.getReply()!=null) {
            Message reply = message.getReply();
            fullMessage=fullMessage.concat("Replying to:"+reply.getMessage());
        }

        Label messageLabel = new Label(fullMessage);
        messageLabel.setWrapText(true);
        messageBox.getChildren().addAll(senderLabel, messageLabel);
        messageBox.setPadding(new Insets(5));
        messageBox.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 10;");
        String finalMessage = fullMessage;
        messageBox.setOnMouseClicked(event -> enableReply(finalMessage,index));
        messagesContainer.getChildren().add(messageBox);

        // Scroll to the bottom
        messageScrollPane.setVvalue(1.0);
        scrollToBottom();
    }
    private void scrollToBottom() {
        // Use Platform.runLater to ensure the scroll operation is executed after layout updates
        javafx.application.Platform.runLater(() -> {
            messageScrollPane.layout();
            messageScrollPane.setVvalue(1.0); // Set vertical value to 1.0 to scroll to the bottom
        });
    }
    /**
     * Shows an alert with the given title and message.
     *
     * @param title   the title of the alert.
     * @param message the message of the alert.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void update() {
        loadMessages();
    }
    // Handle message selection for reply
    private void enableReply(String messageContent,int numberOfMessage) {
        messageToReplyTo = messageContent;
        this.numberOfMessage=numberOfMessage;
        System.out.println(numberOfMessage);
        replyToLabel.setText("Replying to: " + messageContent);
        replyPreview.setVisible(true);
    }
    @FXML
    private void clearReply() {
        messageToReplyTo = null;
        replyPreview.setVisible(false);
    }
    @FXML
    private void handleEnterKey(javafx.scene.input.KeyEvent event) {
        if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
            onSendButtonClicked();
        }
    }
}