package com.example.demo;

import com.example.demo.domain.Entity;
import com.example.demo.domain.LastLogin;
import com.example.demo.domain.User;
import com.example.demo.service.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public Label errorLabel;
    private UserService serviceSignUp;
    private FriendshipService mainService;
    private FriendRequestService serviceFriendReq;
    private ServiceLastLogin serviceLastLogin;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Label welcomeText;
    private Stage stage;
    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    @FXML
    void closeProgram(ActionEvent event) {
        stage.close();
    }
    public void setService(UserService service) {
        this.serviceSignUp = service;
    }
    public void setMainService(FriendshipService mainService){this.mainService=mainService; }
    public void setLoginService(ServiceLastLogin loginService){this.serviceLastLogin=loginService; }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void setStage(Stage stage) {
        this.stage=stage;
    }

    public void closeProgram(javafx.event.ActionEvent actionEvent) {
        stage.close();
    }
    @FXML
    void onSignUpClick() {
        try {
            // Load the sign-up FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signup-view.fxml"));
            Parent signUpRoot = loader.load();

            // Create a new scene with the sign-up layout
            Scene signUpScene = new Scene(signUpRoot,450,270);

            // Set the new scene on the stage
            stage.setScene(signUpScene);
            stage.setTitle("Sign Up");
            SignUpController controller = loader.getController();
            controller.setStage(stage);
            controller.setServices(serviceSignUp,mainService,serviceFriendReq,serviceLastLogin);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void LoginPressed() throws IOException {
        // Load the FXML file for the friends scene

        Optional<Entity> currentLogin=serviceSignUp.searchByName(username.getText(),password.getText());

        if(currentLogin.isPresent()) {
            FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("friends-view.fxml"));
            Parent friendsRoot = fxmlLoader.load();
            FriendsController friendsController = fxmlLoader.getController();

            friendsController.setUsersService(serviceSignUp);
            User user =(User)currentLogin.get();
            friendsController.setId(user.getId());
            friendsController.setLastLoginTime(serviceLastLogin.getLastLogin(user.getId()).get().getDate());
            friendsController.setService(mainService);
            friendsController.setFriendReqService(serviceFriendReq);
            friendsController.setId(user.getId());

            // Inject service to manage friend data
            stage.close();
            // Create a new Stage (window) for the friends scene
            Stage friendsStage = new Stage();

            // Set the scene on the new stage
            friendsStage.setScene(new Scene(friendsRoot, 510, 260));  // Set appropriate width and height
            friendsStage.initModality(Modality.WINDOW_MODAL);  // Optional: Make it modal if you want it to be a focused window
            friendsStage.initStyle(StageStyle.TRANSPARENT);
            // Show the new friends window


            //Maybe needs to be changed in the future
            if(serviceLastLogin.getLastLogin(user.getId()).isEmpty()) {
                serviceLastLogin.addEntity(new LastLogin(user.getId(), LocalDateTime.now()));
            }
            serviceLastLogin.update(serviceLastLogin.getLastLogin(user.getId()).get());
            friendsStage.show();
        }
        else{
            errorLabel.setText("Invalid Username or Password");
            errorLabel.setVisible(true);
        }

    }
    private void showAlert(String message) {
        // Show alert in case of errors or empty fields
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setFReqService(FriendRequestService serviceFriendRequests) {
        this.serviceFriendReq=serviceFriendRequests;
    }
}