package com.example.demo;

import com.example.demo.domain.FriendShip;
import com.example.demo.domain.Tuple;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.service.FriendshipService;
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
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RemoveFriendController implements Observer {
    private FriendshipService friendsService;
    private UserService usersService;
    private Long id;
    @FXML
    private TextField searchField;
    @FXML
    private Text successMessage;
    @FXML
    private ListView<User> searchResultsListView;

    private final ObservableList<User> allUsers = FXCollections.observableArrayList();
    private final ObservableList<User> filteredUsers = FXCollections.observableArrayList();
    public void closeProgram() {
        // Retrieve the current stage from the button's scene
        Stage stage = (Stage) searchResultsListView.getScene().getWindow();
        stage.close();
        friendsService.removeObserver(this);
    }
    public void setUsersService(UserService usersService, FriendshipService friendsService, Long id) {
        this.friendsService=friendsService;
        this.id=id;
        this.usersService=usersService;
        // Populate allUsers with data from the service
        friendsService.addObserver(this);
        initializeList();
    }
    private void initializeList(){
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

        allUsers.setAll(userFriends);
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
                Button removeButton = new Button("Remove Friend");

                removeButton.setOnAction(event -> removeFriend(user));

                // Create an HBox with username text and add button
                HBox hbox = new HBox(usernameText, new Region(), removeButton);

                // Set the Region to grow and push the button to the far right
                HBox.setHgrow(hbox.getChildren().get(1), Priority.ALWAYS);

                setGraphic(hbox);
            }
        }

        private void removeFriend(User user) {
            friendsService.removeEntity(new Tuple<Long,Long>(user.getId(), id));
            friendsService.removeEntity(new Tuple<Long,Long>(id, user.getId()));

            successMessage.setText("Friend Removed!");
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
            initializeList();
        }
    }
}
