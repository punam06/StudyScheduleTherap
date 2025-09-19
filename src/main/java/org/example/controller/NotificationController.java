package org.example.controller;

import org.example.entity.Notification;
import org.example.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Get all notifications for a student
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long studentId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByStudentId(studentId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get unread notifications count
    @GetMapping("/student/{studentId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long studentId) {
        try {
            Long count = notificationService.getUnreadCount(studentId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get unread notifications
    @GetMapping("/student/{studentId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long studentId) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotificationsByStudentId(studentId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get recent notifications (last 24 hours)
    @GetMapping("/student/{studentId}/recent")
    public ResponseEntity<List<Notification>> getRecentNotifications(@PathVariable Long studentId) {
        try {
            List<Notification> notifications = notificationService.getRecentNotifications(studentId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Mark notification as read
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to mark notification as read"));
        }
    }

    // Mark all notifications as read for a student
    @PutMapping("/student/{studentId}/mark-all-read")
    public ResponseEntity<Map<String, String>> markAllAsRead(@PathVariable Long studentId) {
        try {
            notificationService.markAllAsRead(studentId);
            return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to mark all notifications as read"));
        }
    }

    // Delete a notification
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable Long notificationId) {
        try {
            notificationService.deleteNotification(notificationId);
            return ResponseEntity.ok(Map.of("message", "Notification deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete notification"));
        }
    }

    // Create a custom notification (for testing or admin purposes)
    @PostMapping("/create")
    public ResponseEntity<Notification> createNotification(@RequestBody Map<String, Object> request) {
        try {
            Long studentId = Long.valueOf(request.get("studentId").toString());
            String title = request.get("title").toString();
            String message = request.get("message").toString();
            String typeStr = request.get("type").toString();

            Notification.NotificationType type = Notification.NotificationType.valueOf(typeStr);

            Notification notification = notificationService.createNotification(studentId, title, message, type);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get latest N notifications
    @GetMapping("/student/{studentId}/latest/{limit}")
    public ResponseEntity<List<Notification>> getLatestNotifications(
            @PathVariable Long studentId,
            @PathVariable int limit) {
        try {
            List<Notification> notifications = notificationService.getLatestNotifications(studentId, limit);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
