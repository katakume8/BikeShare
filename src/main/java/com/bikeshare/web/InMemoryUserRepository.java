package com.bikeshare.web;

import com.bikeshare.model.User;
import com.bikeshare.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Simple in-memory implementation of UserRepository for demonstration purposes.
 */
public class InMemoryUserRepository implements UserRepository {
    
    private final Map<String, User> users = new ConcurrentHashMap<>();
    
    @Override
    public User save(User user) {
        users.put(user.getUserId(), user);
        return user;
    }
    
    @Override
    public Optional<User> findById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> Objects.equals(user.getEmail(), email))
                .findFirst();
    }
    
    @Override
    public List<User> findByStatus(User.UserStatus status) {
        return users.values().stream()
                .filter(user -> user.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<User> findByMembershipType(User.MembershipType membershipType) {
        return users.values().stream()
                .filter(user -> user.getMembershipType() == membershipType)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<User> findByRegistrationDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return users.values().stream()
                .filter(user -> {
                    LocalDateTime regDate = user.getRegistrationDate();
                    return regDate.isAfter(startDate) && regDate.isBefore(endDate);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public long count() {
        return users.size();
    }
    
    @Override
    public void deleteById(String userId) {
        users.remove(userId);
    }
    
    @Override
    public boolean existsById(String userId) {
        return users.containsKey(userId);
    }
}
