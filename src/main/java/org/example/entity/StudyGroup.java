package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "study_groups")
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String subject;

    @Column
    private String description;

    @Column
    private Integer maxMembers = 6;

    @Column
    private Integer currentMembers = 0;

    @Enumerated(EnumType.STRING)
    private GroupStatus status = GroupStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinator_id")
    private Student coordinator;

    @ManyToMany
    @JoinTable(
        name = "study_group_members",
        joinColumns = @JoinColumn(name = "study_group_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> members = new ArrayList<>();

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL)
    private List<GroupSession> sessions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "group_common_times", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "time_slot")
    private List<String> commonAvailableSlots = new ArrayList<>();

    @Column
    private Double compatibilityScore;

    @Column
    private String learningFocus; // EXAM_PREP, ASSIGNMENT_HELP, CONCEPT_REVIEW, PROJECT_WORK

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    public enum GroupStatus {
        ACTIVE, FULL, ARCHIVED, SUSPENDED
    }

    // Constructors
    public StudyGroup() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public StudyGroup(String name, String subject, Student coordinator) {
        this();
        this.name = name;
        this.subject = subject;
        this.coordinator = coordinator;
        this.members.add(coordinator);
        this.currentMembers = 1;
    }

    // Helper methods
    public boolean addMember(Student student) {
        if (currentMembers < maxMembers && !members.contains(student)) {
            members.add(student);
            currentMembers++;
            if (currentMembers >= maxMembers) {
                status = GroupStatus.FULL;
            }
            updateCommonAvailability();
            return true;
        }
        return false;
    }

    public boolean removeMember(Student student) {
        if (members.remove(student)) {
            currentMembers--;
            if (status == GroupStatus.FULL) {
                status = GroupStatus.ACTIVE;
            }
            updateCommonAvailability();
            return true;
        }
        return false;
    }

    private void updateCommonAvailability() {
        // This would calculate common time slots among all members
        // For now, we'll implement a simple version
        commonAvailableSlots.clear();
        if (!members.isEmpty()) {
            // Implementation would find intersection of all member availabilities
            // Simplified for demo
            commonAvailableSlots.add("MON_10:00-11:30");
            commonAvailableSlots.add("WED_14:00-15:30");
            commonAvailableSlots.add("FRI_09:00-10:30");
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }

    public Integer getCurrentMembers() { return currentMembers; }
    public void setCurrentMembers(Integer currentMembers) { this.currentMembers = currentMembers; }

    public GroupStatus getStatus() { return status; }
    public void setStatus(GroupStatus status) { this.status = status; }

    public Student getCoordinator() { return coordinator; }
    public void setCoordinator(Student coordinator) { this.coordinator = coordinator; }

    public List<Student> getMembers() { return members; }
    public void setMembers(List<Student> members) { this.members = members; }

    public List<GroupSession> getSessions() { return sessions; }
    public void setSessions(List<GroupSession> sessions) { this.sessions = sessions; }

    public List<String> getCommonAvailableSlots() { return commonAvailableSlots; }
    public void setCommonAvailableSlots(List<String> commonAvailableSlots) { this.commonAvailableSlots = commonAvailableSlots; }

    public Double getCompatibilityScore() { return compatibilityScore; }
    public void setCompatibilityScore(Double compatibilityScore) { this.compatibilityScore = compatibilityScore; }

    public String getLearningFocus() { return learningFocus; }
    public void setLearningFocus(String learningFocus) { this.learningFocus = learningFocus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Missing helper methods needed by services
    public boolean isActive() {
        return status == GroupStatus.ACTIVE || status == GroupStatus.FULL;
    }

    public List<String> getPreferredTimes() {
        return commonAvailableSlots;
    }

    public boolean isFull() {
        return currentMembers >= maxMembers;
    }

    public boolean hasAvailableSlots() {
        return currentMembers < maxMembers;
    }
}
