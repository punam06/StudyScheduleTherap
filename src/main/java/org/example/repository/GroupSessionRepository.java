package org.example.repository;

import org.example.entity.GroupSession;
import org.example.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GroupSessionRepository extends JpaRepository<GroupSession, Long> {

    List<GroupSession> findByStudyGroup(StudyGroup studyGroup);

    @Query("SELECT gs FROM GroupSession gs WHERE gs.scheduledTime > :currentTime ORDER BY gs.scheduledTime ASC")
    List<GroupSession> findUpcomingSessions(@Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT gs FROM GroupSession gs WHERE gs.scheduledTime BETWEEN :startTime AND :endTime")
    List<GroupSession> findSessionsBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT gs FROM GroupSession gs WHERE SIZE(gs.conflicts) > 0")
    List<GroupSession> findSessionsWithConflicts();

    @Query("SELECT gs FROM GroupSession gs JOIN gs.studyGroup.members m WHERE m.id = :studentId")
    List<GroupSession> findSessionsByAttendeeId(@Param("studentId") Long studentId);

    List<GroupSession> findBySessionType(GroupSession.SessionType sessionType);

    @Query("SELECT gs FROM GroupSession gs WHERE gs.status = :status")
    List<GroupSession> findByStatus(@Param("status") GroupSession.SessionStatus status);
}
