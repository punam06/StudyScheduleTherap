package org.example.repository;

import org.example.entity.StudySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudyScheduleRepository extends JpaRepository<StudySchedule, Long> {

    List<StudySchedule> findBySubject(String subject);

    List<StudySchedule> findByCompleted(Boolean completed);

    List<StudySchedule> findByPriority(String priority);

    @Query("SELECT s FROM StudySchedule s WHERE s.scheduledTime BETWEEN :start AND :end")
    List<StudySchedule> findByScheduledTimeBetween(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);

    @Query("SELECT s FROM StudySchedule s WHERE s.scheduledTime >= :now ORDER BY s.scheduledTime ASC")
    List<StudySchedule> findUpcomingSchedules(@Param("now") LocalDateTime now);

    @Query("SELECT s FROM StudySchedule s WHERE s.scheduledTime BETWEEN :start AND :end AND s.completed = false")
    List<StudySchedule> findUpcomingSchedulesBetween(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);

    @Query("SELECT s FROM StudySchedule s WHERE s.scheduledTime < :now AND s.completed = false")
    List<StudySchedule> findOverdueSchedules(@Param("now") LocalDateTime now);

    @Query("SELECT DISTINCT s.subject FROM StudySchedule s ORDER BY s.subject")
    List<String> findDistinctSubjects();
}
