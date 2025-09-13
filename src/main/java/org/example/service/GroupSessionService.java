package org.example.service;

import org.example.entity.GroupSession;
import org.example.entity.Student;
import org.example.entity.StudyGroup;
import org.example.repository.GroupSessionRepository;
import org.example.repository.StudentRepository;
import org.example.repository.StudyGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupSessionService {

    @Autowired
    private GroupSessionRepository sessionRepository;

    @Autowired
    private StudyGroupRepository groupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AIService aiService;

    // Core CRUD operations
    public List<GroupSession> getAllSessions() {
        return sessionRepository.findAll();
    }

    public Optional<GroupSession> getSessionById(Long id) {
        return sessionRepository.findById(id);
    }

    public GroupSession createSession(GroupSession session) {
        // Detect conflicts before saving
        detectAndHandleConflicts(session);

        // Generate AI recommendations
        generateAIRecommendations(session);

        // Auto-add all group members as expected attendees
        session.getStudyGroup().getMembers().forEach(session::addAttendee);

        return sessionRepository.save(session);
    }

    public GroupSession updateSession(GroupSession session) {
        session.setUpdatedAt(LocalDateTime.now());
        detectAndHandleConflicts(session);
        return sessionRepository.save(session);
    }

    public void deleteSession(Long id) {
        sessionRepository.deleteById(id);
    }

    // Smart Scheduling - Core feature from proposal
    public List<LocalDateTime> suggestOptimalTimes(Long groupId, int durationMinutes, int numberOfSuggestions) {
        Optional<StudyGroup> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            return new ArrayList<>();
        }

        StudyGroup group = groupOpt.get();
        List<String> commonSlots = group.getCommonAvailableSlots();

        List<LocalDateTime> suggestions = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Look ahead for the next 2 weeks
        for (int day = 1; day <= 14; day++) {
            LocalDateTime checkDate = now.plusDays(day);

            for (String slot : commonSlots) {
                LocalDateTime proposedTime = parseTimeSlot(slot, checkDate);
                if (proposedTime != null && isTimeSlotAvailable(proposedTime, durationMinutes, group)) {
                    suggestions.add(proposedTime);

                    if (suggestions.size() >= numberOfSuggestions) {
                        break;
                    }
                }
            }

            if (suggestions.size() >= numberOfSuggestions) {
                break;
            }
        }

        // Sort by optimality score
        return suggestions.stream()
                .sorted((t1, t2) -> Double.compare(
                    getTimeOptimalityScore(t2),
                    getTimeOptimalityScore(t1)
                ))
                .collect(Collectors.toList());
    }

    // Conflict Detection and Resolution - Key feature from proposal
    public void detectAndHandleConflicts(GroupSession session) {
        List<String> conflicts = new ArrayList<>();

        // Check for member availability conflicts
        LocalDateTime sessionTime = session.getScheduledTime();
        String dayOfWeek = sessionTime.getDayOfWeek().toString();
        String timeSlot = String.format("%s_%02d:%02d-%02d:%02d",
            dayOfWeek.substring(0, 3),
            sessionTime.getHour(),
            sessionTime.getMinute(),
            sessionTime.plusMinutes(session.getDurationMinutes()).getHour(),
            sessionTime.plusMinutes(session.getDurationMinutes()).getMinute()
        );

        for (Student member : session.getStudyGroup().getMembers()) {
            if (!member.getWeeklyAvailability().contains(timeSlot)) {
                conflicts.add(member.getName() + " is not available at " + timeSlot);
            }
        }

        // Check for overlapping sessions
        List<GroupSession> overlappingSessions = findOverlappingSessions(session);
        if (!overlappingSessions.isEmpty()) {
            conflicts.add("Conflicts with " + overlappingSessions.size() + " existing session(s)");
        }

        // Check for study overload
        long dailySessions = countDailySessionsForGroup(session.getStudyGroup(), sessionTime.toLocalDate());
        if (dailySessions >= 2) {
            conflicts.add("Group already has " + dailySessions + " sessions scheduled for this day");
        }

        session.setConflicts(conflicts);

        // Generate alternative suggestions if conflicts exist
        if (!conflicts.isEmpty()) {
            generateAlternativeTimeSlots(session);
        }
    }

    private List<GroupSession> findOverlappingSessions(GroupSession session) {
        LocalDateTime start = session.getScheduledTime();
        LocalDateTime end = start.plusMinutes(session.getDurationMinutes());

        return sessionRepository.findSessionsBetween(start.minusMinutes(30), end.plusMinutes(30))
                .stream()
                .filter(existingSession ->
                    !existingSession.getId().equals(session.getId()) &&
                    hasCommonMembers(session.getStudyGroup(), existingSession.getStudyGroup())
                )
                .collect(Collectors.toList());
    }

    private boolean hasCommonMembers(StudyGroup group1, StudyGroup group2) {
        return group1.getMembers().stream()
                .anyMatch(member -> group2.getMembers().contains(member));
    }

    private long countDailySessionsForGroup(StudyGroup group, java.time.LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        return sessionRepository.findSessionsBetween(startOfDay, endOfDay)
                .stream()
                .filter(session -> session.getStudyGroup().getId().equals(group.getId()))
                .count();
    }

    // Force Schedule Override - Feature from proposal
    public GroupSession forceScheduleSession(GroupSession session, String reason) {
        session.addConflict("FORCE SCHEDULED: " + reason);
        session.setStatus(GroupSession.SessionStatus.SCHEDULED);

        // Notify affected members
        notifyAffectedMembers(session);

        return sessionRepository.save(session);
    }

    private void notifyAffectedMembers(GroupSession session) {
        // In a real implementation, this would send emails/notifications
        System.out.println("🔔 Notification: Session '" + session.getTitle() +
                          "' has been force-scheduled despite conflicts. Please check your calendar.");
    }

    // AI-Enhanced Session Recommendations
    private void generateAIRecommendations(GroupSession session) {
        StringBuilder recommendations = new StringBuilder();

        // Subject-specific advice
        String subject = session.getStudyGroup().getSubject();
        Map<String, String> advice = aiService.getSubjectSpecificAdvice(subject);

        recommendations.append("Recommended duration: ").append(advice.get("duration")).append(". ");
        recommendations.append("Best technique: ").append(advice.get("technique")).append(". ");
        recommendations.append("Tip: ").append(advice.get("tip"));

        // Session type specific recommendations
        switch (session.getSessionType()) {
            case EXAM_PREP:
                recommendations.append(" Focus on practice problems and review key concepts.");
                break;
            case PROJECT_WORK:
                recommendations.append(" Allocate time for planning, individual work, and group coordination.");
                break;
            case REVIEW:
                recommendations.append(" Use active recall and teach-back methods.");
                break;
        }

        session.setAiRecommendations(recommendations.toString());
    }

    // Alternative Time Slot Generation
    private void generateAlternativeTimeSlots(GroupSession session) {
        List<LocalDateTime> alternatives = suggestOptimalTimes(
            session.getStudyGroup().getId(),
            session.getDurationMinutes(),
            3
        );

        if (!alternatives.isEmpty()) {
            String alternativeText = "Suggested alternatives: " +
                alternatives.stream()
                    .map(time -> time.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")))
                    .collect(Collectors.joining(", "));

            session.addConflict(alternativeText);
        }
    }

    // Utility methods for time parsing and scoring
    private LocalDateTime parseTimeSlot(String slot, LocalDateTime baseDate) {
        try {
            // Parse format "MON_10:00-11:30"
            String[] parts = slot.split("_");
            String dayOfWeek = parts[0];
            String startTime = parts[1].split("-")[0];

            // Map day abbreviation to day of week
            Map<String, Integer> dayMap = Map.of(
                "MON", 1, "TUE", 2, "WED", 3, "THU", 4, "FRI", 5, "SAT", 6, "SUN", 7
            );

            int targetDayOfWeek = dayMap.getOrDefault(dayOfWeek, 1);
            int currentDayOfWeek = baseDate.getDayOfWeek().getValue();

            int daysToAdd = (targetDayOfWeek - currentDayOfWeek + 7) % 7;
            if (daysToAdd == 0 && baseDate.getHour() > Integer.parseInt(startTime.split(":")[0])) {
                daysToAdd = 7; // Next week
            }

            LocalDateTime targetDate = baseDate.plusDays(daysToAdd);
            String[] timeParts = startTime.split(":");

            return targetDate.withHour(Integer.parseInt(timeParts[0]))
                           .withMinute(Integer.parseInt(timeParts[1]))
                           .withSecond(0)
                           .withNano(0);

        } catch (Exception e) {
            return null;
        }
    }

    private boolean isTimeSlotAvailable(LocalDateTime proposedTime, int durationMinutes, StudyGroup group) {
        LocalDateTime endTime = proposedTime.plusMinutes(durationMinutes);

        // Check for existing sessions
        List<GroupSession> conflictingSessions = sessionRepository.findSessionsBetween(
            proposedTime.minusMinutes(30),
            endTime.plusMinutes(30)
        );

        return conflictingSessions.stream()
                .noneMatch(session -> hasCommonMembers(group, session.getStudyGroup()));
    }

    private double getTimeOptimalityScore(LocalDateTime time) {
        int hour = time.getHour();

        // Peak concentration times get higher scores
        if (hour >= 9 && hour <= 11) return 100.0; // Peak morning
        if (hour >= 14 && hour <= 16) return 90.0; // Post-lunch focus
        if (hour >= 19 && hour <= 21) return 80.0; // Evening review
        if (hour >= 7 && hour <= 9) return 70.0;   // Early morning
        if (hour >= 16 && hour <= 18) return 60.0; // Late afternoon
        return 30.0; // Less optimal times
    }

    // Session Analytics and Insights
    public Map<String, Object> getSessionAnalytics(Long groupId) {
        List<GroupSession> groupSessions = sessionRepository.findByStudyGroup(
            groupRepository.findById(groupId).orElse(null)
        );

        Map<String, Object> analytics = new HashMap<>();

        // Basic statistics
        analytics.put("totalSessions", groupSessions.size());
        analytics.put("completedSessions", groupSessions.stream()
            .filter(s -> s.getStatus() == GroupSession.SessionStatus.COMPLETED)
            .count());

        // Average attendance
        double avgAttendance = groupSessions.stream()
            .filter(s -> s.getStatus() == GroupSession.SessionStatus.COMPLETED)
            .mapToInt(GroupSession::getAttendancePercentage)
            .average()
            .orElse(0.0);
        analytics.put("averageAttendance", avgAttendance);

        // Session type distribution
        Map<GroupSession.SessionType, Long> typeDistribution = groupSessions.stream()
            .collect(Collectors.groupingBy(
                GroupSession::getSessionType,
                Collectors.counting()
            ));
        analytics.put("sessionTypeDistribution", typeDistribution);

        // Conflict rate
        long sessionsWithConflicts = groupSessions.stream()
            .filter(GroupSession::hasConflicts)
            .count();
        double conflictRate = groupSessions.isEmpty() ? 0.0 :
            (double) sessionsWithConflicts / groupSessions.size() * 100;
        analytics.put("conflictRate", conflictRate);

        return analytics;
    }

    // Query methods
    public List<GroupSession> getUpcomingSessions() {
        return sessionRepository.findUpcomingSessions(LocalDateTime.now());
    }

    public List<GroupSession> getSessionsByGroup(Long groupId) {
        return groupRepository.findById(groupId)
            .map(sessionRepository::findByStudyGroup)
            .orElse(new ArrayList<>());
    }

    public List<GroupSession> getStudentSessions(Long studentId) {
        return sessionRepository.findSessionsByAttendeeId(studentId);
    }

    public List<GroupSession> getSessionsWithConflicts() {
        return sessionRepository.findSessionsWithConflicts();
    }

    public List<GroupSession> getUpcomingSessionsForStudent(Long studentId) {
        // Get sessions for groups the student is a member of
        return sessionRepository.findAll().stream()
                .filter(session -> session.getStudyGroup().getMembers().stream()
                        .anyMatch(member -> member.getId().equals(studentId)))
                .filter(session -> session.getScheduledTime().isAfter(LocalDateTime.now()))
                .sorted((s1, s2) -> s1.getScheduledTime().compareTo(s2.getScheduledTime()))
                .limit(5)
                .toList();
    }

    public List<GroupSession> getRecentSessionsForStudent(Long studentId) {
        // Get recent completed sessions for groups the student is a member of
        return sessionRepository.findAll().stream()
                .filter(session -> session.getStudyGroup().getMembers().stream()
                        .anyMatch(member -> member.getId().equals(studentId)))
                .filter(session -> session.getScheduledTime().isBefore(LocalDateTime.now()))
                .sorted((s1, s2) -> s2.getScheduledTime().compareTo(s1.getScheduledTime()))
                .limit(5)
                .toList();
    }

    public GroupSession addSessionNotes(Long sessionId, String notes) {
        Optional<GroupSession> sessionOpt = getSessionById(sessionId);
        if (sessionOpt.isPresent()) {
            GroupSession session = sessionOpt.get();
            session.setNotes(notes);
            session.setUpdatedAt(LocalDateTime.now());
            return sessionRepository.save(session);
        }
        return null;
    }
}
