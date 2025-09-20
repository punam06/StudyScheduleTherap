package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class AIService {

    @Value("${app.ai.service.url:http://localhost:8085}")
    private String aiServiceUrl;

    @Value("${app.ai.service.enabled:true}")
    private boolean aiServiceEnabled;

    private final RestTemplate restTemplate;
    private final WebClient webClient;

    @Autowired
    private RealTimeAnalyticsService analyticsService;

    public AIService() {
        this.restTemplate = new RestTemplate();
        this.webClient = WebClient.builder().build();
    }

    public CompletableFuture<List<Map<String, Object>>> getPersonalizedRecommendationsAsync(Long userId) {
        if (!aiServiceEnabled) {
            return CompletableFuture.completedFuture(getDefaultRecommendations());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get real user analytics data
                Map<String, Object> userAnalytics = analyticsService.getPersonalizedAIInsights(userId);

                // Send user data to AI service for personalized recommendations
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(userAnalytics, headers);

                ResponseEntity<Map> response = restTemplate.postForEntity(
                    aiServiceUrl + "/api/recommendations", request, Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Map<String, Object> body = response.getBody();
                    return (List<Map<String, Object>>) body.get("recommendations");
                }
            } catch (Exception e) {
                System.err.println("Error fetching personalized AI recommendations: " + e.getMessage());
            }
            return getPersonalizedDefaultRecommendations(userId);
        });
    }

    public Mono<Map<String, Object>> analyzeRealTimeStudyDataAsync(Long userId) {
        if (!aiServiceEnabled) {
            return Mono.just(analyticsService.getPersonalizedAIInsights(userId));
        }

        return webClient.post()
                .uri(aiServiceUrl + "/api/analytics")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(analyticsService.getPersonalizedAIInsights(userId))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    if (response != null && (Boolean) response.get("success")) {
                        return (Map<String, Object>) response.get("analytics");
                    }
                    return analyticsService.getPersonalizedAIInsights(userId);
                })
                .onErrorReturn(analyticsService.getPersonalizedAIInsights(userId));
    }

    public Map<String, Object> optimizeSchedule(Map<String, Object> preferences) {
        if (!aiServiceEnabled) {
            return getDefaultOptimizedSchedule();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(preferences, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                aiServiceUrl + "/api/schedule/optimize", request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if ((Boolean) body.get("success")) {
                    return (Map<String, Object>) body.get("optimized_schedule");
                }
            }
        } catch (Exception e) {
            System.err.println("Error optimizing schedule: " + e.getMessage());
        }

        return getDefaultOptimizedSchedule();
    }

    public Map<String, Object> predictPerformance(Map<String, Object> currentData) {
        if (!aiServiceEnabled) {
            return getDefaultPerformancePrediction();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(currentData, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                aiServiceUrl + "/api/prediction/performance", request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if ((Boolean) body.get("success")) {
                    return (Map<String, Object>) body.get("prediction");
                }
            }
        } catch (Exception e) {
            System.err.println("Error predicting performance: " + e.getMessage());
        }

        return getDefaultPerformancePrediction();
    }

    public boolean isAIServiceHealthy() {
        if (!aiServiceEnabled) {
            return false;
        }

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                aiServiceUrl + "/health", Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    // Default fallback methods
    private List<Map<String, Object>> getDefaultRecommendations() {
        return List.of(
            Map.of(
                "title", "Regular Study Sessions",
                "description", "Maintain consistent daily study schedules for better retention",
                "priority", "High",
                "type", "scheduling"
            ),
            Map.of(
                "title", "Take Regular Breaks",
                "description", "Use the Pomodoro technique: 25 minutes study, 5 minutes break",
                "priority", "Medium",
                "type", "time_management"
            ),
            Map.of(
                "title", "Join Study Groups",
                "description", "Collaborative learning improves understanding and motivation",
                "priority", "Low",
                "type", "collaboration"
            )
        );
    }

    private Map<String, Object> getDefaultAnalytics() {
        return Map.of(
            "efficiency", 75,
            "focusTime", 4.5,
            "recommendedBreaks", List.of("25min", "50min", "75min"),
            "trends", Map.of(
                "weekly_improvement", 5.2,
                "consistency_score", 80,
                "peak_hours", List.of("9:00-11:00", "14:00-16:00")
            )
        );
    }

    private Map<String, Object> getDefaultOptimizedSchedule() {
        return Map.of(
            "schedule", List.of(
                Map.of("time", "09:00", "duration", 60, "effectiveness_score", 0.9),
                Map.of("time", "14:00", "duration", 45, "effectiveness_score", 0.8),
                Map.of("time", "19:00", "duration", 90, "effectiveness_score", 0.7)
            ),
            "optimization_score", 0.85
        );
    }

    private Map<String, Object> getDefaultPerformancePrediction() {
        return Map.of(
            "predicted_score", 82.5,
            "confidence", 0.75,
            "factors", List.of(
                Map.of("factor", "Study Consistency", "impact", 0.3, "current_score", 0.8)
            )
        );
    }

    private List<Map<String, Object>> getPersonalizedDefaultRecommendations(Long userId) {
        Map<String, Object> userInsights = analyticsService.getPersonalizedAIInsights(userId);
        Double efficiency = (Double) userInsights.get("efficiency");
        Integer dayStreak = (Integer) userInsights.get("dayStreak");
        Double studyHours = (Double) userInsights.get("studyHoursWeek");

        List<Map<String, Object>> recommendations = new java.util.ArrayList<>();

        if (efficiency < 60) {
            recommendations.add(Map.of(
                "title", "Improve Study Efficiency",
                "description", "Your current efficiency is " + efficiency + "%. Try breaking tasks into smaller chunks.",
                "priority", "High",
                "type", "efficiency_improvement"
            ));
        }

        if (dayStreak == 0) {
            recommendations.add(Map.of(
                "title", "Start Your Study Streak",
                "description", "Begin building consistency with just 15 minutes of focused study today.",
                "priority", "High",
                "type", "streak_building"
            ));
        } else if (dayStreak > 0) {
            recommendations.add(Map.of(
                "title", "Maintain Your Streak",
                "description", "Great job on your " + dayStreak + "-day streak! Keep the momentum going.",
                "priority", "Medium",
                "type", "streak_maintenance"
            ));
        }

        if (studyHours < 5) {
            recommendations.add(Map.of(
                "title", "Increase Study Time",
                "description", "Gradually increase your weekly study hours from " + studyHours + "h to reach your goals.",
                "priority", "Medium",
                "type", "time_management"
            ));
        }

        return recommendations;
    }
}
