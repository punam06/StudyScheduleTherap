package org.example.service;

import org.example.entity.Notification;
import org.example.entity.StudySchedule;
import org.example.entity.GroupSession;
import org.example.repository.StudyScheduleRepository;
import org.example.repository.GroupSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class NotificationSchedulerService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private StudyScheduleRepository studyScheduleRepository;

    @Autowired
    private GroupSessionRepository groupSessionRepository;

    // Check for upcoming study sessions every 15 minutes
    @Scheduled(fixedRate = 900000) // 15 minutes = 900,000 milliseconds
    public void checkUpcomingSchedules() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneHourFromNow = now.plusHours(1);
            LocalDateTime tomorrow = now.plusDays(1);

            // Find schedules starting within the next hour (1-hour reminders)
            List<StudySchedule> upcomingSchedules = studyScheduleRepository.findUpcomingSchedulesBetween(now, oneHourFromNow);
            for (StudySchedule schedule : upcomingSchedules) {
                createScheduleReminder(schedule, "1 hour");
            }

            // Find schedules starting tomorrow (24-hour reminders)
            LocalDateTime tomorrowStart = tomorrow.withHour(0).withMinute(0);
            LocalDateTime tomorrowEnd = tomorrow.withHour(23).withMinute(59);
            List<StudySchedule> tomorrowSchedules = studyScheduleRepository.findUpcomingSchedulesBetween(tomorrowStart, tomorrowEnd);
            for (StudySchedule schedule : tomorrowSchedules) {
                createScheduleReminder(schedule, "24 hours");
            }

        } catch (Exception e) {
            System.err.println("Error checking upcoming schedules: " + e.getMessage());
        }
    }

    // Check for upcoming group sessions every 10 minutes
    @Scheduled(fixedRate = 600000) // 10 minutes = 600,000 milliseconds
    public void checkUpcomingSessions() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime thirtyMinutesFromNow = now.plusMinutes(30);

            // Find sessions starting within the next 30 minutes
            List<GroupSession> upcomingSessions = groupSessionRepository.findSessionsBetween(now, thirtyMinutesFromNow);
            for (GroupSession session : upcomingSessions) {
                createSessionReminder(session);
            }

        } catch (Exception e) {
            System.err.println("Error checking upcoming sessions: " + e.getMessage());
        }
    }

    // Send daily study tips every day at 9 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyStudyTips() {
        try {
            // Get all active students (you would need to implement this)
            List<Long> activeStudentIds = getActiveStudentIds();

            String tip = getRandomStudyTip();

            for (Long studentId : activeStudentIds) {
                notificationService.createStudyTip(studentId, tip);
            }

        } catch (Exception e) {
            System.err.println("Error sending daily study tips: " + e.getMessage());
        }
    }

    // Check for overdue tasks every hour
    @Scheduled(fixedRate = 3600000) // 1 hour = 3,600,000 milliseconds
    public void checkOverdueTasks() {
        try {
            LocalDateTime now = LocalDateTime.now();

            // Find overdue schedules
            List<StudySchedule> overdueSchedules = studyScheduleRepository.findOverdueSchedules(now);
            for (StudySchedule schedule : overdueSchedules) {
                createOverdueWarning(schedule);
            }

        } catch (Exception e) {
            System.err.println("Error checking overdue tasks: " + e.getMessage());
        }
    }

    // Send weekly progress summary every Sunday at 6 PM
    @Scheduled(cron = "0 0 18 * * SUN")
    public void sendWeeklyProgressSummary() {
        try {
            List<Long> activeStudentIds = getActiveStudentIds();

            for (Long studentId : activeStudentIds) {
                String summary = generateWeeklyProgressSummary(studentId);
                notificationService.createNotification(
                    studentId,
                    "Weekly Progress Summary",
                    summary,
                    Notification.NotificationType.SYSTEM_NOTIFICATION
                );
            }

        } catch (Exception e) {
            System.err.println("Error sending weekly progress summary: " + e.getMessage());
        }
    }

    private void createScheduleReminder(StudySchedule schedule, String timeframe) {
        try {
            String formattedTime = schedule.getScheduledTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"));
            String title = String.format("Study Reminder - %s", timeframe);
            String message = String.format(
                "Don't forget! You have '%s' scheduled for %s. Subject: %s",
                schedule.getTopic(),
                formattedTime,
                schedule.getSubject()
            );

            // Get student ID from the Student entity
            Long studentId = schedule.getStudent() != null ? schedule.getStudent().getId() : 1L;

            notificationService.createNotification(
                studentId,
                title,
                message,
                Notification.NotificationType.SCHEDULE_REMINDER,
                schedule.getId().toString()
            );
        } catch (Exception e) {
            System.err.println("Error creating schedule reminder: " + e.getMessage());
        }
    }

    private void createSessionReminder(GroupSession session) {
        try {
            String formattedTime = session.getScheduledTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"));
            String title = "Group Session Starting Soon";
            String message = String.format(
                "Your group study session '%s' starts in 30 minutes (%s). Don't be late!",
                session.getTitle(),
                formattedTime
            );

            // Send to all participants in the session
            List<Long> participantIds = getSessionParticipants(session);

            for (Long participantId : participantIds) {
                notificationService.createNotification(
                    participantId,
                    title,
                    message,
                    Notification.NotificationType.SESSION_REMINDER,
                    session.getId().toString()
                );
            }
        } catch (Exception e) {
            System.err.println("Error creating session reminder: " + e.getMessage());
        }
    }

    private void createOverdueWarning(StudySchedule schedule) {
        try {
            String title = "Overdue Task Warning";
            String message = String.format(
                "Your study task '%s' for %s was scheduled for %s and is now overdue. Consider rescheduling or marking as complete.",
                schedule.getTopic(),
                schedule.getSubject(),
                schedule.getScheduledTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"))
            );

            // Get student ID from the Student entity
            Long studentId = schedule.getStudent() != null ? schedule.getStudent().getId() : 1L;

            notificationService.createNotification(
                studentId,
                title,
                message,
                Notification.NotificationType.DEADLINE_WARNING,
                schedule.getId().toString()
            );
        } catch (Exception e) {
            System.err.println("Error creating overdue warning: " + e.getMessage());
        }
    }

    private String getRandomStudyTip() {
        String[] tips = {
            "Take regular breaks every 25-30 minutes to maintain focus and prevent burnout.",
            "Create a dedicated study space free from distractions to improve concentration.",
            "Use active recall techniques like flashcards and practice questions instead of just re-reading.",
            "Set specific, achievable goals for each study session to stay motivated.",
            "Get enough sleep - your brain consolidates information during rest.",
            "Stay hydrated and eat brain-healthy foods like nuts, berries, and fish.",
            "Use the Pomodoro Technique: 25 minutes of focused study followed by a 5-minute break.",
            "Teach concepts to others or explain them out loud to reinforce your understanding.",
            "Practice spaced repetition - review material at increasing intervals over time.",
            "Find your peak concentration hours and schedule difficult subjects during those times.",
            "Use visual aids like mind maps and diagrams to organize complex information.",
            "Join study groups to discuss concepts and gain different perspectives.",
            "Eliminate digital distractions by using website blockers during study time.",
            "Reward yourself after completing study goals to maintain motivation.",
            "Review material before bed - your brain processes information during sleep."
        };

        Random random = new Random();
        return tips[random.nextInt(tips.length)];
    }

    private String generateWeeklyProgressSummary(Long studentId) {
        // This would analyze the student's study patterns, completed schedules, etc.
        // For now, returning a template message
        return "This week you completed several study sessions and stayed on track with your schedule. " +
               "Keep up the great work! Remember to review your completed topics and prepare for upcoming deadlines. " +
               "Your consistency is the key to success!";
    }

    private List<Long> getActiveStudentIds() {
        // This would query the database for active students
        // For now, returning a placeholder - you'd implement based on your Student entity
        try {
            // return studentRepository.findActiveStudentIds();
            return List.of(1L, 2L, 3L); // Placeholder
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<Long> getSessionParticipants(GroupSession session) {
        // Get attendee IDs from the session
        try {
            List<Long> attendeeIds = session.getAttendeeIds();
            if (attendeeIds != null && !attendeeIds.isEmpty()) {
                return attendeeIds;
            }
            // Fallback to study group members if no specific attendees
            return List.of(1L); // Placeholder
        } catch (Exception e) {
            return List.of();
        }
    }

    // Method to create welcome notifications for new users
    public void createWelcomeNotifications(Long studentId) {
        try {
            notificationService.createNotification(
                studentId,
                "Welcome to Study Schedule Therapy!",
                "Welcome to your personalized study management system! Start by creating your first study schedule and explore our AI-powered recommendations.",
                Notification.NotificationType.SYSTEM_NOTIFICATION
            );

            // Send a study tip after 5 minutes (in a real app, you'd schedule this)
            notificationService.createStudyTip(
                studentId,
                "Pro tip: Start with short, focused study sessions and gradually increase duration as you build the habit."
            );

        } catch (Exception e) {
            System.err.println("Error creating welcome notifications: " + e.getMessage());
        }
    }
}
