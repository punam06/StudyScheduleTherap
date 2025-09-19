package org.example.repository;

import org.example.entity.Notification;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isRead = false")
    List<Notification> findUnreadByUser(@Param("user") User user);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.isRead = false")
    Long countUnreadByUser(@Param("user") User user);

    @Query("SELECT n FROM Notification n WHERE n.scheduledFor <= :now AND n.isRead = false")
    List<Notification> findScheduledNotifications(@Param("now") LocalDateTime now);

    void deleteByCreatedAtBefore(LocalDateTime date);
}
