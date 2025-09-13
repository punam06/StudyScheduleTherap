package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.STUDENT;

    @Column
    private String learningStyle; // VISUAL, AUDITORY, KINESTHETIC, READING_WRITING

    @Column
    private Double gpa = 0.0; // Initialize with 0.0 instead of random value

    @Column
    private String major; // Initialize as null

    @Column
    private Integer year = 0; // Initialize with 0 instead of random value

    @ElementCollection
    @CollectionTable(name = "student_availability", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "time_slot")
    private List<String> weeklyAvailability = new ArrayList<>(); // Empty list by default

    @ManyToMany(mappedBy = "members")
    private List<StudyGroup> studyGroups = new ArrayList<>(); // Empty list by default

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    public enum UserRole {
        STUDENT, COORDINATOR, INSTRUCTOR
    }

    // Constructors
    public Student() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // Initialize all numeric fields with 0 and collections with empty lists
        this.gpa = 0.0;
        this.year = 0;
        this.weeklyAvailability = new ArrayList<>();
        this.studyGroups = new ArrayList<>();
    }

    public Student(String email, String name, String password) {
        this(); // Call default constructor to ensure proper initialization
        this.email = email;
        this.name = name;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getLearningStyle() { return learningStyle; }
    public void setLearningStyle(String learningStyle) { this.learningStyle = learningStyle; }

    public Double getGpa() { return gpa; }
    public void setGpa(Double gpa) { this.gpa = gpa; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public List<String> getWeeklyAvailability() { return weeklyAvailability; }
    public void setWeeklyAvailability(List<String> weeklyAvailability) { this.weeklyAvailability = weeklyAvailability; }

    public List<StudyGroup> getStudyGroups() { return studyGroups; }
    public void setStudyGroups(List<StudyGroup> studyGroups) { this.studyGroups = studyGroups; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
