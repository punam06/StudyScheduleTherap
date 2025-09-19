package org.example.service;

import org.example.entity.Notification;
import org.example.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // Create a new notification
    public Notification createNotification(Long studentId, String title, String message,
                                         Notification.NotificationType type) {
        Notification notification = new Notification(studentId, title, message, type);
        return notificationRepository.save(notification);
    }

    // Create a notification with related entity
    public Notification createNotification(Long studentId, String title, String message,
                                         Notification.NotificationType type, String relatedEntityId) {
        Notification notification = new Notification(studentId, title, message, type, relatedEntityId);
        return notificationRepository.save(notification);
    }

    // Get all notifications for a student
    public List<Notification> getNotificationsByStudentId(Long studentId) {
        return notificationRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    // Get unread notifications for a student
    public List<Notification> getUnreadNotificationsByStudentId(Long studentId) {
        return notificationRepository.findByStudentIdAndIsReadOrderByCreatedAtDesc(studentId, false);
    }

    // Get count of unread notifications
    public Long getUnreadCount(Long studentId) {
        return notificationRepository.countUnreadByStudentId(studentId);
    }

    // Mark notification as read
    public void markAsRead(Long notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isPresent()) {
            notification.get().setIsRead(true);
            notificationRepository.save(notification.get());
        }
    }

    // Mark all notifications as read for a student
    public void markAllAsRead(Long studentId) {
        List<Notification> unreadNotifications = getUnreadNotificationsByStudentId(studentId);
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    // Get recent notifications (last 24 hours)
    public List<Notification> getRecentNotifications(Long studentId) {
        LocalDateTime since = LocalDateTime.now().minusDays(1);
        return notificationRepository.findRecentByStudentId(studentId, since);
    }

    // Get latest N notifications
    public List<Notification> getLatestNotifications(Long studentId, int limit) {
        return notificationRepository.findLatestByStudentId(studentId, limit);
    }

    // Delete a notification
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // Create study reminder notifications
    public void createStudyReminder(Long studentId, String subject, LocalDateTime studyTime) {
        String title = "Study Reminder";
        String message = String.format("Don't forget to study %s at %s",
                                     subject, studyTime.toString());
        createNotification(studentId, title, message, Notification.NotificationType.SCHEDULE_REMINDER);
    }

    // Create session reminder
    public void createSessionReminder(Long studentId, String sessionName, LocalDateTime sessionTime) {
        String title = "Session Reminder";
        String message = String.format("Your study session '%s' starts at %s",
                                     sessionName, sessionTime.toString());
        createNotification(studentId, title, message, Notification.NotificationType.SESSION_REMINDER);
    }

    // Create group invitation notification
    public void createGroupInvitation(Long studentId, String groupName, String inviterName) {
        String title = "Group Invitation";
        String message = String.format("%s invited you to join the study group '%s'",
                                     inviterName, groupName);
        createNotification(studentId, title, message, Notification.NotificationType.GROUP_INVITATION);
    }

    // Create deadline warning
    public void createDeadlineWarning(Long studentId, String task, LocalDateTime deadline) {
        String title = "Deadline Warning";
        String message = String.format("Reminder: '%s' is due on %s",
                                     task, deadline.toString());
        createNotification(studentId, title, message, Notification.NotificationType.DEADLINE_WARNING);
    }

    // Create study tip notification
    public void createStudyTip(Long studentId, String tip) {
        String title = "Study Tip";
        createNotification(studentId, title, tip, Notification.NotificationType.STUDY_TIP);
    }
}
