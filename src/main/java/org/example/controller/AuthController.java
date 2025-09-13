package org.example.controller;

import org.example.entity.Student;
import org.example.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("student", new Student());
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                              @RequestParam String password,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            Optional<Student> student = studentService.authenticateStudent(email, password);
            if (student.isPresent()) {
                session.setAttribute("currentUser", student.get());
                session.setAttribute("userId", student.get().getId());
                session.setAttribute("userRole", student.get().getRole().name());

                redirectAttributes.addFlashAttribute("successMessage",
                    "Welcome back, " + student.get().getName() + "!");
                return "redirect:/dashboard";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                    "Invalid email or password. Please try again.");
                return "redirect:/auth/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Login failed: " + e.getMessage());
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("student", new Student());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute Student student,
                                    @RequestParam String confirmPassword,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Validate password confirmation
            if (!student.getPassword().equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                    "Passwords do not match. Please try again.");
                return "redirect:/auth/register";
            }

            // Check if email already exists
            if (studentService.existsByEmail(student.getEmail())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                    "Email already registered. Please use a different email or login.");
                return "redirect:/auth/register";
            }

            Student savedStudent = studentService.registerStudent(student);
            redirectAttributes.addFlashAttribute("successMessage",
                "Registration successful! Please login with your credentials.");
            return "redirect:/auth/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Registration failed: " + e.getMessage());
            return "redirect:/auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out successfully.");
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        Student currentUser = (Student) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // Refresh user data from database
        Optional<Student> refreshedUser = studentService.getStudentById(currentUser.getId());
        if (refreshedUser.isPresent()) {
            model.addAttribute("student", refreshedUser.get());
            session.setAttribute("currentUser", refreshedUser.get());
        } else {
            model.addAttribute("student", currentUser);
        }

        return "auth/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Student student,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            Student currentUser = (Student) session.getAttribute("currentUser");
            if (currentUser == null) {
                return "redirect:/auth/login";
            }

            // Update only allowed fields
            student.setId(currentUser.getId());
            student.setEmail(currentUser.getEmail()); // Keep original email
            student.setPassword(currentUser.getPassword()); // Keep original password
            student.setRole(currentUser.getRole()); // Keep original role

            Student updatedStudent = studentService.updateStudent(student);
            session.setAttribute("currentUser", updatedStudent);

            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/auth/profile";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Profile update failed: " + e.getMessage());
            return "redirect:/auth/profile";
        }
    }
}
