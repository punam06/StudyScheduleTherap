package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_analytics")
public class UserAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "study_efficiency")
    private Double studyEfficiency = 0.0;

    @Column(name = "goals_completed")
    private Integer goalsCompleted = 0;

    @Column(name = "study_hours_week")
    private Double studyHoursWeek = 0.0;

    @Column(name = "day_streak")
    private Integer dayStreak = 0;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @Column(name = "total_sessions")
    private Integer totalSessions = 0;

    @Column(name = "completed_sessions")
    private Integer completedSessions = 0;

    @Column(name = "average_session_duration")
    private Double averageSessionDuration = 0.0;

    @Column(name = "preferred_study_time")
    private String preferredStudyTime;

    @Column(name = "focus_score")
    private Double focusScore = 0.0;

    @Column(name = "learning_velocity")
    private Double learningVelocity = 0.0;

    @Column(name = "consistency_score")
    private Double consistencyScore = 0.0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();

    // Constructors
    public UserAnalytics() {}

    public UserAnalytics(User user) {
        this.user = user;
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getStudyEfficiency() {
        return studyEfficiency;
    }

    public void setStudyEfficiency(Double studyEfficiency) {
        this.studyEfficiency = studyEfficiency;
    }

    public Integer getGoalsCompleted() {
        return goalsCompleted;
    }

    public void setGoalsCompleted(Integer goalsCompleted) {
        this.goalsCompleted = goalsCompleted;
    }

    public Double getStudyHoursWeek() {
        return studyHoursWeek;
    }

    public void setStudyHoursWeek(Double studyHoursWeek) {
        this.studyHoursWeek = studyHoursWeek;
    }

    public Integer getDayStreak() {
        return dayStreak;
    }

    public void setDayStreak(Integer dayStreak) {
        this.dayStreak = dayStreak;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    public Integer getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }

    public Integer getCompletedSessions() {
        return completedSessions;
    }

    public void setCompletedSessions(Integer completedSessions) {
        this.completedSessions = completedSessions;
    }

    public Double getAverageSessionDuration() {
        return averageSessionDuration;
    }

    public void setAverageSessionDuration(Double averageSessionDuration) {
        this.averageSessionDuration = averageSessionDuration;
    }

    public String getPreferredStudyTime() {
        return preferredStudyTime;
    }

    public void setPreferredStudyTime(String preferredStudyTime) {
        this.preferredStudyTime = preferredStudyTime;
    }

    public Double getFocusScore() {
        return focusScore;
    }

    public void setFocusScore(Double focusScore) {
        this.focusScore = focusScore;
    }

    public Double getLearningVelocity() {
        return learningVelocity;
    }

    public void setLearningVelocity(Double learningVelocity) {
        this.learningVelocity = learningVelocity;
    }

    public Double getConsistencyScore() {
        return consistencyScore;
    }

    public void setConsistencyScore(Double consistencyScore) {
        this.consistencyScore = consistencyScore;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
