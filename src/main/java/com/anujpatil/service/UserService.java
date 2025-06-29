package com.anujpatil.service;

import com.anujpatil.model.User;
import com.anujpatil.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(User user) {
        if (user.getActive() == null) {
            user.setActive(true);
        }
        if (user.getRating() == null) {
            user.setRating(5.0);
        }
        return userRepository.save(user);
    }

    @Transactional
    public Optional<User> updateUser(String userId, User updatedUser) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    if (updatedUser.getName() != null) {
                        existingUser.setName(updatedUser.getName());
                    }
                    if (updatedUser.getEmail() != null) {
                        existingUser.setEmail(updatedUser.getEmail());
                    }
                    if (updatedUser.getPhoneNumber() != null) {
                        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
                    }
                    if (updatedUser.getActive() != null) {
                        existingUser.setActive(updatedUser.getActive());
                    }
                    return userRepository.save(existingUser);
                });
    }

    @Transactional
    public Optional<User> updateUserRating(String userId, Double newRating) {
        return userRepository.findById(userId)
                .map(user -> {
                    Double currentRating = user.getRating();
                    if (currentRating == null) {
                        user.setRating(newRating);
                    } else {
                        user.setRating((currentRating + newRating) / 2);
                    }
                    user.setRating(Math.round(user.getRating() * 10.0) / 10.0);
                    return userRepository.save(user);
                });
    }

    @Transactional
    public Optional<User> setUserActiveStatus(String userId, boolean active) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setActive(active);
                    return userRepository.save(user);
                });
    }

    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public Iterable<User> getAllActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
