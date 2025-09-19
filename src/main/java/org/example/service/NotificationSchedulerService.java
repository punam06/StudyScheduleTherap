package org.example.service;

import org.example.entity.StudySchedule;
import org.example.entity.GroupSession;
import org.example.repository.StudyScheduleRepository;
import org.example.repository.GroupSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationSchedulerService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private StudyScheduleRepository studyScheduleRepository;

    @Autowired
    private GroupSessionRepository groupSessionRepository;

    // Check for upcoming study sessions every 5 minutes
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    public void checkUpcomingSchedules() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime in15Minutes = now.plusMinutes(15);

        // Find schedules starting in the next 15 minutes
        List<StudySchedule> upcomingSchedules = studyScheduleRepository
            .findByStartTimeBetween(now, in15Minutes);

        for (StudySchedule schedule : upcomingSchedules) {
            // Check if notification hasn't been sent already
            if (!hasReminderBeenSent(schedule)) {
                notificationService.createScheduleReminder(schedule, schedule.getStudent());
            }
        }
    }

    // Check for upcoming group sessions every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void checkUpcomingSessions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime in30Minutes = now.plusMinutes(30);

        // Find sessions starting in the next 30 minutes
        List<GroupSession> upcomingSessions = groupSessionRepository
            .findByStartTimeBetween(now, in30Minutes);

        for (GroupSession session : upcomingSessions) {
            if (!hasSessionReminderBeenSent(session)) {
                // Send notification to all group members
                if (session.getStudyGroup() != null && session.getStudyGroup().getMembers() != null) {
                    session.getStudyGroup().getMembers().forEach(member ->
                        notificationService.createSessionReminder(session, member));
                }
            }
        }
    }

    // Check for approaching deadlines daily at 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkDeadlines() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        // Find schedules with deadlines approaching in 1 day
        List<StudySchedule> approachingDeadlines = studyScheduleRepository
            .findByDateBetween(now.toLocalDate(), tomorrow.toLocalDate());

        for (StudySchedule schedule : approachingDeadlines) {
            notificationService.createDeadlineWarning(schedule, schedule.getStudent());
        }
    }

    // Clean up old notifications weekly on Sunday at midnight
    @Scheduled(cron = "0 0 0 ? * SUN")
    public void cleanupOldNotifications() {
        notificationService.cleanupOldNotifications();
    }

    private boolean hasReminderBeenSent(StudySchedule schedule) {
        // In a real implementation, you'd check if a reminder notification
        // has already been created for this schedule
        return false; // Simplified for now
    }

    private boolean hasSessionReminderBeenSent(GroupSession session) {
        // In a real implementation, you'd check if reminder notifications
        // have already been created for this session
        return false; // Simplified for now
    }
}
