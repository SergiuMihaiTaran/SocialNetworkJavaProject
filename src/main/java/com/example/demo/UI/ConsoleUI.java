package com.example.demo.UI;

import com.example.demo.domain.FriendShip;
import com.example.demo.domain.Tuple;
import com.example.demo.domain.User;
import com.example.demo.service.AbstractService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class ConsoleUI {
    AbstractService serv;
    AbstractService servFriend;
    public static void printMeniu(){

        System.out.println("1.Add User");
        System.out.println("2.Remove User");
        System.out.println("3.Add Friend");
        System.out.println("4.Remove Friend");
        System.out.println("5.Print number of groups");
        System.out.println("6.Print most social group");
    }

    public ConsoleUI(AbstractService serv,AbstractService servFriend) {
        this.serv = serv;
        this.servFriend = servFriend;
    }
    public void start(){
        Collection<User> colUsers=(Collection<User>) serv.getEntities();
        Collection<FriendShip> colFriends=(Collection<FriendShip>) servFriend.getEntities();
        for(User u:colUsers){
            ArrayList<User> FriendList=new ArrayList<>();
            for(FriendShip p:colFriends){
                if(p.getId1()== u.getId())
                    //FriendList.add(serv.getUser(p.getId2()));
                    continue;
            }

            u.setFriends(FriendList);
            printMeniu();
            int option;
            Scanner scanner = new Scanner(System.in);
            while(true){
                printMeniu();
                option=Integer.parseInt(scanner.nextLine());
                if(option==1){
                    Scanner sc = new Scanner(System.in);
                    System.out.println("Enter the first name of the user: ");
                    String firstName = sc.nextLine();
                    System.out.println("Enter the last name of the user: ");
                    String lastName = sc.nextLine();
                    User newUser = new User(firstName, lastName);
                    Collection c = (Collection) serv.getEntities();
                    long newId = c.size() + 1;
                    newUser.setId(newId);
                    serv.addEntity(newUser);
                }
                if(option==2){
                    Scanner sc = new Scanner(System.in);
                    System.out.println("Enter the id of the user: ");
                    long userId = sc.nextLong();
                    serv.removeEntity(userId);
                }
                if(option==3){
                    Scanner sc = new Scanner(System.in);
                    System.out.println("Enter the id of the first user: ");
                    long firstId = sc.nextLong();
                    System.out.println("Enter the id of the second user: ");
                    long secondId = sc.nextLong();

                    servFriend.addEntity(new FriendShip(firstId, secondId, LocalDateTime.now()));

                    servFriend.addEntity(new FriendShip(secondId, firstId,LocalDateTime.now()));

                    System.out.println(servFriend.getEntities());
                }
                if(option==4){
                    Scanner sc = new Scanner(System.in);
                    System.out.println("Enter the id of the first user: ");
                    long firstId = sc.nextLong();
                    System.out.println("Enter the id of the second user: ");
                    long secondId = sc.nextLong();
                    servFriend.removeEntity(new Tuple<Long, Long>(firstId, secondId));
                    servFriend.removeEntity(new Tuple<Long, Long>(secondId, firstId));
                    System.out.println(servFriend.getEntities());
                }
                if(option==5){
                    //System.out.println(servFriend.getComunities());
                }
                if(option==6){
                    //System.out.println(servFriend.getBiggestComunity());
                }
                if(option==7){
                    serv.getEntities().forEach(System.out::println);
                }
                if(option==8){
                    servFriend.getEntities().forEach(System.out::println);
                }
            }

        }
    }
}
