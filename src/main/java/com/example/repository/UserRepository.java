package com.example.repository;

import com.example.model.User;
import com.example.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class UserRepository extends MainRepository<User>{
    String FILE_PATH = "src/main/java/data/users.json";

    public UserRepository() {
    }

    @Override
    protected String getDataPath() {
        return FILE_PATH;
    }

    @Override
    protected Class<User[]> getArrayType() {
        return User[].class;
    }

    //6.2.2.1
    public ArrayList<User> getUsers(){
        return findAll();
    }

    //6.2.2.2
    public User getUserById(UUID userId) {
        return findAll().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    //6.2.2.3
    public User addUser(User user){
        save(user);
        return user;
    }

    //6.2.2.4
    public List<Order> getOrdersByUserId(UUID userId){
        return findAll().stream()
                .filter(user -> user.getId().equals(userId))
                .map(User::getOrders)
                .findFirst()
                .orElse(null);

    }

    //6.2.2.5
    public void addOrderToUser(UUID userId, Order order){
        List<User> users = findAll();
        for (User user : users) {
            if (user.getId().equals(userId)) {
                user.getOrders().add(order);
                saveAll(new ArrayList<>(users));
                return;
            }
        }
    }

    //6.2.2.6 Remove Order from User
    //Let the user remove one of his/her orders.
    public void removeOrderFromUser(UUID userId, UUID orderId){
        List<User> users = findAll();
        for (User user : users) {
            if (user.getId().equals(userId)) {
                user.getOrders().removeIf(order -> order.getId().equals(orderId));
                saveAll(new ArrayList<>(users));
                return;
            }
        }
    }

//    6.2.2.7 Delete User
//    Delete a user by passing his/her ID.
    public void deleteUserById(UUID userId){
        List<User> users = findAll();
        users.removeIf(user -> user.getId().equals(userId));
        saveAll(new ArrayList<>(users));

    }


}