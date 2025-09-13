package org.example.controller;

import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {

    @Autowired
    private UserService userService;

    /**
     * Handle user registration - This fixes the "Cannot POST /register.html" error
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, String> registrationData,
                                                           HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Extract registration data
            String email = registrationData.get("email");
            String name = registrationData.get("name");
            String password = registrationData.get("password");
            String confirmPassword = registrationData.get("confirmPassword");

            // Validate input
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (name == null || name.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Name is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Password is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (confirmPassword == null || !password.equals(confirmPassword)) {
                response.put("success", false);
                response.put("message", "Passwords do not match");
                return ResponseEntity.badRequest().body(response);
            }

            // Create new user
            User newUser = new User();
            newUser.setEmail(email.trim().toLowerCase());
            newUser.setName(name.trim());
            newUser.setPassword(password);

            // Set optional fields if provided
            if (registrationData.containsKey("major")) {
                newUser.setMajor(registrationData.get("major"));
            }
            if (registrationData.containsKey("year")) {
                try {
                    newUser.setYear(Integer.parseInt(registrationData.get("year")));
                } catch (NumberFormatException e) {
                    newUser.setYear(0);
                }
            }
            if (registrationData.containsKey("institution")) {
                newUser.setInstitution(registrationData.get("institution"));
            }
            if (registrationData.containsKey("phoneNumber")) {
                newUser.setPhoneNumber(registrationData.get("phoneNumber"));
            }

            // Register user
            User registeredUser = userService.registerUser(newUser);

            // Create session
            session.setAttribute("userId", registeredUser.getId());
            session.setAttribute("userEmail", registeredUser.getEmail());
            session.setAttribute("userName", registeredUser.getName());
            session.setAttribute("userRole", registeredUser.getRole().name());

            response.put("success", true);
            response.put("message", "Registration successful! Welcome to Study Portal!");
            response.put("user", createUserResponse(registeredUser));
            response.put("redirectUrl", "/dashboard.html");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handle user login - This fixes the "Cannot POST /login.html" error
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> loginData,
                                                        HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = loginData.get("email");
            String password = loginData.get("password");

            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Password is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Authenticate user
            Optional<User> userOptional = userService.authenticateUser(email, password);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Create session
                session.setAttribute("userId", user.getId());
                session.setAttribute("userEmail", user.getEmail());
                session.setAttribute("userName", user.getName());
                session.setAttribute("userRole", user.getRole().name());

                response.put("success", true);
                response.put("message", "Login successful! Welcome back, " + user.getName() + "!");
                response.put("user", createUserResponse(user));
                response.put("redirectUrl", "/dashboard.html");

                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid email or password. Please try again.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handle user logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logoutUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            session.invalidate();
            response.put("success", true);
            response.put("message", "Logout successful!");
            response.put("redirectUrl", "/index.html");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Logout failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get current user session info
     */
    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> getCurrentSession(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> userOptional = userService.findById(userId);
            if (userOptional.isPresent()) {
                response.put("authenticated", true);
                response.put("user", createUserResponse(userOptional.get()));
                return ResponseEntity.ok(response);
            }
        }

        response.put("authenticated", false);
        response.put("message", "No active session");
        return ResponseEntity.ok(response);
    }

    /**
     * Update user profile
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, String> profileData,
                                                            HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "Please login to update profile");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            Optional<User> userOptional = userService.findById(userId);
            if (!userOptional.isPresent()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            User user = userOptional.get();

            // Update allowed fields
            if (profileData.containsKey("name") && !profileData.get("name").trim().isEmpty()) {
                user.setName(profileData.get("name").trim());
            }
            if (profileData.containsKey("major")) {
                user.setMajor(profileData.get("major"));
            }
            if (profileData.containsKey("year")) {
                try {
                    user.setYear(Integer.parseInt(profileData.get("year")));
                } catch (NumberFormatException e) {
                    // Ignore invalid year
                }
            }
            if (profileData.containsKey("institution")) {
                user.setInstitution(profileData.get("institution"));
            }
            if (profileData.containsKey("phoneNumber")) {
                user.setPhoneNumber(profileData.get("phoneNumber"));
            }
            if (profileData.containsKey("learningStyle")) {
                user.setLearningStyle(profileData.get("learningStyle"));
            }
            if (profileData.containsKey("gpa")) {
                try {
                    user.setGpa(Double.parseDouble(profileData.get("gpa")));
                } catch (NumberFormatException e) {
                    // Ignore invalid GPA
                }
            }

            User updatedUser = userService.updateUser(user);

            response.put("success", true);
            response.put("message", "Profile updated successfully!");
            response.put("user", createUserResponse(updatedUser));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Profile update failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Check if email is available for registration
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();

        boolean exists = userService.existsByEmail(email);
        response.put("available", !exists);
        response.put("message", exists ? "Email already taken" : "Email available");

        return ResponseEntity.ok(response);
    }

    /**
     * Create a safe user response (without sensitive data like password)
     */
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("email", user.getEmail());
        userResponse.put("name", user.getName());
        userResponse.put("role", user.getRole().name());
        userResponse.put("gpa", user.getGpa());
        userResponse.put("year", user.getYear());
        userResponse.put("major", user.getMajor());
        userResponse.put("institution", user.getInstitution());
        userResponse.put("phoneNumber", user.getPhoneNumber());
        userResponse.put("learningStyle", user.getLearningStyle());
        userResponse.put("isActive", user.getIsActive());
        userResponse.put("createdAt", user.getCreatedAt());
        return userResponse;
    }
}
