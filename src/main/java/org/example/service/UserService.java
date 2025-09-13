package org.example.service;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new user with proper validation and password encryption
     */
    public User registerUser(User user) throws Exception {
        // Validate required fields
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered. Please use a different email.");
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Ensure proper initialization with default values
        if (user.getGpa() == null) user.setGpa(0.0);
        if (user.getYear() == null) user.setYear(0);
        if (user.getRole() == null) user.setRole(User.UserRole.STUDENT);

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsActive(true);
        user.setEmailVerified(false);

        return userRepository.save(user);
    }

    /**
     * Authenticate user login with encrypted password verification
     */
    public Optional<User> authenticateUser(String email, String rawPassword) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            return Optional.empty();
        }

        Optional<User> userOptional = userRepository.findByEmail(email.trim().toLowerCase());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check if user is active
            if (!user.getIsActive()) {
                return Optional.empty();
            }

            // Verify password
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                // Update last login time
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByEmail(email.trim().toLowerCase());
    }

    /**
     * Find user by ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    /**
     * Update user profile
     */
    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Update user password
     */
    public void updatePassword(Long userId, String newPassword) throws Exception {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (newPassword == null || newPassword.length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters long");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get users by role
     */
    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    /**
     * Get active users
     */
    public List<User> getActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }

    /**
     * Deactivate user account
     */
    public void deactivateUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setIsActive(false);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    /**
     * Get user statistics
     */
    public long getTotalActiveUsers() {
        return userRepository.countByIsActiveTrue();
    }

    /**
     * Find similar users for recommendations
     */
    public List<User> findSimilarUsers(User currentUser) {
        if (currentUser.getMajor() != null && currentUser.getYear() != null) {
            return userRepository.findSimilarUsers(
                currentUser.getMajor(),
                currentUser.getYear(),
                currentUser.getId()
            );
        }
        return List.of();
    }
}
