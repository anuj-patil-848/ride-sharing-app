package com.anujpatil.resolver;

import com.anujpatil.model.User;
import com.anujpatil.service.RideService;
import com.anujpatil.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class UserResolver {

    private final UserService userService;
    private final RideService rideService;

    @Autowired
    public UserResolver(UserService userService, RideService rideService) {
        this.userService = userService;
        this.rideService = rideService;
    }

    @QueryMapping
    public Optional<User> user(@Argument String id) {
        return userService.getUserById(id);
    }

    @QueryMapping
    public Optional<User> userByEmail(@Argument String email) {
        return userService.getUserByEmail(email);
    }

    @QueryMapping
    public Optional<User> userByPhoneNumber(@Argument String phoneNumber) {
        return userService.getUserByPhoneNumber(phoneNumber);
    }

    @QueryMapping
    public List<User> allUsers() {
        return userService.getAllUsers();
    }

    @QueryMapping
    public Iterable<User> activeUsers() {
        return userService.getAllActiveUsers();
    }

    @MutationMapping
    public User createUser(@Argument("input") Map<String, Object> input) {
        User user = User.builder()
                .name((String) input.get("name"))
                .email((String) input.get("email"))
                .phoneNumber((String) input.get("phoneNumber"))
                .active(true)
                .build();
        return userService.createUser(user);
    }

    @MutationMapping
    public Optional<User> updateUser(@Argument String id, @Argument("input") Map<String, Object> input) {
        User updatedUser = User.builder()
                .name((String) input.get("name"))
                .email((String) input.get("email"))
                .phoneNumber((String) input.get("phoneNumber"))
                .active((Boolean) input.get("active"))
                .build();
        return userService.updateUser(id, updatedUser);
    }

    @MutationMapping
    public Optional<User> updateUserRating(@Argument String id, @Argument Double rating) {
        return userService.updateUserRating(id, rating);
    }

    @MutationMapping
    public Optional<User> setUserActiveStatus(@Argument String id, @Argument Boolean active) {
        return userService.setUserActiveStatus(id, active);
    }

    @SchemaMapping(typeName = "User", field = "ridesAsRider")
    public List<com.anujpatil.model.Ride> getRidesAsRider(User user) {
        return rideService.getRidesByRider(user);
    }

    @SchemaMapping(typeName = "User", field = "ridesAsDriver")
    public List<com.anujpatil.model.Ride> getRidesAsDriver(User user) {
        return rideService.getRidesByDriver(user);
    }
}
