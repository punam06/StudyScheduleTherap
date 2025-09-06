package org.example.controller;

import org.example.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    // AI Dashboard - Main AI interface
    @GetMapping("/dashboard")
    public String aiDashboard(Model model) {
        model.addAttribute("title", "AI-Enhanced Study Assistant");
        model.addAttribute("peakLearningWindows", aiService.getOptimalStudyTimes());
        return "ai/dashboard";
    }

    // AI Recommendations endpoint
    @GetMapping("/recommendations")
    public String aiRecommendations(Model model) {
        model.addAttribute("title", "AI Study Recommendations");
        return "ai-recommendations";
    }

    // Natural Language Scheduling API
    @PostMapping("/api/parse-scheduling")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> parseSchedulingRequest(@RequestBody Map<String, String> request) {
        String naturalLanguageInput = request.get("input");
        Map<String, Object> parsedSchedule = aiService.parseNaturalLanguageScheduling(naturalLanguageInput);
        return ResponseEntity.ok(parsedSchedule);
    }

    // Enhanced Group Matching API
    @PostMapping("/api/enhanced-matching")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getEnhancedGroupMatching(@RequestBody Map<String, Object> studentProfile) {
        String subject = (String) studentProfile.get("subject");
        Map<String, Object> matching = aiService.getEnhancedGroupMatching(subject, studentProfile);
        return ResponseEntity.ok(matching);
    }

    // Group Dynamics Analysis API
    @PostMapping("/api/analyze-group-dynamics/{groupId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> analyzeGroupDynamics(
            @PathVariable Long groupId,
            @RequestBody Map<String, List<String>> requestData) {

        List<String> chatMessages = requestData.get("chatMessages");
        Map<String, Object> insights = aiService.analyzeGroupDynamics(groupId, chatMessages);
        return ResponseEntity.ok(insights);
    }

    // Collaborative Content Generation API
    @PostMapping("/api/generate-content")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateCollaborativeContent(@RequestBody Map<String, String> request) {
        String subject = request.get("subject");
        String topic = request.get("topic");
        String syllabusContent = request.get("syllabusContent");

        Map<String, Object> content = aiService.generateCollaborativeContent(subject, topic, syllabusContent);
        return ResponseEntity.ok(content);
    }

    // Study Recommendations API
    @PostMapping("/api/detailed-recommendations")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDetailedRecommendations(@RequestBody Map<String, Object> request) {
        String subject = (String) request.get("subject");
        Integer currentHours = (Integer) request.get("currentStudyHours");
        String difficulty = (String) request.get("difficulty");
        String goalType = (String) request.get("goalType");

        Map<String, Object> recommendations = aiService.getDetailedStudyRecommendation(
            subject, currentHours, difficulty, goalType);

        return ResponseEntity.ok(recommendations);
    }

    // Smart Notifications API
    @GetMapping("/api/smart-notifications")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getSmartNotifications() {
        List<Map<String, Object>> notifications = List.of(
            Map.of(
                "id", 1,
                "type", "STUDY_REMINDER",
                "title", "Optimal Study Time",
                "message", "Now is peak concentration time for Mathematics!",
                "priority", "HIGH",
                "action", "Start Session"
            ),
            Map.of(
                "id", 2,
                "type", "SPACED_REPETITION",
                "title", "Review Due",
                "message", "Physics concepts from 3 days ago need review",
                "priority", "MEDIUM",
                "action", "Review Now"
            )
        );

        return ResponseEntity.ok(notifications);
    }
}
