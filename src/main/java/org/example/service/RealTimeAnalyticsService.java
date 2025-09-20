package org.example.service;

import org.example.entity.User;
import org.example.entity.UserAnalytics;
import org.example.entity.StudySchedule;
import org.example.repository.UserAnalyticsRepository;
import org.example.repository.StudyScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class RealTimeAnalyticsService {

    @Autowired
    private UserAnalyticsRepository analyticsRepository;

    @Autowired
    private StudyScheduleRepository scheduleRepository;

    @Autowired
    private AIService aiService;

    public UserAnalytics calculateRealTimeMetrics(Long userId) {
        UserAnalytics analytics = analyticsRepository.findByUserId(userId)
                .orElse(new UserAnalytics());

        // Set user if it's a new analytics record
        if (analytics.getId() == null) {
            User user = new User();
            user.setId(userId);
            analytics.setUser(user);
        }

        // Calculate metrics based on real user data
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = now.minusWeeks(1);
        LocalDateTime monthStart = now.minusMonths(1);

        // Get user's study schedules
        List<StudySchedule> allSchedules = scheduleRepository.findByUserId(userId);
        List<StudySchedule> weekSchedules = scheduleRepository.findByUserIdAndScheduledTimeAfter(userId, weekStart);
        List<StudySchedule> monthSchedules = scheduleRepository.findByUserIdAndScheduledTimeAfter(userId, monthStart);

        // Calculate Study Efficiency
        Double efficiency = calculateStudyEfficiency(allSchedules);
        analytics.setStudyEfficiency(efficiency);

        // Calculate Goals Completed (this month)
        Integer goalsCompleted = calculateGoalsCompleted(monthSchedules);
        analytics.setGoalsCompleted(goalsCompleted);

        // Calculate Study Hours (this week)
        Double studyHoursWeek = calculateStudyHours(weekSchedules);
        analytics.setStudyHoursWeek(studyHoursWeek);

        // Calculate Day Streak
        Integer dayStreak = calculateDayStreak(userId);
        analytics.setDayStreak(dayStreak);

        // Calculate additional metrics
        analytics.setTotalSessions(allSchedules.size());
        analytics.setCompletedSessions((int) allSchedules.stream().filter(s -> s.getCompleted()).count());
        analytics.setAverageSessionDuration(calculateAverageSessionDuration(allSchedules));
        analytics.setPreferredStudyTime(findPreferredStudyTime(allSchedules));
        analytics.setFocusScore(calculateFocusScore(allSchedules));
        analytics.setLearningVelocity(calculateLearningVelocity(allSchedules));
        analytics.setConsistencyScore(calculateConsistencyScore(allSchedules));

        analytics.setLastActivity(now);
        analytics.setLastUpdated(now);

        return analyticsRepository.save(analytics);
    }

    private Double calculateStudyEfficiency(List<StudySchedule> schedules) {
        if (schedules.isEmpty()) return 0.0;

        long completedCount = schedules.stream()
                .filter(s -> s.getCompleted())
                .count();

        double baseEfficiency = (double) completedCount / schedules.size() * 100;

        // Bonus for consistency
        double consistencyBonus = calculateConsistencyScore(schedules) * 0.2;

        // Penalty for overdue tasks
        long overdueCount = schedules.stream()
                .filter(s -> !s.getCompleted() && s.getScheduledTime().isBefore(LocalDateTime.now()))
                .count();

        double overduePenalty = (double) overdueCount / schedules.size() * 10;

        return Math.min(100.0, Math.max(0.0, baseEfficiency + consistencyBonus - overduePenalty));
    }

    private Integer calculateGoalsCompleted(List<StudySchedule> monthSchedules) {
        return (int) monthSchedules.stream()
                .filter(s -> s.getCompleted())
                .count();
    }

    private Double calculateStudyHours(List<StudySchedule> weekSchedules) {
        return weekSchedules.stream()
                .filter(s -> s.getCompleted())
                .mapToDouble(s -> s.getDuration() / 60.0) // Convert minutes to hours
                .sum();
    }

    private Integer calculateDayStreak(Long userId) {
        LocalDateTime current = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        int streak = 0;

        while (true) {
            LocalDateTime dayStart = current.minusDays(streak);
            LocalDateTime dayEnd = dayStart.plusDays(1);

            List<StudySchedule> daySchedules = scheduleRepository
                    .findByUserIdAndScheduledTimeBetween(userId, dayStart, dayEnd);

            boolean hasCompletedSession = daySchedules.stream()
                    .anyMatch(s -> s.getCompleted());

            if (hasCompletedSession) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }

    private Double calculateAverageSessionDuration(List<StudySchedule> schedules) {
        if (schedules.isEmpty()) return 0.0;

        return schedules.stream()
                .filter(s -> s.getCompleted())
                .mapToDouble(s -> s.getDuration())
                .average()
                .orElse(0.0);
    }

    private String findPreferredStudyTime(List<StudySchedule> schedules) {
        if (schedules.isEmpty()) return "Morning";

        Map<String, Long> timeSlotCounts = schedules.stream()
                .filter(s -> s.getCompleted())
                .collect(Collectors.groupingBy(
                    this::categorizeTimeSlot,
                    Collectors.counting()
                ));

        return timeSlotCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Morning");
    }

    private String categorizeTimeSlot(StudySchedule schedule) {
        int hour = schedule.getScheduledTime().getHour();
        if (hour < 6) return "Late Night";
        if (hour < 12) return "Morning";
        if (hour < 17) return "Afternoon";
        if (hour < 21) return "Evening";
        return "Night";
    }

    private Double calculateFocusScore(List<StudySchedule> schedules) {
        if (schedules.isEmpty()) return 0.0;

        // Calculate based on completion rate and session duration consistency
        double completionRate = schedules.stream()
                .filter(s -> s.getCompleted())
                .count() / (double) schedules.size();

        double avgDuration = calculateAverageSessionDuration(schedules);
        double durationConsistency = schedules.stream()
                .filter(s -> s.getCompleted())
                .mapToDouble(s -> Math.abs(s.getDuration() - avgDuration))
                .average()
                .orElse(0.0);

        // Lower deviation = higher focus score
        double focusFromConsistency = Math.max(0, 1 - (durationConsistency / avgDuration));

        return (completionRate * 0.7 + focusFromConsistency * 0.3) * 100;
    }

    private Double calculateLearningVelocity(List<StudySchedule> schedules) {
        if (schedules.size() < 2) return 0.0;

        // Calculate improvement over time
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        List<StudySchedule> recentSchedules = schedules.stream()
                .filter(s -> s.getScheduledTime().isAfter(thirtyDaysAgo))
                .collect(Collectors.toList());

        List<StudySchedule> olderSchedules = schedules.stream()
                .filter(s -> s.getScheduledTime().isBefore(thirtyDaysAgo))
                .collect(Collectors.toList());

        if (olderSchedules.isEmpty()) return 50.0; // Default for new users

        double recentCompletionRate = recentSchedules.stream()
                .filter(s -> s.getCompleted())
                .count() / (double) Math.max(1, recentSchedules.size());

        double oldCompletionRate = olderSchedules.stream()
                .filter(s -> s.getCompleted())
                .count() / (double) olderSchedules.size();

        return Math.max(0, Math.min(100, 50 + (recentCompletionRate - oldCompletionRate) * 100));
    }

    private Double calculateConsistencyScore(List<StudySchedule> schedules) {
        if (schedules.size() < 7) return 50.0; // Not enough data

        // Check for regular study patterns
        Map<Integer, Long> dayOfWeekCounts = schedules.stream()
                .filter(s -> s.getCompleted())
                .collect(Collectors.groupingBy(
                    s -> s.getScheduledTime().getDayOfWeek().getValue(),
                    Collectors.counting()
                ));

        // Calculate variance in daily study patterns
        double avgSessionsPerDay = dayOfWeekCounts.values().stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        double variance = dayOfWeekCounts.values().stream()
                .mapToDouble(count -> Math.pow(count - avgSessionsPerDay, 2))
                .average()
                .orElse(0.0);

        // Lower variance = higher consistency
        return Math.max(0, Math.min(100, 100 - (variance * 10)));
    }

    public Map<String, Object> getPersonalizedAIInsights(Long userId) {
        UserAnalytics analytics = analyticsRepository.findByUserId(userId)
                .orElse(calculateRealTimeMetrics(userId));

        Map<String, Object> insights = new HashMap<>();

        // Generate personalized recommendations based on real data
        insights.put("efficiency", analytics.getStudyEfficiency());
        insights.put("goalsCompleted", analytics.getGoalsCompleted());
        insights.put("studyHoursWeek", analytics.getStudyHoursWeek());
        insights.put("dayStreak", analytics.getDayStreak());
        insights.put("focusScore", analytics.getFocusScore());
        insights.put("learningVelocity", analytics.getLearningVelocity());
        insights.put("consistencyScore", analytics.getConsistencyScore());
        insights.put("preferredStudyTime", analytics.getPreferredStudyTime());

        // Generate AI recommendations based on user's actual performance
        insights.put("recommendations", generatePersonalizedRecommendations(analytics));
        insights.put("motivationalMessage", generateMotivationalMessage(analytics));
        insights.put("nextGoals", generateNextGoals(analytics));

        return insights;
    }

    private List<String> generatePersonalizedRecommendations(UserAnalytics analytics) {
        List<String> recommendations = new java.util.ArrayList<>();

        if (analytics.getStudyEfficiency() < 60) {
            recommendations.add("Focus on completing smaller, achievable study sessions to build momentum");
        }

        if (analytics.getConsistencyScore() < 50) {
            recommendations.add("Try to study at the same time each day to build a consistent routine");
        }

        if (analytics.getDayStreak() == 0) {
            recommendations.add("Start a new study streak today - even 15 minutes counts!");
        }

        if (analytics.getStudyHoursWeek() < 5) {
            recommendations.add("Gradually increase your weekly study hours for better results");
        }

        if (analytics.getFocusScore() < 70) {
            recommendations.add("Consider shorter study sessions with more frequent breaks");
        }

        return recommendations.isEmpty() ?
            List.of("Great job! Keep maintaining your excellent study habits!") :
            recommendations;
    }

    private String generateMotivationalMessage(UserAnalytics analytics) {
        if (analytics.getDayStreak() > 7) {
            return "Amazing! You're on fire with a " + analytics.getDayStreak() + "-day streak! 🔥";
        } else if (analytics.getStudyEfficiency() > 80) {
            return "Excellent efficiency! You're mastering your study routine! ⭐";
        } else if (analytics.getGoalsCompleted() > 0) {
            return "Great progress! You've completed " + analytics.getGoalsCompleted() + " goals this month! 🎯";
        } else {
            return "Every expert was once a beginner. You've got this! 💪";
        }
    }

    private List<String> generateNextGoals(UserAnalytics analytics) {
        List<String> goals = new java.util.ArrayList<>();

        if (analytics.getDayStreak() < 7) {
            goals.add("Build a 7-day study streak");
        }

        if (analytics.getStudyHoursWeek() < 10) {
            goals.add("Reach 10 hours of study this week");
        }

        if (analytics.getStudyEfficiency() < 85) {
            goals.add("Achieve 85% study efficiency");
        }

        goals.add("Complete 3 more study sessions this week");

        return goals;
    }

    // Scheduled task to update analytics for all active users
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void updateAllUserAnalytics() {
        // This would update analytics for active users
        // Implementation depends on how you track active users
    }

    // Real-time update when user completes a session
    public void onSessionCompleted(Long userId, StudySchedule completedSession) {
        calculateRealTimeMetrics(userId);

        // Trigger AI insights update
        Map<String, Object> updatedInsights = getPersonalizedAIInsights(userId);

        // Here you could emit WebSocket events for real-time UI updates
        // webSocketService.sendToUser(userId, "analytics-updated", updatedInsights);
    }
}
