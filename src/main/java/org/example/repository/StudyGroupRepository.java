package org.example.repository;

import org.example.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    List<StudyGroup> findBySubject(String subject);

    List<StudyGroup> findByIsActiveTrue();

    @Query("SELECT DISTINCT sg.subject FROM StudyGroup sg WHERE sg.subject IS NOT NULL")
    List<String> findDistinctSubjects();

    @Query("SELECT sg FROM StudyGroup sg WHERE sg.maxMembers > SIZE(sg.members)")
    List<StudyGroup> findGroupsWithAvailableSlots();

    List<StudyGroup> findByNameContainingIgnoreCase(String name);

    @Query("SELECT sg FROM StudyGroup sg WHERE sg.isActive = true AND SIZE(sg.members) < sg.maxMembers")
    List<StudyGroup> findAvailableActiveGroups();
}
