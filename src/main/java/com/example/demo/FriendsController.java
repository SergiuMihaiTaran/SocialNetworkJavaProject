package com.example.demo;

import com.example.demo.Utils.Pageable;
import com.example.demo.domain.FriendRequest;
import com.example.demo.domain.FriendShip;
import com.example.demo.service.UserService;
import com.example.demo.service.FriendRequestService;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import com.example.demo.service.FriendshipService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FriendsController implements Observer {
    private UserService usersService;
    private FriendshipService serviceFriends;
    private FriendRequestService serviceFriendRequests;
    private LocalDateTime lastLoginTime;
    private Long id;
    private int currentPageNumber=0;
    private int numberPerPage=2;
    @FXML
    private Button prevPageButton;
    @FXML
    private Button nextPageButton;
    @FXML
    private TextField searchField;

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @FXML
    private ListView<String> friendListView;
    @FXML
    private VBox friendListContainer;  // The container holding the friend list
    @FXML
    private Button toggleButton;            // The button to toggle the friend list visibility

    private boolean isFriendListVisible = false;
    public void closeProgram() {
        // Retrieve the current stage from the button's scene
        Stage stage = (Stage) friendListView.getScene().getWindow();
        stage.close();
        serviceFriends.removeObserver(this);
    }
    public void setUsersService(UserService service){
        this.usersService=service;
    }
    public void setService(FriendshipService service) {
        this.serviceFriends = service;

        loadFriendList();
        serviceFriends.addObserver(this);
    }

    public void setId(Long id) {
        this.id = id;

    }
    @FXML
    private void initialize() {
        // Attach a listener for double-clicks on the friend list
        friendListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Check for double-click
                String selectedFriend = friendListView.getSelectionModel().getSelectedItem();
                String[] selectedFriendStrip=selectedFriend.split(" ");
                Long friendID=usersService.searchGetIdByName(selectedFriendStrip[1].trim());
                System.out.println(selectedFriendStrip[1]);
                if (friendID != 0) {
                    openChatWindow(friendID);
                }
            }
        });

    }

    private void openChatWindow(Long friendID) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("message-view.fxml"));
            Parent messageRoot = fxmlLoader.load();

            // Get the controller and initialize it with friend data
            MessageController messageController = fxmlLoader.getController();
            messageController.initializeChat(friendID, id,usersService);

            // Create and show the chat window
            Stage chatStage = new Stage();
            chatStage.setScene(new Scene(messageRoot, 400, 300));
            chatStage.initStyle(StageStyle.TRANSPARENT);// Adjust width and height as needed
            chatStage.initModality(Modality.WINDOW_MODAL); // Optional: Make it modal
            chatStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to open chat window " + e.getMessage());
        }
    }
    // Toggle the visibility of the friend list with sliding effect
    @FXML
    public void toggleFriendList() {
        double width = friendListContainer.getWidth();
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), friendListContainer);
        if (isFriendListVisible) {
            // Slide out (to the right)
            transition.setToX(width);
        } else {
            // Slide in (from the right)
            transition.setToX(0);
        }
        transition.play();

        // Toggle visibility state
        transition.setOnFinished(event -> {
            isFriendListVisible = !isFriendListVisible;
            friendListContainer.setVisible(isFriendListVisible);  // Toggle the actual visibility after the animation
            toggleButton.setText(isFriendListVisible ? "Hide Friends" : "Show Friends");
        });
    }

    // Load and display the friend list
    private void loadFriendList() {
        friendListView.getItems().clear();

        ArrayList<FriendShip> friendhips= (ArrayList<FriendShip>) serviceFriends.findAllOnPage(new Pageable(numberPerPage,currentPageNumber+currentPageNumber),id).getElementsOnPage();
        for(FriendShip p:friendhips)
                friendListView.getItems().add(usersService.searchByID(p.getId2()).get()+"   "+p.getDate().getDayOfMonth()+"/"+p.getDate().getMonth()+"/"+p.getDate().getYear()+" "+p.getDate().getHour()+":"+p.getDate().getMinute());// Assume this returns a list of friend usernames
        prevPageButton.setDisable(currentPageNumber < 1);
        friendhips= (ArrayList<FriendShip>) serviceFriends.findAllOnPage(new Pageable(numberPerPage,currentPageNumber+numberPerPage+1),id).getElementsOnPage();
        nextPageButton.setDisable(friendhips.isEmpty());
    }

    @FXML
    protected void onAddFriendClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("add-friend-view.fxml"));
        Parent addFriendsRoot = fxmlLoader.load();


        // Get the controller and set the service
        AddFriendController addFriendsController = fxmlLoader.getController();
        addFriendsController.setUsersService(usersService,serviceFriends,serviceFriendRequests,id); // Make sure this is set before displaying
        //System.out.println(usersService.getEntities());
        // Create a new Stage (window) for the friends scene
        Stage addFriendsStage = new Stage();

        // Set the scene on the new stage
        addFriendsStage.setScene(new Scene(addFriendsRoot, 400, 300));  // Set appropriate width and height
        addFriendsStage.initModality(Modality.WINDOW_MODAL);  // Optional: Make it modal if you want it to be a focused window
        //scene.setFill(Color.TRANSPARENT);
        addFriendsStage.initStyle(StageStyle.TRANSPARENT);
        // Show the new friends window
        addFriendsStage.show();
    }

    private void showAlert(String message) {
        // Show alert in case of errors or empty fields
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onRemoveFriendClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("remove-friend-view.fxml"));
        Parent removeFriendsRoot = fxmlLoader.load();


        // Get the controller and set the service
        RemoveFriendController removeFriendsController = fxmlLoader.getController();
        removeFriendsController.setUsersService(usersService,serviceFriends,id); // Make sure this is set before displaying
        //System.out.println(usersService.getEntities());
        // Create a new Stage (window) for the friends scene
        Stage removeFriendStage = new Stage();

        removeFriendStage.initStyle(StageStyle.TRANSPARENT);
        // Set the scene on the new stage
        removeFriendStage.setScene(new Scene(removeFriendsRoot, 400, 300));  // Set appropriate width and height
        removeFriendStage.initModality(Modality.WINDOW_MODAL);  // Optional: Make it modal if you want it to be a focused window

        // Show the new friends window
        removeFriendStage.show();
    }

    public void onFriendRequests(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("friend-request-view.fxml"));
        Parent FRRoot = fxmlLoader.load();


        // Get the controller and set the service
        FriendRequestController friendRequestController = fxmlLoader.getController();
        friendRequestController.setServices(serviceFriendRequests,serviceFriends,usersService,id); // Make sure this is set before displaying
        //System.out.println(usersService.getEntities());
        // Create a new Stage (window) for the friends scene
        Stage FRStage = new Stage();

        FRStage.initStyle(StageStyle.TRANSPARENT);
        // Set the scene on the new stage
        FRStage.setScene(new Scene(FRRoot, 400, 300));  // Set appropriate width and height
        FRStage.initModality(Modality.WINDOW_MODAL);  // Optional: Make it modal if you want it to be a focused window

        // Show the new friends window
        FRStage.show();
    }

    public void setFriendReqService(FriendRequestService serviceFriendReq) {
        this.serviceFriendRequests = serviceFriendReq;
        int counter=0;
        for(FriendRequest fr:serviceFriendRequests.getFriendRequestsById(id)){
            if(fr.getDate().isAfter(lastLoginTime)){
                counter++;
            }
        }
        if(counter==0) {
            showAlert("You have no new friend requests");
        }
        else{
            showAlert("You have "+counter+" new friend requests");
        }

    }

    @Override
    public void update() {
        loadFriendList();
    }

    public void onSendMessageClick(ActionEvent actionEvent) throws IOException {
        return;
    }
    public void onPrevPage(ActionEvent actionEvent) throws IOException {
        if(currentPageNumber>0) {
            currentPageNumber--;
            update();
        }
    }
    public void onNextPage(ActionEvent actionEvent) throws IOException {
        ArrayList<FriendShip> friendhips= (ArrayList<FriendShip>) serviceFriends.findAllOnPage(new Pageable(numberPerPage,currentPageNumber+1),id).getElementsOnPage();
        if(!friendhips.isEmpty()){
            currentPageNumber++;
            update();
    }}
}
