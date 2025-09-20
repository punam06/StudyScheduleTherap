package org.example.repository;

import org.example.entity.UserAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserAnalyticsRepository extends JpaRepository<UserAnalytics, Long> {

    Optional<UserAnalytics> findByUserId(Long userId);

    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.user.id = :userId")
    Optional<UserAnalytics> findByUserIdWithDetails(@Param("userId") Long userId);

    @Query("SELECT COUNT(s) FROM StudySchedule s WHERE s.user.id = :userId AND s.completed = true AND s.scheduledTime >= :startDate")
    Integer countCompletedSessionsInPeriod(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT SUM(s.duration) FROM StudySchedule s WHERE s.user.id = :userId AND s.completed = true AND s.scheduledTime >= :startDate")
    Double sumStudyHoursInPeriod(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(DISTINCT DATE(s.scheduledTime)) FROM StudySchedule s WHERE s.user.id = :userId AND s.completed = true AND s.scheduledTime >= :startDate")
    Integer countStudyDaysInPeriod(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
}
