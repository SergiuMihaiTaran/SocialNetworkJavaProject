package com.example.demo;

import com.example.demo.domain.FriendShip;
import com.example.demo.domain.User;
import com.example.demo.repository.database.FriendRequestDataBaseRepository;
import com.example.demo.repository.database.LastLoginDataBaseRepository;
import com.example.demo.repository.database.FriendShipDataBaseRepository;
import com.example.demo.repository.database.UserDataBaseRepository;
import com.example.demo.service.UserService;
import com.example.demo.service.FriendshipService;
import com.example.demo.service.FriendRequestService;
import com.example.demo.service.ServiceLastLogin;
import com.example.demo.validators.FriendRequestValidator;
import com.example.demo.validators.FriendValidator;
import com.example.demo.validators.LastLoginValidator;
import com.example.demo.validators.UtilizatorValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Collection;

public class StartApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        UserDataBaseRepository repository = new UserDataBaseRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "postgres", // PostgreSQL username
                "postgres", // leave password empty
                new UtilizatorValidator()
        );
        FriendShipDataBaseRepository repositoryFriends = new FriendShipDataBaseRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "postgres", // PostgreSQL username
                "postgres", // leave password empty
                new FriendValidator()
        );
        UserService serviceUsers=new UserService(repository);
        FriendshipService serviceFriend=new FriendshipService(repositoryFriends);
        //GeneralServiceFriends servFriend=new GeneralServiceFriends(repoFriend,serv);
        FriendRequestDataBaseRepository repositoryFR=new FriendRequestDataBaseRepository("jdbc:postgresql://localhost:5432/socialnetwork",
                "postgres", // PostgreSQL username
                "postgres",new FriendRequestValidator()); // leave password empty)
        FriendRequestService serviceFriendRequests=new FriendRequestService(repositoryFR);
        // Test findAll
        LastLoginDataBaseRepository repositoryLogin=new LastLoginDataBaseRepository("jdbc:postgresql://localhost:5432/socialnetwork",
                "postgres", // PostgreSQL username
                "postgres",new LastLoginValidator()); // leave password empty)
        ServiceLastLogin serviceLastLogin=new ServiceLastLogin(repositoryLogin);
        Collection<User> users=(Collection<User>) repository.findAll();
        for (User user : users) {
            System.out.println(user);
            //System.out.println(users.size());
        }
        Collection<FriendShip> friends=(Collection<FriendShip>) repositoryFriends.findAll();
        for (FriendShip user : friends) {
            System.out.println(user);
            //System.out.println(users.size());
        }
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("login-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        LoginController controller = fxmlLoader.getController();
        controller.setStage(stage);
        controller.setService(serviceUsers);
        controller.setMainService(serviceFriend);
        controller.setFReqService(serviceFriendRequests);
        controller.setLoginService(serviceLastLogin);
        System.out.println(serviceUsers);
        stage.setTitle("SocialNetwork Demo");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}