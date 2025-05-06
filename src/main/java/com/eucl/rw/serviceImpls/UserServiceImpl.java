package com.eucl.rw.serviceImpls;

import com.eucl.rw.enums.ERole;
import com.eucl.rw.model.Meter;
import com.eucl.rw.model.User;
import com.eucl.rw.repository.MeterRepository;
import com.eucl.rw.repository.UserRepository;
import com.eucl.rw.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MeterRepository meterRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, MeterRepository meterRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.meterRepository = meterRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(User user, Set<ERole> roles) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.existsByPhone(user.getPhone())) {
            throw new RuntimeException("Phone number already in use");
        }
        if (userRepository.existsByNationalId(user.getNationalId())) {
            throw new RuntimeException("National ID already registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UUID userId, User userDetails) {
        User user = getUserById(userId);
        user.setName(userDetails.getName());
        user.setPhone(userDetails.getPhone());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }

    @Override
    public boolean hasAdminRole(User user) {
        return user.getRoles().contains(ERole.ROLE_ADMIN);
    }

    @Override
    public void assignMeterToUser(UUID userId, String meterNumber) {
        User user = getUserById(userId);
        Meter meter = new Meter(meterNumber, user, null);
        meterRepository.save(meter);
    }

    @Override
    public List<Meter> getUserMeters(UUID userId) {
        return meterRepository.findByUser_Id(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String emailFromToken) {
        return null;
    }
}