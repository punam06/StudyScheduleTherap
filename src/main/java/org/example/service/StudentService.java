package org.example.service;

import org.example.entity.Student;
import org.example.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public Student registerStudent(Student student) {
        // Validate required fields
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (student.getPassword() == null || student.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        // Ensure proper initialization with default values
        if (student.getGpa() == null) {
            student.setGpa(0.0);
        }
        if (student.getYear() == null) {
            student.setYear(0);
        }
        if (student.getWeeklyAvailability() == null) {
            student.setWeeklyAvailability(new ArrayList<>());
        }
        if (student.getStudyGroups() == null) {
            student.setStudyGroups(new ArrayList<>());
        }

        // Hash password (in production, use BCrypt or similar)
        // For now, storing as plain text for simplicity
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());
        student.setRole(Student.UserRole.STUDENT); // Ensure default role

        return studentRepository.save(student);
    }

    public Optional<Student> authenticateStudent(String email, String password) {
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent() && student.get().getPassword().equals(password)) {
            return student;
        }
        return Optional.empty();
    }

    public boolean existsByEmail(String email) {
        return studentRepository.findByEmail(email).isPresent();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student updateStudent(Student student) {
        student.setUpdatedAt(LocalDateTime.now());
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> getStudentsByRole(Student.UserRole role) {
        return studentRepository.findByRole(role);
    }

    public List<Student> getStudentsByMajor(String major) {
        return studentRepository.findByMajor(major);
    }

    public List<Student> getStudentsWithSimilarSchedule(Student student) {
        // Find students with overlapping availability
        return studentRepository.findAll().stream()
                .filter(s -> !s.getId().equals(student.getId()))
                .filter(s -> hasOverlappingAvailability(s.getWeeklyAvailability(), student.getWeeklyAvailability()))
                .toList();
    }

    private boolean hasOverlappingAvailability(List<String> availability1, List<String> availability2) {
        return availability1.stream()
                .anyMatch(slot1 -> availability2.stream()
                        .anyMatch(slot2 -> slot1.equals(slot2)));
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}
