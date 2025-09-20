package org.example.controller;

import org.example.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    @Autowired
    private AIService aiService;

    @GetMapping("/recommendations")
    public CompletableFuture<ResponseEntity<List<Map<String, Object>>>> getRecommendations() {
        return aiService.getRecommendationsAsync()
                .thenApply(recommendations -> ResponseEntity.ok(recommendations))
                .exceptionally(throwable -> ResponseEntity.internalServerError().build());
    }

    @PostMapping("/recommendations/generate")
    public CompletableFuture<ResponseEntity<List<Map<String, Object>>>> generateRecommendations(
            @RequestBody(required = false) Map<String, Object> userData) {
        return aiService.getRecommendationsAsync()
                .thenApply(recommendations -> ResponseEntity.ok(recommendations))
                .exceptionally(throwable -> ResponseEntity.internalServerError().build());
    }

    @PostMapping("/analytics")
    public ResponseEntity<Map<String, Object>> analyzeStudyData(
            @RequestBody Map<String, Object> studyData) {
        try {
            Map<String, Object> analytics = aiService.analyzeStudyDataAsync(studyData).block();
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/schedule/optimize")
    public ResponseEntity<Map<String, Object>> optimizeSchedule(
            @RequestBody Map<String, Object> preferences) {
        try {
            Map<String, Object> optimizedSchedule = aiService.optimizeSchedule(preferences);
            return ResponseEntity.ok(optimizedSchedule);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/performance/predict")
    public ResponseEntity<Map<String, Object>> predictPerformance(
            @RequestBody Map<String, Object> currentData) {
        try {
            Map<String, Object> prediction = aiService.predictPerformance(currentData);
            return ResponseEntity.ok(prediction);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkAIHealth() {
        boolean isHealthy = aiService.isAIServiceHealthy();
        return ResponseEntity.ok(Map.of(
            "ai_service_healthy", isHealthy,
            "status", isHealthy ? "online" : "offline"
        ));
    }
}
