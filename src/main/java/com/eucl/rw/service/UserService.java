package com.eucl.rw.service;

import com.eucl.rw.enums.ERole;
import com.eucl.rw.model.Meter;
import com.eucl.rw.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;
import java.util.UUID;


public interface UserService {
    User registerUser(User user, Set<ERole> roles);
    User authenticateUser(String email, String password);
    User getUserById(UUID id);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    User updateUser(UUID userId, User userDetails);
    void deleteUser(UUID userId);
    boolean hasAdminRole(User user);
    void assignMeterToUser(UUID userId, String meterNumber);
    List<Meter> getUserMeters(UUID userId);
    UserDetails loadUserByUsername(String emailFromToken);
}
