package org.example.controller;

import org.example.service.AIService;
import org.example.service.StudyGroupService;
import org.example.service.GroupSessionService;
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
public class AIEnhancedController {

    @Autowired
    private AIService aiService;

    @Autowired
    private StudyGroupService groupService;

    @Autowired
    private GroupSessionService sessionService;

    // AI Dashboard - Version 2 Feature
    @GetMapping("/dashboard")
    public String aiDashboard(Model model) {
        model.addAttribute("title", "AI-Enhanced Study Assistant");
        model.addAttribute("peakLearningWindows", aiService.getPeakLearningWindows());
        return "ai/dashboard";
    }

    // Natural Language Scheduling - Version 2 Feature
    @GetMapping("/natural-scheduling")
    public String naturalSchedulingPage(Model model) {
        model.addAttribute("title", "Natural Language Scheduling");
        return "ai/natural-scheduling";
    }

    @PostMapping("/api/parse-scheduling-request")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> parseSchedulingRequest(@RequestBody Map<String, String> request) {
        String naturalLanguageInput = request.get("input");
        Map<String, Object> parsedSchedule = aiService.parseNaturalLanguageScheduling(naturalLanguageInput);

        return ResponseEntity.ok(parsedSchedule);
    }

    // Predictive Scheduling Analytics - Version 2 Feature
    @PostMapping("/api/predictive-analytics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPredictiveAnalytics(@RequestBody Map<String, Object> requestData) {
        String subject = (String) requestData.get("subject");
        @SuppressWarnings("unchecked")
        List<String> timeStrings = (List<String>) requestData.get("proposedTimes");

        // Convert string times to LocalDateTime (simplified for demo)
        List<LocalDateTime> proposedTimes = timeStrings.stream()
                .map(timeStr -> LocalDateTime.now().plusHours(Integer.parseInt(timeStr.split(":")[0])))
                .toList();

        Map<String, Object> analytics = aiService.getPredictiveSchedulingAnalytics(proposedTimes, subject);

        return ResponseEntity.ok(analytics);
    }

    // Enhanced Group Matching - Version 2 Feature
    @PostMapping("/api/enhanced-matching")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getEnhancedGroupMatching(@RequestBody Map<String, Object> studentProfile) {
        String subject = (String) studentProfile.get("subject");
        Map<String, Object> matching = aiService.getEnhancedGroupMatching(subject, studentProfile);

        return ResponseEntity.ok(matching);
    }

    // Group Dynamics Insights - Version 2 Feature
    @GetMapping("/group-dynamics/{groupId}")
    public String groupDynamicsPage(@PathVariable Long groupId, Model model) {
        model.addAttribute("groupId", groupId);
        model.addAttribute("title", "Group Dynamics Insights");

        // Get group information
        groupService.getGroupById(groupId).ifPresent(group -> {
            model.addAttribute("group", group);
        });

        return "ai/group-dynamics";
    }

    @PostMapping("/api/analyze-group-dynamics/{groupId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> analyzeGroupDynamics(
            @PathVariable Long groupId,
            @RequestBody Map<String, List<String>> requestData) {

        List<String> chatMessages = requestData.get("chatMessages");
        Map<String, Object> insights = aiService.analyzeGroupDynamics(groupId, chatMessages);

        return ResponseEntity.ok(insights);
    }

    // Collaborative Content AI - Version 2 Feature
    @GetMapping("/collaborative-content")
    public String collaborativeContentPage(Model model) {
        model.addAttribute("title", "AI-Generated Study Content");
        return "ai/collaborative-content";
    }

    @PostMapping("/api/generate-content")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateCollaborativeContent(@RequestBody Map<String, String> request) {
        String subject = request.get("subject");
        String topic = request.get("topic");
        String syllabusContent = request.get("syllabusContent");

        Map<String, Object> content = aiService.generateCollaborativeContent(subject, topic, syllabusContent);

        return ResponseEntity.ok(content);
    }

    // Spaced Repetition Scheduler - Version 2 Feature
    @GetMapping("/spaced-repetition")
    public String spacedRepetitionPage(Model model) {
        model.addAttribute("title", "AI-Powered Spaced Repetition");
        return "ai/spaced-repetition";
    }

    @PostMapping("/api/generate-spaced-schedule")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> generateSpacedRepetitionSchedule(@RequestBody Map<String, String> request) {
        String subject = request.get("subject");

        // Use the private method through a public wrapper
        Map<String, Object> analytics = aiService.getPredictiveSchedulingAnalytics(List.of(), subject);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> spacedSchedule = (List<Map<String, Object>>) analytics.get("spacedRepetitionSchedule");

        return ResponseEntity.ok(spacedSchedule);
    }

    // Advanced Study Recommendations - Version 2 Feature
    @GetMapping("/study-recommendations")
    public String studyRecommendationsPage(Model model) {
        model.addAttribute("title", "Personalized Study Recommendations");
        return "ai/study-recommendations";
    }

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

    // Learning Style Assessment - Version 2 Feature
    @GetMapping("/learning-style-assessment")
    public String learningStyleAssessment(Model model) {
        model.addAttribute("title", "VARK Learning Style Assessment");
        return "ai/learning-style-assessment";
    }

    @PostMapping("/api/assess-learning-style")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> assessLearningStyle(@RequestBody Map<String, Object> responses) {
        // Simple VARK assessment logic
        Map<String, Object> assessment = Map.of(
            "primaryStyle", "VISUAL",
            "secondaryStyle", "KINESTHETIC",
            "styleBreakdown", Map.of(
                "VISUAL", 35,
                "AUDITORY", 20,
                "KINESTHETIC", 30,
                "READING_WRITING", 15
            ),
            "recommendations", List.of(
                "Use visual aids like diagrams and mind maps",
                "Incorporate hands-on activities in study sessions",
                "Combine reading with visual elements"
            )
        );

        return ResponseEntity.ok(assessment);
    }

    // Session Optimization - Version 2 Feature
    @GetMapping("/session-optimization")
    public String sessionOptimization(Model model) {
        model.addAttribute("title", "AI Session Optimization");
        return "ai/session-optimization";
    }

    @PostMapping("/api/optimize-session")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> optimizeSession(@RequestBody Map<String, Object> sessionData) {
        String subject = (String) sessionData.get("subject");
        Integer duration = (Integer) sessionData.get("duration");
        String sessionType = (String) sessionData.get("sessionType");

        Map<String, Object> optimization = Map.of(
            "optimalBreaks", List.of("25 min", "15 min", "25 min", "15 min", "25 min"),
            "technique", "Pomodoro Technique with subject-specific adaptations",
            "productivity", Map.of(
                "score", 87,
                "improvements", List.of(
                    "Add 5-minute warm-up review",
                    "Include active recall exercises",
                    "End with concept summary"
                )
            ),
            "aiSuggestions", aiService.getSubjectSpecificAdvice(subject)
        );

        return ResponseEntity.ok(optimization);
    }

    // Smart Notifications - Version 2 Feature
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
            ),
            Map.of(
                "id", 3,
                "type", "GROUP_SUGGESTION",
                "title", "New Compatible Group",
                "message", "Found a 95% compatible Chemistry study group",
                "priority", "LOW",
                "action", "View Group"
            )
        );

        return ResponseEntity.ok(notifications);
    }
}
