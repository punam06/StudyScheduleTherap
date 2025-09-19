package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime scheduledFor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private StudySchedule studySchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private GroupSession groupSession;

    // Constructors
    public Notification() {
        this.createdAt = LocalDateTime.now();
    }

    public Notification(String title, String message, NotificationType type, User user) {
        this();
        this.title = title;
        this.message = message;
        this.type = type;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getScheduledFor() { return scheduledFor; }
    public void setScheduledFor(LocalDateTime scheduledFor) { this.scheduledFor = scheduledFor; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public StudySchedule getStudySchedule() { return studySchedule; }
    public void setStudySchedule(StudySchedule studySchedule) { this.studySchedule = studySchedule; }

    public GroupSession getGroupSession() { return groupSession; }
    public void setGroupSession(GroupSession groupSession) { this.groupSession = groupSession; }

    public enum NotificationType {
        SCHEDULE_REMINDER,
        SESSION_REMINDER,
        SCHEDULE_CREATED,
        SESSION_CREATED,
        SESSION_UPDATED,
        STUDY_GOAL_ACHIEVED,
        DEADLINE_WARNING
    }
}
