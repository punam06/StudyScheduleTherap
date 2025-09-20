package org.example.controller;

import org.example.entity.Student;
import org.example.entity.StudySchedule;
import org.example.service.StudentService;
import org.example.service.StudyScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudyScheduleService scheduleService;

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserProfile(HttpSession session) {
        Student currentUser = (Student) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        // Refresh user data from database
        Student refreshedUser = studentService.getStudentById(currentUser.getId())
                .orElse(currentUser);

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("id", refreshedUser.getId());
        profileData.put("name", refreshedUser.getName());
        profileData.put("email", refreshedUser.getEmail());
        profileData.put("major", refreshedUser.getMajor() != null ? refreshedUser.getMajor() : "Not specified");
        profileData.put("year", refreshedUser.getYear() != null ? refreshedUser.getYear() : 1);
        profileData.put("gpa", refreshedUser.getGpa() != null ? refreshedUser.getGpa() : 0.0);
        profileData.put("phoneNumber", refreshedUser.getPhoneNumber() != null ? refreshedUser.getPhoneNumber() : "");
        profileData.put("learningStyle", refreshedUser.getLearningStyle() != null ? refreshedUser.getLearningStyle() : "VISUAL");
        profileData.put("role", refreshedUser.getRole().name());
        profileData.put("createdAt", refreshedUser.getCreatedAt());
        profileData.put("updatedAt", refreshedUser.getUpdatedAt());

        return ResponseEntity.ok(profileData);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(HttpSession session) {
        Student currentUser = (Student) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        // Get user's study schedules
        List<StudySchedule> allSchedules = scheduleService.getSchedulesByStudentId(currentUser.getId());
        long completedSchedules = allSchedules.stream()
                .filter(schedule -> schedule.getStatus() != null &&
                       schedule.getStatus().equals("COMPLETED"))
                .count();

        // Calculate total study hours (sum of all schedule durations)
        double totalStudyHours = allSchedules.stream()
                .mapToDouble(schedule -> {
                    if (schedule.getStartTime() != null && schedule.getEndTime() != null) {
                        return java.time.Duration.between(schedule.getStartTime(), schedule.getEndTime()).toMinutes() / 60.0;
                    }
                    return 0.0;
                })
                .sum();

        // Get study groups count
        int studyGroupsCount = currentUser.getStudyGroups() != null ? currentUser.getStudyGroups().size() : 0;

        // Calculate study streak (simplified - days with completed schedules)
        long studyStreak = calculateStudyStreak(currentUser.getId());

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSchedules", allSchedules.size());
        stats.put("completedSchedules", completedSchedules);
        stats.put("totalStudyHours", Math.round(totalStudyHours * 10.0) / 10.0); // Round to 1 decimal
        stats.put("studyGroups", studyGroupsCount);
        stats.put("studyStreak", studyStreak);
        stats.put("completionRate", allSchedules.size() > 0 ?
                   Math.round((completedSchedules * 100.0 / allSchedules.size()) * 10.0) / 10.0 : 0.0);
        stats.put("focusScore", calculateFocusScore(allSchedules));

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivity(HttpSession session) {
        Student currentUser = (Student) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(null);
        }

        // Get recent schedules (last 10)
        List<StudySchedule> recentSchedules = scheduleService.getRecentSchedulesByStudentId(currentUser.getId(), 10);

        List<Map<String, Object>> activities = recentSchedules.stream()
                .map(schedule -> {
                    Map<String, Object> activity = new HashMap<>();
                    activity.put("id", schedule.getId());
                    activity.put("title", schedule.getSubject() + " Study Session");
                    activity.put("description", schedule.getDescription() != null ? schedule.getDescription() : "Study session completed");
                    activity.put("status", schedule.getStatus() != null ? schedule.getStatus() : "PENDING");
                    activity.put("createdAt", schedule.getCreatedAt());
                    activity.put("type", "STUDY_SESSION");
                    return activity;
                })
                .toList();

        return ResponseEntity.ok(activities);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, Object> profileData,
                                                            HttpSession session) {
        Student currentUser = (Student) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        try {
            // Update user profile
            Student updatedUser = studentService.updateStudentProfile(currentUser.getId(), profileData);
            session.setAttribute("currentUser", updatedUser);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Profile updated successfully",
                "user", Map.of(
                    "name", updatedUser.getName(),
                    "major", updatedUser.getMajor(),
                    "year", updatedUser.getYear(),
                    "phoneNumber", updatedUser.getPhoneNumber() != null ? updatedUser.getPhoneNumber() : ""
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Failed to update profile: " + e.getMessage()
            ));
        }
    }

    private long calculateStudyStreak(Long studentId) {
        // Simplified streak calculation - count consecutive days with completed schedules
        // In a real implementation, you'd check consecutive days from today backwards
        List<StudySchedule> completedSchedules = scheduleService.getCompletedSchedulesByStudentId(studentId);
        return Math.min(completedSchedules.size(), 30); // Cap at 30 days for demo
    }

    private double calculateFocusScore(List<StudySchedule> schedules) {
        // Simplified focus score based on completion rate and consistency
        if (schedules.isEmpty()) return 0.0;

        long completed = schedules.stream()
                .filter(s -> "COMPLETED".equals(s.getStatus()))
                .count();

        double completionRate = (double) completed / schedules.size();
        return Math.round(completionRate * 100 * 0.85 + 15); // Base score + completion bonus
    }
}
