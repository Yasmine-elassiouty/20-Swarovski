package com.example.repository;

import com.example.model.Order;
import com.example.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class UserRepository extends MainRepository<User>{
    String FILE_PATH = "src/main/java/com/example/data/users.json";

    public UserRepository() {
        super();
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
        if(userId == null){
            throw new IllegalArgumentException("Id passed cannot be null");
        } else
        return findAll().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    //6.2.2.3
    public User addUser(User user){
        if(user == null){
            throw new IllegalArgumentException("User cannot be null");
        }
        if(getUserById(user.getId()) != null){
            throw new IllegalArgumentException("User already exists");
        }
        if(user.getName() == null || user.getName().isBlank()){
            throw new IllegalArgumentException("User name cannot be null or blank");
        }
        save(user);
        return user;
    }

    //6.2.2.4
    public List<Order> getOrdersByUserId(UUID userId){
        if(userId == null){
            throw new IllegalArgumentException("Id passed cannot be null");
        }
        else if (getUserById(userId) == null){
            throw new NoSuchElementException("User not found");
        }
        else
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
        if(userId == null){
            throw new IllegalArgumentException("Id passed cannot be null");
        }
        else if (getUserById(userId) == null){
            throw new NoSuchElementException("User not found");
        }
        else if (orderId == null){
            throw new IllegalArgumentException("Id passed cannot be null");
        }
        else if (getUserById(userId).getOrders().stream().noneMatch(order -> order.getId().equals(orderId))){
            throw new NoSuchElementException("Order not found");
        }
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
        if(userId == null){
            throw new IllegalArgumentException("Id passed cannot be null");
        }
        List<User> users = findAll();
        boolean removed = users.removeIf(user -> user.getId().equals(userId));
        if (removed) {
            saveAll(new ArrayList<>(users));
        }
        else {
            throw new NoSuchElementException("User not found");
        }

    }


}