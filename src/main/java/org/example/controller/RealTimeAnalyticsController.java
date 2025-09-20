package org.example.controller;

import org.example.service.RealTimeAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class RealTimeAnalyticsController {

    @Autowired
    private RealTimeAnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getRealTimeDashboard(Authentication auth) {
        try {
            // Get user ID from authentication (for now using mock ID)
            Long userId = getUserIdFromAuth(auth);

            Map<String, Object> insights = analyticsService.getPersonalizedAIInsights(userId);
            return ResponseEntity.ok(insights);
        } catch (Exception e) {
            // Return mock data if user not authenticated
            return ResponseEntity.ok(getMockDashboardData());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateAnalytics(Authentication auth) {
        try {
            Long userId = getUserIdFromAuth(auth);

            analyticsService.calculateRealTimeMetrics(userId);
            Map<String, Object> insights = analyticsService.getPersonalizedAIInsights(userId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Analytics updated successfully",
                "data", insights
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Using demo data",
                "data", getMockDashboardData()
            ));
        }
    }

    @PostMapping("/session-completed")
    public ResponseEntity<Map<String, Object>> onSessionCompleted(
            @RequestBody Map<String, Object> sessionData,
            Authentication auth) {
        try {
            Long userId = getUserIdFromAuth(auth);

            // Update analytics when session is completed
            analyticsService.calculateRealTimeMetrics(userId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Session completion recorded",
                "updatedAnalytics", analyticsService.getPersonalizedAIInsights(userId)
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Demo mode - no real updates"
            ));
        }
    }

    private Long getUserIdFromAuth(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            // Extract user ID from authentication
            return 1L; // Mock user ID for demo
        }
        return 1L; // Default to demo user
    }

    private Map<String, Object> getMockDashboardData() {
        return Map.of(
            "efficiency", 75.0,
            "goalsCompleted", 0,
            "studyHoursWeek", 0.0,
            "dayStreak", 1,
            "focusScore", 68.0,
            "learningVelocity", 45.0,
            "consistencyScore", 32.0,
            "preferredStudyTime", "Morning",
            "recommendations", java.util.List.of(
                "Start with small 25-minute focused sessions",
                "Set achievable daily study goals",
                "Create a consistent study schedule"
            ),
            "motivationalMessage", "Every expert was once a beginner. You've got this! 💪",
            "nextGoals", java.util.List.of(
                "Complete your first study session today",
                "Build a 3-day study streak",
                "Set up your study schedule"
            )
        );
    }
}
