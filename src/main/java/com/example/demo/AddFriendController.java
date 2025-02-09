package com.example.demo;

import com.example.demo.domain.FriendRequest;
import com.example.demo.domain.FriendShip;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.service.FriendshipService;
import com.example.demo.service.FriendRequestService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.stage.Stage;

public class AddFriendController implements Observer {
    private UserService usersService;
    private FriendRequestService serviceFriendRequests;
    private FriendshipService friendsService;
    private Long id;
    @FXML
    private TextField searchField;
    @FXML
    private Text successMessage;
    @FXML
    private ListView<User> searchResultsListView;

    private final ObservableList<User> allUsers = FXCollections.observableArrayList();
    private final ObservableList<User> filteredUsers = FXCollections.observableArrayList();
    @FXML

    public void closeProgram() {
        // Retrieve the current stage from the button's scene
        serviceFriendRequests.removeObserver(this);
        Stage stage = (Stage) searchResultsListView.getScene().getWindow();
        stage.close();
    }
    public void setUsersService(UserService service, FriendshipService friendsService, FriendRequestService serviceFriendRequests, Long id) {
        this.usersService = service;
        this.friendsService=friendsService;
        this.serviceFriendRequests=serviceFriendRequests;
        this.id=id;
        initializeList();
        serviceFriendRequests.addObserver(this);
    }
    private void initializeList(){
        allUsers.clear();
        // Populate allUsers with data from the service
        List<User> userFriends = new ArrayList<>();
        for(Object u:friendsService.getFriendshipsById(id))
        {
            User user1= (User) usersService.searchByID(((FriendShip)u).getId1()).get();
            User user2= (User) usersService.searchByID(((FriendShip)u).getId2()).get();
            if(!Objects.equals(user1.getId(), id))
                userFriends.add(user1);
            if(!Objects.equals(user2.getId(), id))
                userFriends.add(user2);
        }
        List<Long> idList=new ArrayList<>();
        for(Object e:serviceFriendRequests.getEntities()){
            FriendRequest friendRequest= (FriendRequest) e;
            if(friendRequest.getIdUserFrom()==id)
                idList.add(friendRequest.getIdUserTo());
            if(friendRequest.getIdUserTo()==id)
                idList.add(friendRequest.getIdUserFrom());
        }
        List<User> users = new ArrayList<>();
        for(Object u:usersService.getEntities())
            if(!userFriends.contains((User) u) && !((User) u).getId().equals(id) && !idList.contains(((User) u).getId()))
                users.add((User)u);

        allUsers.setAll(users);
        filteredUsers.setAll(allUsers);
        searchResultsListView.setItems(filteredUsers);
    }
    @FXML
    public void initialize() {
        searchResultsListView.setCellFactory(listView -> new UserCell());

    }

    @FXML
    public void onSearchFieldKeyTyped(KeyEvent event) {
        String filter = searchField.getText().toLowerCase();
        filteredUsers.setAll(allUsers.filtered(user -> user.getFirstName().toLowerCase().contains(filter)));
    }

    @Override
    public void update() {
        initializeList();
    }

    private class UserCell extends ListCell<User> {
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);

            if (empty || user == null) {
                setGraphic(null);
                setText(null);
            } else {
                Text usernameText = new Text(user.getFirstName());
                Button addButton = new Button("Add Friend");

                addButton.setOnAction(event -> addFriend(user));

                // Create an HBox with username text and add button
                HBox hbox = new HBox(usernameText, new Region(), addButton);

                // Set the Region to grow and push the button to the far right
                HBox.setHgrow(hbox.getChildren().get(1), Priority.ALWAYS);

                setGraphic(hbox);
            }
        }

        private void addFriend(User user) {
            // Logic for adding a friend

            serviceFriendRequests.addEntity(new FriendRequest(id, user.getId(), LocalDateTime.now()));

            // Show success message

            successMessage.setText("Friend Request Sent!");
            successMessage.setVisible(true);

            // Hide the message after 3 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(3000); // Wait for 3 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                successMessage.setVisible(false);
            }).start();
        }
    }
}
