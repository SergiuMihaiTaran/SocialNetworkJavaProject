package com.example.demo;

import com.example.demo.domain.User;
import com.example.demo.service.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {
    public Label errorLabel;
    private UserService serviceSignUp;
    private FriendshipService serviceFriendship;
    private FriendRequestService serviceFriendRequest;
    private ServiceLastLogin serviceLastLogin;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField confirmPasswordField;
    private Stage stage;
    public void setStage(Stage stage) {
        this.stage=stage;
    }
    public void setServices(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService, ServiceLastLogin serviceLastLogin) {
        this.serviceSignUp = userService;
        this.serviceFriendship = friendshipService;
        this.serviceFriendRequest = friendRequestService;
        this.serviceLastLogin = serviceLastLogin;
    }
    // You can add methods here to handle registration logic
    @FXML
    protected void onSignUpClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        try{
            
            if(username.length()<3 || password.length()<3 || confirmPassword.length()<3) {
                errorLabel.setText("Username/Password must be at least 3 characters");
                errorLabel.setVisible(true);
                return;
            }

            if(!password.equals(confirmPassword)) {
                errorLabel.setText("Passwords do not match");
                errorLabel.setVisible(true);
                return;
                }
            serviceSignUp.addEntity(new User(username,password));
            errorLabel.setText("Registration successful");
            errorLabel.setTextFill(Color.BLUE);
            errorLabel.setVisible(true);

        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void closeProgram(ActionEvent actionEvent) {
        stage.close();
    }

    @FXML
    public void onSignInClick(MouseEvent mouseEvent) throws IOException {
        // Load the sign-in FXML file (login-view.fxml)
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("login-view.fxml"));
        Parent signInRoot = fxmlLoader.load();

        // Get the stage from the current window
        Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
        LoginController controller = fxmlLoader.getController();
        //TODO:need to pass the services
        controller.setStage(stage);
        controller.setLoginService(serviceLastLogin);
        controller.setMainService(serviceFriendship);
        controller.setService(serviceSignUp);
        controller.setFReqService(serviceFriendRequest);
        // Create the sign-in scene and set it on the stage
        Scene signInScene = new Scene(signInRoot);
        stage.setScene(signInScene);
        stage.setTitle("Sign In");
    }
}

