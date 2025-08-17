package org.example.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AIService {

    private final WebClient webClient;

    public AIService() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8085") // AI model service URL
                .build();
    }

    public Mono<String> getStudyRecommendation(String studentData) {
        return webClient.post()
                .uri("/prediction")
                .bodyValue(studentData)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("Unable to get AI recommendation at this time");
    }

    public String getSimpleRecommendation(String subject, int studyHours) {
        // Enhanced AI-like logic for demonstration
        if (studyHours < 2) {
            return "Recommended: Increase study time for " + subject + " to at least 2 hours daily";
        } else if (studyHours > 6) {
            return "Recommended: Take breaks! Consider 4-6 hours for " + subject + " with rest periods";
        } else {
            return "Good study schedule for " + subject + "! Consider adding practice sessions";
        }
    }

    public Map<String, Object> getDetailedStudyRecommendation(String subject, int currentStudyHours, String difficulty, String goalType) {
        Map<String, Object> recommendation = new HashMap<>();

        // Calculate optimal study duration
        int optimalHours = calculateOptimalDuration(subject, difficulty, goalType);

        // Generate study plan
        List<String> studyPlan = generateStudyPlan(subject, optimalHours, difficulty);

        // Get study techniques
        List<String> techniques = getStudyTechniques(subject);

        // Calculate efficiency score
        int efficiencyScore = calculateEfficiencyScore(currentStudyHours, optimalHours);

        // Generate motivational message
        String motivation = generateMotivationalMessage(subject, efficiencyScore);

        recommendation.put("optimalHours", optimalHours);
        recommendation.put("studyPlan", studyPlan);
        recommendation.put("techniques", techniques);
        recommendation.put("efficiencyScore", efficiencyScore);
        recommendation.put("motivation", motivation);
        recommendation.put("recommendations", generateSpecificRecommendations(subject, currentStudyHours, optimalHours));

        return recommendation;
    }

    public List<String> getOptimalStudyTimes() {
        return Arrays.asList(
            "6:00 AM - 8:00 AM (Peak concentration)",
            "9:00 AM - 11:00 AM (High cognitive performance)",
            "2:00 PM - 4:00 PM (Post-lunch focus)",
            "7:00 PM - 9:00 PM (Evening review)"
        );
    }

    public Map<String, String> getSubjectSpecificAdvice(String subject) {
        Map<String, String> advice = new HashMap<>();

        switch (subject.toLowerCase()) {
            case "mathematics":
            case "math":
                advice.put("technique", "Practice problems daily, start with easier concepts");
                advice.put("duration", "90 minutes with 15-minute breaks");
                advice.put("best_time", "Morning (9-11 AM) for maximum concentration");
                advice.put("tip", "Review previous day's work for 10 minutes before starting new topics");
                break;

            case "physics":
                advice.put("technique", "Combine theory with practical examples and experiments");
                advice.put("duration", "90 minutes with visual aids and diagrams");
                advice.put("best_time", "Morning (8-10 AM) for complex problem solving");
                advice.put("tip", "Create concept maps to connect different physics principles");
                break;

            case "chemistry":
                advice.put("technique", "Use molecular models and chemical equation practice");
                advice.put("duration", "75 minutes with hands-on activities");
                advice.put("best_time", "Mid-morning (10 AM-12 PM) for analytical thinking");
                advice.put("tip", "Practice naming compounds and balancing equations daily");
                break;

            case "biology":
                advice.put("technique", "Create diagrams, use flashcards for terminology");
                advice.put("duration", "60 minutes with visual learning aids");
                advice.put("best_time", "Afternoon (2-4 PM) for memorization tasks");
                advice.put("tip", "Relate biological processes to real-life examples");
                break;

            case "history":
                advice.put("technique", "Create timelines, use storytelling methods");
                advice.put("duration", "60 minutes with active reading");
                advice.put("best_time", "Evening (6-8 PM) for reading and reflection");
                advice.put("tip", "Connect historical events to current world situations");
                break;

            case "literature":
            case "english":
                advice.put("technique", "Active reading with note-taking and analysis");
                advice.put("duration", "75 minutes with discussion or writing");
                advice.put("best_time", "Afternoon (3-5 PM) for creative thinking");
                advice.put("tip", "Keep a reading journal with quotes and personal reflections");
                break;

            case "programming":
            case "computer science":
                advice.put("technique", "Code along with tutorials, build small projects");
                advice.put("duration", "120 minutes with regular code execution");
                advice.put("best_time", "Morning (9 AM-12 PM) for logical thinking");
                advice.put("tip", "Practice coding problems daily and review others' code");
                break;

            default:
                advice.put("technique", "Active learning with regular practice");
                advice.put("duration", "75 minutes with appropriate breaks");
                advice.put("best_time", "Choose your most alert time of day");
                advice.put("tip", "Set specific goals for each study session");
        }

        return advice;
    }

    private int calculateOptimalDuration(String subject, String difficulty, String goalType) {
        int baseDuration = getBaseDuration(subject);

        // Adjust for difficulty
        switch (difficulty.toLowerCase()) {
            case "easy": baseDuration -= 15; break;
            case "hard": baseDuration += 30; break;
            default: break; // medium - no change
        }

        // Adjust for goal type
        switch (goalType.toLowerCase()) {
            case "exam": baseDuration += 20; break;
            case "assignment": baseDuration += 10; break;
            case "review": baseDuration -= 10; break;
            default: break; // regular study
        }

        return Math.max(30, Math.min(180, baseDuration)); // Between 30 and 180 minutes
    }

    private int getBaseDuration(String subject) {
        switch (subject.toLowerCase()) {
            case "mathematics":
            case "physics":
            case "chemistry":
                return 90; // Technical subjects need more time
            case "programming":
            case "computer science":
                return 120; // Practical subjects
            case "history":
            case "literature":
            case "biology":
                return 60; // Reading-intensive subjects
            default:
                return 75; // Default
        }
    }

    private List<String> generateStudyPlan(String subject, int duration, String difficulty) {
        List<String> plan = new ArrayList<>();

        plan.add("Warm-up: Review previous session (5-10 minutes)");

        if (duration >= 90) {
            plan.add("Main session 1: Core concepts (" + (duration * 0.4) + " minutes)");
            plan.add("Break: 10-15 minutes");
            plan.add("Main session 2: Practice/Application (" + (duration * 0.35) + " minutes)");
            plan.add("Cool-down: Summary and notes (" + (duration * 0.1) + " minutes)");
        } else {
            plan.add("Main session: Core learning (" + (duration * 0.7) + " minutes)");
            plan.add("Practice: Apply concepts (" + (duration * 0.2) + " minutes)");
            plan.add("Review: Quick summary (" + (duration * 0.1) + " minutes)");
        }

        return plan;
    }

    private List<String> getStudyTechniques(String subject) {
        Map<String, List<String>> techniqueMap = new HashMap<>();

        techniqueMap.put("mathematics", Arrays.asList(
            "Spaced repetition for formulas",
            "Practice problems with increasing difficulty",
            "Create formula sheets and reference cards",
            "Explain solutions out loud"
        ));

        techniqueMap.put("programming", Arrays.asList(
            "Code-along tutorials",
            "Build mini-projects",
            "Debug others' code",
            "Explain code logic to peers"
        ));

        techniqueMap.put("history", Arrays.asList(
            "Create timelines and concept maps",
            "Use mnemonics for dates and events",
            "Storytelling method for events",
            "Connect past to present situations"
        ));

        // Default techniques for any subject
        return techniqueMap.getOrDefault(subject.toLowerCase(), Arrays.asList(
            "Active reading with note-taking",
            "Create mind maps and diagrams",
            "Use flashcards for key concepts",
            "Teach concepts to someone else"
        ));
    }

    private int calculateEfficiencyScore(int currentHours, int optimalHours) {
        if (currentHours == 0) return 0;

        double efficiency = (double) Math.min(currentHours, optimalHours) / optimalHours * 100;

        if (currentHours > optimalHours * 1.5) {
            efficiency -= 20; // Penalty for overstudy
        }

        return Math.max(0, Math.min(100, (int) efficiency));
    }

    private String generateMotivationalMessage(String subject, int score) {
        if (score >= 80) {
            return "Excellent! Your " + subject + " study routine is highly efficient. Keep up the great work!";
        } else if (score >= 60) {
            return "Good progress in " + subject + "! A few adjustments could make your study time even more effective.";
        } else if (score >= 40) {
            return "You're on the right track with " + subject + ". Let's optimize your study approach for better results.";
        } else {
            return "Great potential in " + subject + "! Let's create a more structured study plan to boost your learning.";
        }
    }

    private List<String> generateSpecificRecommendations(String subject, int current, int optimal) {
        List<String> recommendations = new ArrayList<>();

        if (current < optimal * 0.5) {
            recommendations.add("Increase study time gradually to reach optimal duration");
            recommendations.add("Set up a consistent daily study schedule");
        } else if (current > optimal * 1.5) {
            recommendations.add("Consider taking more breaks to avoid burnout");
            recommendations.add("Focus on quality over quantity in study sessions");
        }

        recommendations.add("Use active learning techniques for better retention");
        recommendations.add("Review previous material before starting new topics");
        recommendations.add("Set specific, measurable goals for each session");

        return recommendations;
    }
}
