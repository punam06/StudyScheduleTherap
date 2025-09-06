package org.example.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    // NEW: Predictive Scheduling Analytics - Version 2 Feature
    public Map<String, Object> getPredictiveSchedulingAnalytics(List<LocalDateTime> proposedTimes, String subject) {
        Map<String, Object> analytics = new HashMap<>();

        // Predict conflict probability for each time slot
        Map<LocalDateTime, Double> conflictProbability = new HashMap<>();
        for (LocalDateTime time : proposedTimes) {
            conflictProbability.put(time, predictConflictProbability(time, subject));
        }

        // Identify high-productivity time slots
        List<LocalDateTime> highProductivitySlots = identifyHighProductivitySlots(proposedTimes, subject);

        // Generate spaced repetition recommendations
        List<Map<String, Object>> spacedRepetition = generateSpacedRepetitionSchedule(subject);

        analytics.put("conflictProbabilities", conflictProbability);
        analytics.put("highProductivitySlots", highProductivitySlots);
        analytics.put("spacedRepetitionSchedule", spacedRepetition);
        analytics.put("recommendedDuration", getOptimalDurationForSubject(subject));
        analytics.put("peakLearningWindows", getPeakLearningWindows());

        return analytics;
    }

    // NEW: Natural Language Scheduling - Version 2 Feature
    public Map<String, Object> parseNaturalLanguageScheduling(String naturalLanguageInput) {
        Map<String, Object> parsedSchedule = new HashMap<>();

        // Simple NLP parsing (in production, would use advanced NLP libraries)
        String input = naturalLanguageInput.toLowerCase();

        // Extract subject
        String subject = extractSubjectFromText(input);

        // Extract time preferences
        List<String> timePreferences = extractTimePreferences(input);

        // Extract participants
        List<String> participants = extractParticipants(input);

        // Extract urgency/priority
        String priority = extractPriority(input);

        // Generate structured scheduling data
        parsedSchedule.put("subject", subject);
        parsedSchedule.put("timePreferences", timePreferences);
        parsedSchedule.put("participants", participants);
        parsedSchedule.put("priority", priority);
        parsedSchedule.put("suggestedDuration", getRecommendedDurationForSubject(subject));
        parsedSchedule.put("confidence", calculateParsingConfidence(input));

        return parsedSchedule;
    }

    // NEW: Group Dynamics Insights - Version 2 Feature
    public Map<String, Object> analyzeGroupDynamics(Long groupId, List<String> chatMessages) {
        Map<String, Object> insights = new HashMap<>();

        // Sentiment analysis of group communications
        Map<String, Double> sentimentAnalysis = analyzeSentiment(chatMessages);

        // Participation balance analysis
        Map<String, Integer> participationBalance = analyzeParticipationBalance(chatMessages);

        // Engagement score calculation
        double engagementScore = calculateEngagementScore(chatMessages);

        // Conflict detection in communications
        List<String> potentialConflicts = detectCommunicationConflicts(chatMessages);

        // Collaboration effectiveness metrics
        Map<String, Object> collaborationMetrics = calculateCollaborationMetrics(groupId);

        insights.put("sentimentAnalysis", sentimentAnalysis);
        insights.put("participationBalance", participationBalance);
        insights.put("engagementScore", engagementScore);
        insights.put("potentialConflicts", potentialConflicts);
        insights.put("collaborationMetrics", collaborationMetrics);
        insights.put("recommendations", generateGroupDynamicsRecommendations(insights));

        return insights;
    }

    // NEW: Collaborative Content AI - Version 2 Feature
    public Map<String, Object> generateCollaborativeContent(String subject, String topic, String syllabusContent) {
        Map<String, Object> content = new HashMap<>();

        // Generate quiz questions from syllabus
        List<Map<String, Object>> quizQuestions = generateQuizFromSyllabus(subject, topic, syllabusContent);

        // Suggest shared resources
        List<Map<String, String>> sharedResources = suggestSharedResources(subject, topic);

        // Create study roadmap
        List<Map<String, Object>> studyRoadmap = createStudyRoadmap(subject, topic);

        // Generate discussion prompts
        List<String> discussionPrompts = generateDiscussionPrompts(subject, topic);

        content.put("quizQuestions", quizQuestions);
        content.put("sharedResources", sharedResources);
        content.put("studyRoadmap", studyRoadmap);
        content.put("discussionPrompts", discussionPrompts);
        content.put("difficultyLevel", assessTopicDifficulty(subject, topic));

        return content;
    }

    // NEW: Intelligent Group Matching - Enhanced Version 2
    public Map<String, Object> getEnhancedGroupMatching(String subject, Map<String, Object> studentProfile) {
        Map<String, Object> matching = new HashMap<>();

        // VARK learning style assessment
        String learningStyle = (String) studentProfile.get("learningStyle");
        Double gpa = (Double) studentProfile.get("gpa");

        // Personality traits analysis (Big Five model simulation)
        Map<String, Double> personalityTraits = analyzePersonalityTraits(studentProfile);

        // Enhanced compatibility calculation
        List<Map<String, Object>> compatibleGroups = findCompatibleGroupsEnhanced(subject, learningStyle, gpa, personalityTraits);

        // Predict group success probability
        for (Map<String, Object> group : compatibleGroups) {
            double successProbability = predictGroupSuccessProbability(group, studentProfile);
            group.put("successProbability", successProbability);
        }

        matching.put("compatibleGroups", compatibleGroups);
        matching.put("personalityAnalysis", personalityTraits);
        matching.put("learningStyleRecommendations", getLearningStyleRecommendations(learningStyle));
        matching.put("optimalGroupSize", calculateOptimalGroupSize(subject, learningStyle));

        return matching;
    }

    // Supporting methods for Version 2 features

    private double predictConflictProbability(LocalDateTime time, String subject) {
        // AI model to predict conflicts based on historical data
        int hour = time.getHour();
        int dayOfWeek = time.getDayOfWeek().getValue();

        double baseProbability = 0.15; // 15% base conflict rate

        // Adjust based on time of day
        if (hour >= 12 && hour <= 14) baseProbability += 0.25; // Lunch time conflicts
        if (hour >= 17 && hour <= 19) baseProbability += 0.20; // End of day conflicts

        // Adjust based on day of week
        if (dayOfWeek == 1 || dayOfWeek == 5) baseProbability += 0.10; // Monday/Friday higher conflicts

        // Subject-specific adjustments
        if ("Mathematics".equals(subject) || "Physics".equals(subject)) {
            baseProbability -= 0.05; // Technical subjects have more dedicated students
        }

        return Math.min(0.8, baseProbability); // Cap at 80%
    }

    private List<LocalDateTime> identifyHighProductivitySlots(List<LocalDateTime> times, String subject) {
        return times.stream()
                .filter(time -> {
                    int hour = time.getHour();
                    // Peak productivity hours: 9-11 AM, 2-4 PM, 7-9 PM
                    return (hour >= 9 && hour <= 11) || (hour >= 14 && hour <= 16) || (hour >= 19 && hour <= 21);
                })
                .sorted((t1, t2) -> {
                    // Sort by productivity score
                    int score1 = getProductivityScore(t1);
                    int score2 = getProductivityScore(t2);
                    return Integer.compare(score2, score1);
                })
                .collect(Collectors.toList());
    }

    private int getProductivityScore(LocalDateTime time) {
        int hour = time.getHour();
        if (hour >= 9 && hour <= 11) return 100; // Peak morning
        if (hour >= 14 && hour <= 16) return 90; // Post-lunch focus
        if (hour >= 19 && hour <= 21) return 80; // Evening review
        if (hour >= 7 && hour <= 9) return 70;   // Early morning
        return 50; // Other times
    }

    private List<Map<String, Object>> generateSpacedRepetitionSchedule(String subject) {
        List<Map<String, Object>> schedule = new ArrayList<>();

        // Spaced repetition intervals: 1 day, 3 days, 1 week, 2 weeks, 1 month
        int[] intervals = {1, 3, 7, 14, 30};

        for (int interval : intervals) {
            Map<String, Object> session = new HashMap<>();
            session.put("daysFromNow", interval);
            session.put("reviewType", getReviewType(interval));
            session.put("duration", calculateReviewDuration(interval, subject));
            session.put("focus", getReviewFocus(interval));
            schedule.add(session);
        }

        return schedule;
    }

    private String getReviewType(int interval) {
        if (interval <= 3) return "Active Recall";
        if (interval <= 7) return "Practice Problems";
        if (interval <= 14) return "Concept Review";
        return "Integration & Application";
    }

    private int calculateReviewDuration(int interval, String subject) {
        int baseDuration = getBaseDuration(subject);
        // Decrease duration for spaced repetition (should be shorter than initial learning)
        return Math.max(30, baseDuration - (interval * 5));
    }

    private String getReviewFocus(int interval) {
        if (interval <= 3) return "Key concepts and definitions";
        if (interval <= 7) return "Problem-solving techniques";
        if (interval <= 14) return "Connections between topics";
        return "Real-world applications";
    }

    private String extractSubjectFromText(String input) {
        // Simple keyword matching (in production, would use NER)
        Map<String, String> subjectKeywords = Map.of(
            "math", "Mathematics",
            "calculus", "Mathematics",
            "physics", "Physics",
            "chemistry", "Chemistry",
            "bio", "Biology",
            "history", "History",
            "cs", "Computer Science",
            "programming", "Computer Science"
        );

        for (Map.Entry<String, String> entry : subjectKeywords.entrySet()) {
            if (input.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "General";
    }

    private List<String> extractTimePreferences(String input) {
        List<String> preferences = new ArrayList<>();

        if (input.contains("morning")) preferences.add("09:00-12:00");
        if (input.contains("afternoon")) preferences.add("13:00-17:00");
        if (input.contains("evening")) preferences.add("18:00-21:00");
        if (input.contains("weekend")) preferences.add("WEEKEND");
        if (input.contains("weekday")) preferences.add("WEEKDAY");

        return preferences;
    }

    private List<String> extractParticipants(String input) {
        List<String> participants = new ArrayList<>();

        // Simple name extraction (would use NER in production)
        String[] words = input.split(" ");
        for (String word : words) {
            if (word.length() > 2 && Character.isUpperCase(word.charAt(0))) {
                participants.add(word);
            }
        }

        return participants;
    }

    private String extractPriority(String input) {
        if (input.contains("urgent") || input.contains("asap")) return "HIGH";
        if (input.contains("soon") || input.contains("important")) return "MEDIUM";
        return "NORMAL";
    }

    private double calculateParsingConfidence(String input) {
        // Calculate confidence based on keyword matches and structure
        double confidence = 0.5; // Base confidence

        if (input.contains("study") || input.contains("session")) confidence += 0.2;
        if (input.matches(".*\\d{1,2}:\\d{2}.*")) confidence += 0.2; // Contains time
        if (input.contains("with") || input.contains("and")) confidence += 0.1; // Contains participants

        return Math.min(1.0, confidence);
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

    // Additional supporting methods for Version 2 AI features

    private Map<String, Double> analyzeSentiment(List<String> chatMessages) {
        Map<String, Double> sentiment = new HashMap<>();

        int positiveCount = 0, negativeCount = 0, neutralCount = 0;

        for (String message : chatMessages) {
            String msg = message.toLowerCase();
            if (containsPositiveWords(msg)) {
                positiveCount++;
            } else if (containsNegativeWords(msg)) {
                negativeCount++;
            } else {
                neutralCount++;
            }
        }

        int total = chatMessages.size();
        if (total > 0) {
            sentiment.put("positive", (double) positiveCount / total);
            sentiment.put("negative", (double) negativeCount / total);
            sentiment.put("neutral", (double) neutralCount / total);
        }

        return sentiment;
    }

    private boolean containsPositiveWords(String message) {
        String[] positiveWords = {"great", "awesome", "good", "excellent", "helpful", "understand", "clear", "thanks"};
        return Arrays.stream(positiveWords).anyMatch(message::contains);
    }

    private boolean containsNegativeWords(String message) {
        String[] negativeWords = {"confused", "difficult", "hard", "don't understand", "frustrated", "unclear", "problem"};
        return Arrays.stream(negativeWords).anyMatch(message::contains);
    }

    private Map<String, Integer> analyzeParticipationBalance(List<String> chatMessages) {
        Map<String, Integer> participation = new HashMap<>();

        // Simulate participant extraction (in production, would use proper message attribution)
        for (String message : chatMessages) {
            String[] words = message.split(" ");
            if (words.length > 0) {
                String participant = "User" + (message.hashCode() % 5 + 1); // Simulate 5 users
                participation.put(participant, participation.getOrDefault(participant, 0) + 1);
            }
        }

        return participation;
    }

    private double calculateEngagementScore(List<String> chatMessages) {
        if (chatMessages.isEmpty()) return 0.0;

        // Calculate engagement based on message frequency, length, and content
        double avgMessageLength = chatMessages.stream()
                .mapToInt(String::length)
                .average()
                .orElse(0.0);

        long questionCount = chatMessages.stream()
                .filter(msg -> msg.contains("?"))
                .count();

        double questionRatio = (double) questionCount / chatMessages.size();

        // Normalize to 0-100 scale
        double lengthScore = Math.min(100, avgMessageLength / 2.0);
        double questionScore = questionRatio * 50;
        double frequencyScore = Math.min(50, chatMessages.size() * 2);

        return (lengthScore + questionScore + frequencyScore) / 3.0;
    }

    private List<String> detectCommunicationConflicts(List<String> chatMessages) {
        List<String> conflicts = new ArrayList<>();

        for (String message : chatMessages) {
            String msg = message.toLowerCase();
            if (msg.contains("disagree") || msg.contains("wrong") || msg.contains("that's not right")) {
                conflicts.add("Potential disagreement detected in discussion");
            }
            if (msg.contains("confused") && msg.contains("explanation")) {
                conflicts.add("Confusion about explanations - may need clarification");
            }
        }

        return conflicts;
    }

    private Map<String, Object> calculateCollaborationMetrics(Long groupId) {
        Map<String, Object> metrics = new HashMap<>();

        // Simulate collaboration metrics (in production, would query actual data)
        metrics.put("averageResponseTime", "12 minutes");
        metrics.put("knowledgeSharing", 85); // Percentage
        metrics.put("problemSolvingEfficiency", 78);
        metrics.put("groupCohesion", 82);
        metrics.put("learningProgress", 76);

        return metrics;
    }

    private List<String> generateGroupDynamicsRecommendations(Map<String, Object> insights) {
        List<String> recommendations = new ArrayList<>();

        Double engagementScore = (Double) insights.get("engagementScore");
        if (engagementScore < 60) {
            recommendations.add("Low engagement detected - consider more interactive activities");
            recommendations.add("Implement round-robin discussion format");
        }

        @SuppressWarnings("unchecked")
        List<String> conflicts = (List<String>) insights.get("potentialConflicts");
        if (!conflicts.isEmpty()) {
            recommendations.add("Address communication issues through structured discussions");
            recommendations.add("Consider appointing a session moderator");
        }

        recommendations.add("Use collaborative tools for better participation tracking");
        recommendations.add("Schedule regular check-ins to maintain group dynamics");

        return recommendations;
    }

    private List<Map<String, Object>> generateQuizFromSyllabus(String subject, String topic, String syllabusContent) {
        List<Map<String, Object>> quiz = new ArrayList<>();

        // Generate sample quiz questions based on subject and topic
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> question = new HashMap<>();
            question.put("id", i);
            question.put("question", generateQuestionForTopic(subject, topic, i));
            question.put("options", generateOptions(subject, topic));
            question.put("correctAnswer", 0); // First option is correct
            question.put("difficulty", i <= 2 ? "Easy" : i <= 4 ? "Medium" : "Hard");
            quiz.add(question);
        }

        return quiz;
    }

    private String generateQuestionForTopic(String subject, String topic, int questionNumber) {
        Map<String, String[]> questionTemplates = Map.of(
            "Mathematics", new String[]{
                "What is the derivative of x²?",
                "Solve for x: 2x + 5 = 15",
                "What is the integral of 2x dx?",
                "Find the limit of (x² - 1)/(x - 1) as x approaches 1",
                "What is the slope of the line y = 3x + 2?"
            },
            "Physics", new String[]{
                "What is Newton's second law of motion?",
                "Calculate the force needed to accelerate a 5kg object at 2m/s²",
                "What is the relationship between energy and mass?",
                "Define momentum and its conservation",
                "Explain the concept of wave-particle duality"
            }
        );

        String[] questions = questionTemplates.getOrDefault(subject, new String[]{
            "What is the main concept in " + topic + "?",
            "Explain the key principle of " + topic,
            "How does " + topic + " relate to real-world applications?",
            "What are the challenges in understanding " + topic + "?",
            "Summarize the importance of " + topic
        });

        return questions[Math.min(questionNumber - 1, questions.length - 1)];
    }

    private List<String> generateOptions(String subject, String topic) {
        // Generate plausible options based on subject
        return Arrays.asList("Option A (Correct)", "Option B", "Option C", "Option D");
    }

    private List<Map<String, String>> suggestSharedResources(String subject, String topic) {
        List<Map<String, String>> resources = new ArrayList<>();

        // Generate subject-specific resources
        Map<String, String> resource1 = Map.of(
            "title", "Khan Academy - " + subject,
            "type", "Video Tutorial",
            "url", "https://khanacademy.org/" + subject.toLowerCase(),
            "relevance", "High"
        );

        Map<String, String> resource2 = Map.of(
            "title", "MIT OpenCourseWare - " + topic,
            "type", "Course Material",
            "url", "https://ocw.mit.edu",
            "relevance", "Medium"
        );

        Map<String, String> resource3 = Map.of(
            "title", "Practice Problems - " + topic,
            "type", "Exercise Set",
            "url", "https://example.com/practice",
            "relevance", "High"
        );

        resources.add(resource1);
        resources.add(resource2);
        resources.add(resource3);

        return resources;
    }

    private List<Map<String, Object>> createStudyRoadmap(String subject, String topic) {
        List<Map<String, Object>> roadmap = new ArrayList<>();

        String[] phases = {"Foundation", "Core Concepts", "Advanced Topics", "Application", "Mastery"};
        int[] durations = {2, 3, 4, 3, 2}; // weeks

        for (int i = 0; i < phases.length; i++) {
            Map<String, Object> phase = new HashMap<>();
            phase.put("phase", phases[i]);
            phase.put("duration", durations[i] + " weeks");
            phase.put("focus", getPhaseFocus(phases[i], subject));
            phase.put("milestones", getPhaseMilestones(phases[i]));
            roadmap.add(phase);
        }

        return roadmap;
    }

    private String getPhaseFoundation(String phase, String subject) {
        return switch (phase) {
            case "Foundation" -> "Build basic understanding of " + subject + " fundamentals";
            case "Core Concepts" -> "Master essential " + subject + " principles and theories";
            case "Advanced Topics" -> "Explore complex " + subject + " applications";
            case "Application" -> "Apply knowledge to real-world problems";
            case "Mastery" -> "Achieve deep understanding and teaching ability";
            default -> "Study " + subject + " systematically";
        };
    }

    private List<String> getPhaseMilestones(String phase) {
        return switch (phase) {
            case "Foundation" -> Arrays.asList("Complete basic readings", "Pass foundation quiz");
            case "Core Concepts" -> Arrays.asList("Master key formulas", "Solve practice problems");
            case "Advanced Topics" -> Arrays.asList("Tackle complex scenarios", "Lead group discussions");
            case "Application" -> Arrays.asList("Complete project work", "Present findings");
            case "Mastery" -> Arrays.asList("Teach others", "Create study materials");
            default -> Arrays.asList("Complete phase objectives");
        };
    }

    private List<String> generateDiscussionPrompts(String subject, String topic) {
        return Arrays.asList(
            "How does " + topic + " relate to real-world applications?",
            "What are the most challenging aspects of " + topic + "?",
            "Can you explain " + topic + " in simple terms?",
            "What connections do you see between " + topic + " and other subjects?",
            "How has your understanding of " + topic + " evolved?"
        );
    }

    private String assessTopicDifficulty(String subject, String topic) {
        // Simple heuristic for topic difficulty
        if (topic.toLowerCase().contains("advanced") || topic.toLowerCase().contains("complex")) {
            return "Hard";
        } else if (topic.toLowerCase().contains("intro") || topic.toLowerCase().contains("basic")) {
            return "Easy";
        }
        return "Medium";
    }

    private Map<String, Double> analyzePersonalityTraits(Map<String, Object> studentProfile) {
        Map<String, Double> traits = new HashMap<>();

        // Simulate Big Five personality assessment
        traits.put("openness", 0.7);      // Openness to experience
        traits.put("conscientiousness", 0.8); // Conscientiousness
        traits.put("extraversion", 0.6);   // Extraversion
        traits.put("agreeableness", 0.75); // Agreeableness
        traits.put("neuroticism", 0.3);    // Neuroticism (lower is better)

        return traits;
    }

    private List<Map<String, Object>> findCompatibleGroupsEnhanced(String subject, String learningStyle, Double gpa, Map<String, Double> personalityTraits) {
        // Simulate enhanced group matching with personality and GPA consideration
        List<Map<String, Object>> groups = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            Map<String, Object> group = new HashMap<>();
            group.put("id", i);
            group.put("name", subject + " Study Group " + i);
            group.put("compatibilityScore", 85 - (i * 5)); // Decreasing compatibility
            group.put("personalityMatch", 0.8 - (i * 0.1));
            group.put("learningStyleCompatibility", 0.9 - (i * 0.05));
            group.put("gpaBalance", 0.75 + (i * 0.05));
            groups.add(group);
        }

        return groups;
    }

    private double predictGroupSuccessProbability(Map<String, Object> group, Map<String, Object> studentProfile) {
        // ML model simulation for predicting group success
        double baseSuccess = 0.7;

        Double compatibilityScore = (Double) group.get("compatibilityScore");
        if (compatibilityScore != null) {
            baseSuccess += (compatibilityScore / 100.0) * 0.2;
        }

        return Math.min(0.95, baseSuccess);
    }

    private List<String> getLearningStyleRecommendations(String learningStyle) {
        Map<String, List<String>> recommendations = Map.of(
            "VISUAL", Arrays.asList("Use diagrams and charts", "Create mind maps", "Watch educational videos"),
            "AUDITORY", Arrays.asList("Participate in discussions", "Use audio recordings", "Explain concepts aloud"),
            "KINESTHETIC", Arrays.asList("Use hands-on activities", "Take frequent breaks", "Use physical models"),
            "READING_WRITING", Arrays.asList("Take detailed notes", "Create summaries", "Use written exercises")
        );

        return recommendations.getOrDefault(learningStyle, Arrays.asList("Use mixed learning approaches"));
    }

    private int calculateOptimalGroupSize(String subject, String learningStyle) {
        // AI recommendation for optimal group size
        if ("KINESTHETIC".equals(learningStyle)) return 3; // Smaller groups for hands-on
        if ("Computer Science".equals(subject)) return 4; // Programming pairs well in small groups
        return 5; // Default optimal size
    }

    private int getOptimalDurationForSubject(String subject) {
        return getBaseDuration(subject);
    }

    private int getRecommendedDurationForSubject(String subject) {
        return getBaseDuration(subject);
    }

    private List<String> getPeakLearningWindows() {
        return Arrays.asList(
            "9:00-11:00 AM (Peak concentration)",
            "2:00-4:00 PM (Post-lunch focus)",
            "7:00-9:00 PM (Evening review)"
        );
    }

    private String getPhaseFlocus(String phase, String subject) {
        return getPhaseFoundation(phase, subject);
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
}
