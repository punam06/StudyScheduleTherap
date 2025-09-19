package org.example.repository;

import org.example.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<Notification> findByStudentIdAndIsReadOrderByCreatedAtDesc(Long studentId, Boolean isRead);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.studentId = :studentId AND n.isRead = false")
    Long countUnreadByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT n FROM Notification n WHERE n.studentId = :studentId AND n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentByStudentId(@Param("studentId") Long studentId, @Param("since") LocalDateTime since);

    List<Notification> findByStudentIdAndTypeOrderByCreatedAtDesc(Long studentId, Notification.NotificationType type);

    @Query("SELECT n FROM Notification n WHERE n.studentId = :studentId ORDER BY n.createdAt DESC LIMIT :limit")
    List<Notification> findLatestByStudentId(@Param("studentId") Long studentId, @Param("limit") int limit);
}
