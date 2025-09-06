package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "study_schedules")
public class StudySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private LocalDateTime scheduledTime;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column
    private String priority;

    @Column
    private String studyType;

    @Column
    private String description;

    @Column
    private Boolean completed = false;

    // New fields for group collaboration
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroup;

    @Column
    private Boolean isGroupStudy = false;

    @Column
    private String aiRecommendations;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    // Constructors
    public StudySchedule() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public StudySchedule(String subject, String topic, LocalDateTime scheduledTime,
                        Integer durationMinutes, String priority, String studyType) {
        this();
        this.subject = subject;
        this.topic = topic;
        this.scheduledTime = scheduledTime;
        this.durationMinutes = durationMinutes;
        this.priority = priority;
        this.studyType = studyType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStudyType() { return studyType; }
    public void setStudyType(String studyType) { this.studyType = studyType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) {
        this.completed = completed;
        this.updatedAt = LocalDateTime.now();
    }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public StudyGroup getStudyGroup() { return studyGroup; }
    public void setStudyGroup(StudyGroup studyGroup) {
        this.studyGroup = studyGroup;
        this.isGroupStudy = (studyGroup != null);
    }

    public Boolean getIsGroupStudy() { return isGroupStudy; }
    public void setIsGroupStudy(Boolean isGroupStudy) { this.isGroupStudy = isGroupStudy; }

    public String getAiRecommendations() { return aiRecommendations; }
    public void setAiRecommendations(String aiRecommendations) { this.aiRecommendations = aiRecommendations; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
