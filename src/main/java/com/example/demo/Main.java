package com.example.demo;

import com.example.demo.Utils.Pageable;
import com.example.demo.domain.FriendShip;
import com.example.demo.domain.User;
import com.example.demo.repository.database.LastLoginDataBaseRepository;
import com.example.demo.repository.database.MessageDataBaseRepository;
import com.example.demo.repository.database.FriendShipDataBaseRepository;
import com.example.demo.repository.database.UserDataBaseRepository;
import com.example.demo.service.UserService;
import com.example.demo.service.FriendshipService;
import com.example.demo.validators.FriendValidator;
import com.example.demo.validators.LastLoginValidator;
import com.example.demo.validators.MessageValidator;
import com.example.demo.validators.UtilizatorValidator;

import java.util.*;
// ubb.scs.map.service.ServiceUsers;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {


    public static void main(String[] args) {


        //Repository<Long, Utilizator> repo = new InMemoryRepository<Long, Utilizator>(new UtilizatorValidator());
        // UtilizatorRepository repoFile = new UtilizatorRepository(new UtilizatorValidator(), "./data/utilizatori.txt");
        //FriendRepository repoFriend=new FriendRepository(new FriendValidator(),"./data/friendship.txt");
        //GeneralServiceUsers serv=new GeneralServiceUsers(repoFile);
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
        UserService serviceUsers = new UserService(repository);
        FriendshipService serviceFriend = new FriendshipService(repositoryFriends);
        //GeneralServiceFriends servFriend=new GeneralServiceFriends(repoFriend,serv);

        // Test findAll

        Collection<User> users = (Collection<User>) repository.findAll();
        for (User user : users) {
            System.out.println(user);
            //System.out.println(users.size());
        }
        Collection<FriendShip> friends = (Collection<FriendShip>) repositoryFriends.findAll();
        for (FriendShip user : friends) {
            System.out.println(user);
            //System.out.println(users.size());
        }
        //ConsoleUI console=new ConsoleUI(serviceUsers,serviceFriend);
        //console.start();
        Iterable<User> allUsers = repository.findAll();
        ArrayList<User> l = new ArrayList<>();
        for (User user : allUsers) {
            l.add(user);
        }
        MessageDataBaseRepository repository2 = new MessageDataBaseRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "postgres", // PostgreSQL username
                "postgres", // leave password empty
                new MessageValidator()
        );

        repository2.findAll().forEach(System.out::println);
        LastLoginDataBaseRepository repo2 = new LastLoginDataBaseRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "postgres", // PostgreSQL username
                "postgres", // leave password empty
                new LastLoginValidator()
        );
        repositoryFriends.findAllOnPage(new Pageable(1,1), 8L).getElementsOnPage().forEach(System.out::println);
    }
}



