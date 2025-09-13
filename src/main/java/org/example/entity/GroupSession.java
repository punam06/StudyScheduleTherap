package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "group_sessions")
public class GroupSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;

    @Column(nullable = false)
    private LocalDateTime scheduledTime;

    @Column(nullable = false)
    private Integer durationMinutes = 60;

    @Column
    private String location;

    @Column
    private String meetingLink;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionType sessionType = SessionType.STUDY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.SCHEDULED;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String aiRecommendations;

    @ElementCollection
    @CollectionTable(name = "session_conflicts", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "conflict_description")
    private List<String> conflicts = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "session_attendees", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "student_id")
    private List<Long> attendeeIds = new ArrayList<>();

    @Column
    private Integer actualAttendees = 0;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    public enum SessionType {
        STUDY, REVIEW, EXAM_PREP, PROJECT_WORK
    }

    public enum SessionStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    // Constructors
    public GroupSession() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public GroupSession(String title, StudyGroup studyGroup, LocalDateTime scheduledTime) {
        this();
        this.title = title;
        this.studyGroup = studyGroup;
        this.scheduledTime = scheduledTime;
    }

    // Helper methods
    public boolean hasConflicts() {
        return conflicts != null && !conflicts.isEmpty();
    }

    public void addConflict(String conflict) {
        if (conflicts == null) {
            conflicts = new ArrayList<>();
        }
        conflicts.add(conflict);
    }

    public void addAttendee(Student student) {
        if (attendeeIds == null) {
            attendeeIds = new ArrayList<>();
        }
        if (!attendeeIds.contains(student.getId())) {
            attendeeIds.add(student.getId());
        }
    }

    public int getAttendancePercentage() {
        if (attendeeIds == null || attendeeIds.isEmpty()) {
            return 0;
        }
        return (int) ((double) actualAttendees / attendeeIds.size() * 100);
    }

    public boolean isUpcoming() {
        return scheduledTime.isAfter(LocalDateTime.now()) && status == SessionStatus.SCHEDULED;
    }

    public boolean isCompleted() {
        return status == SessionStatus.COMPLETED;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public StudyGroup getStudyGroup() { return studyGroup; }
    public void setStudyGroup(StudyGroup studyGroup) { this.studyGroup = studyGroup; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }

    public SessionType getSessionType() { return sessionType; }
    public void setSessionType(SessionType sessionType) { this.sessionType = sessionType; }

    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getAiRecommendations() { return aiRecommendations; }
    public void setAiRecommendations(String aiRecommendations) { this.aiRecommendations = aiRecommendations; }

    public List<String> getConflicts() { return conflicts; }
    public void setConflicts(List<String> conflicts) { this.conflicts = conflicts; }

    public List<Long> getAttendeeIds() { return attendeeIds; }
    public void setAttendeeIds(List<Long> attendeeIds) { this.attendeeIds = attendeeIds; }

    public Integer getActualAttendees() { return actualAttendees; }
    public void setActualAttendees(Integer actualAttendees) { this.actualAttendees = actualAttendees; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Additional helper methods
    public Integer getDuration() { return durationMinutes; }
    public void setDuration(Integer duration) { this.durationMinutes = duration; }
}
