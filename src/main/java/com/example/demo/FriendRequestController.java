package com.example.demo;

import com.example.demo.domain.FriendRequest;
import com.example.demo.domain.FriendShip;
import com.example.demo.domain.Tuple;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javafx.stage.Stage;

public class FriendRequestController implements Observer {
    private FriendRequestService friendReqService;
    private FriendshipService friendsService;
    private UserService userService;
    private Long id;
    private final ObservableList<FriendRequest> allRequests = FXCollections.observableArrayList();

    @Override
    public void update() {
        initializeList();
    }

    public void closeProgram() {
        // Retrieve the current stage from the button's scene
        friendReqService.removeObserver(this);
        Stage stage = (Stage) searchResultsListView.getScene().getWindow();
        stage.close();
    }


    private class UserCellFR extends ListCell<FriendRequest> {
        @Override
        protected void updateItem(FriendRequest request, boolean empty) {
            super.updateItem(request, empty);

            if (empty || request == null) {
                setGraphic(null);
                setText(null);
            } else {
               User user= (User) userService.searchByID(request.getIdUserFrom()).get();
                Text usernameText = new Text(user.getFirstName()+"     "+request.getDate().getDayOfMonth()+"/"+request.getDate().getMonth()+"/"+request.getDate().getYear()+" "+request.getDate().getHour()+":"+request.getDate().getMinute() );
                Button addButton = new Button("Accept");

                addButton.setOnAction(event -> AcceptFriendRequest(request));

                // Create an HBox with username text and add button
                HBox hbox = new HBox(usernameText, new Region(), addButton);

                // Set the Region to grow and push the button to the far right
                HBox.setHgrow(hbox.getChildren().get(1), Priority.ALWAYS);

                setGraphic(hbox);
            }
        }

        private void AcceptFriendRequest(FriendRequest friendRequest) {
            friendReqService.removeEntity(new Tuple<Long,Long>(friendRequest.getIdUserFrom(),friendRequest.getIdUserTo()));
            friendsService.addEntity(new FriendShip(friendRequest.getIdUserFrom(),friendRequest.getIdUserTo(),LocalDateTime.now()));
            friendsService.addEntity(new FriendShip(friendRequest.getIdUserTo(),friendRequest.getIdUserFrom(),LocalDateTime.now()));
        }
        }

        @FXML
    public void initialize() {
        searchResultsListView.setCellFactory(listView -> new UserCellFR());
    }
    @FXML
    private ListView<FriendRequest> searchResultsListView;
    public void setServices(FriendRequestService service, FriendshipService friendsService, UserService userService, Long id) {
        this.friendReqService = service;
        this.userService=userService;
        this.friendsService=friendsService;
        this.id=id;
        // Populate allUsers with data from the service
        friendReqService.addObserver(this);
        initializeList();
    }
    private void initializeList(){
        allRequests.clear();
        List<FriendRequest> requests = new ArrayList<>();
        for(FriendRequest u:friendReqService.getFriendRequestsById(id))
            requests.add(u);
        System.out.println(requests);
        allRequests.addAll(requests);
        searchResultsListView.setItems(allRequests);
    }
}