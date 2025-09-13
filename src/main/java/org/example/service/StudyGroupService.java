package org.example.service;

import org.example.entity.StudyGroup;
import org.example.entity.Student;
import org.example.repository.StudyGroupRepository;
import org.example.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudyGroupService {

    @Autowired
    private StudyGroupRepository groupRepository;

    @Autowired
    private StudentRepository studentRepository;

    public List<StudyGroup> getAllGroups() {
        return groupRepository.findAll();
    }

    public Optional<StudyGroup> getGroupById(Long id) {
        return groupRepository.findById(id);
    }

    public StudyGroup createGroup(StudyGroup group) {
        group.setCreatedAt(LocalDateTime.now());
        group.setUpdatedAt(LocalDateTime.now());
        return groupRepository.save(group);
    }

    public StudyGroup updateGroup(StudyGroup group) {
        group.setUpdatedAt(LocalDateTime.now());
        return groupRepository.save(group);
    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }

    public List<StudyGroup> getGroupsBySubject(String subject) {
        return groupRepository.findBySubject(subject);
    }

    public List<StudyGroup> getAvailableGroups() {
        return groupRepository.findByIsActiveTrue();
    }

    public List<String> getAllSubjects() {
        return groupRepository.findDistinctSubjects();
    }

    public List<StudyGroup> getRecommendedGroupsForStudent(Student student) {
        // Find groups with same major/subject
        List<StudyGroup> recommendedGroups = groupRepository.findBySubject(student.getMajor());

        // Filter out groups the student is already in
        return recommendedGroups.stream()
                .filter(group -> !group.getMembers().contains(student))
                .filter(StudyGroup::isActive)
                .limit(6) // Limit to 6 recommendations
                .toList();
    }

    public StudyGroup joinGroup(Long groupId, Long studentId) {
        Optional<StudyGroup> groupOpt = groupRepository.findById(groupId);
        Optional<Student> studentOpt = studentRepository.findById(studentId);

        if (groupOpt.isPresent() && studentOpt.isPresent()) {
            StudyGroup group = groupOpt.get();
            Student student = studentOpt.get();

            if (!group.getMembers().contains(student) && group.getMembers().size() < group.getMaxMembers()) {
                group.getMembers().add(student);
                group.setUpdatedAt(LocalDateTime.now());
                return groupRepository.save(group);
            }
        }
        return null;
    }

    public StudyGroup leaveGroup(Long groupId, Long studentId) {
        Optional<StudyGroup> groupOpt = groupRepository.findById(groupId);
        Optional<Student> studentOpt = studentRepository.findById(studentId);

        if (groupOpt.isPresent() && studentOpt.isPresent()) {
            StudyGroup group = groupOpt.get();
            Student student = studentOpt.get();

            group.getMembers().remove(student);
            group.setUpdatedAt(LocalDateTime.now());
            return groupRepository.save(group);
        }
        return null;
    }

    public List<StudyGroup> findGroupsWithCompatibleSchedules(Student student) {
        // In a real implementation, this would check schedule compatibility
        return groupRepository.findByIsActiveTrue().stream()
                .filter(group -> !group.getMembers().contains(student))
                .filter(group -> hasCompatibleSchedule(group, student))
                .toList();
    }

    private boolean hasCompatibleSchedule(StudyGroup group, Student student) {
        // Simplified compatibility check
        return group.getPreferredTimes().stream()
                .anyMatch(time -> student.getWeeklyAvailability().contains(time));
    }

    // Missing methods needed by StudyGroupController
    public List<String> getGroupRecommendations(Long groupId) {
        Optional<StudyGroup> groupOpt = getGroupById(groupId);
        if (groupOpt.isEmpty()) {
            return new ArrayList<>();
        }

        StudyGroup group = groupOpt.get();
        List<String> recommendations = new ArrayList<>();

        // Generate recommendations based on group data
        recommendations.add("Consider scheduling sessions during " + group.getCommonAvailableSlots().get(0) + " for maximum attendance");
        recommendations.add("Current group size (" + group.getCurrentMembers() + ") is optimal for collaborative learning");
        recommendations.add("Focus on " + group.getLearningFocus() + " activities for this subject");

        return recommendations;
    }

    public List<String> findOptimalMeetingTimes(Long groupId) {
        Optional<StudyGroup> groupOpt = getGroupById(groupId);
        if (groupOpt.isEmpty()) {
            return new ArrayList<>();
        }

        StudyGroup group = groupOpt.get();
        return group.getCommonAvailableSlots();
    }

    public List<StudyGroup> findMatchingGroups(String subject, String learningStyle, List<String> availability) {
        List<StudyGroup> allGroups = getAllGroups();
        return allGroups.stream()
                .filter(group -> group.getSubject().equalsIgnoreCase(subject))
                .filter(group -> group.hasAvailableSlots())
                .filter(group -> hasMatchingAvailability(group, availability))
                .limit(10)
                .toList();
    }

    private boolean hasMatchingAvailability(StudyGroup group, List<String> availability) {
        return group.getCommonAvailableSlots().stream()
                .anyMatch(availability::contains);
    }

    public List<StudyGroup> getStudentGroups(Long studentId) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isPresent()) {
            return studentOpt.get().getStudyGroups();
        }
        return new ArrayList<>();
    }

    public List<String> detectScheduleConflicts(Student student, StudyGroup group) {
        List<String> conflicts = new ArrayList<>();

        // Check if student's availability matches group's preferred times
        boolean hasCommonTime = group.getCommonAvailableSlots().stream()
                .anyMatch(slot -> student.getWeeklyAvailability().contains(slot));

        if (!hasCommonTime) {
            conflicts.add("No common available time slots found");
        }

        // Check if student is already in too many groups
        if (student.getStudyGroups().size() >= 5) {
            conflicts.add("Student is already in maximum number of groups");
        }

        return conflicts;
    }

    // Fix the joinGroup method return type issue
    public boolean joinGroupBoolean(Long groupId, Long studentId) {
        StudyGroup result = joinGroup(groupId, studentId);
        return result != null;
    }
}
