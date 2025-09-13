package org.example.repository;

import org.example.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);

    List<Student> findByRole(Student.UserRole role);

    List<Student> findByMajor(String major);

    List<Student> findByYear(Integer year);

    List<Student> findByLearningStyle(String learningStyle);

    @Query("SELECT s FROM Student s WHERE s.gpa >= :minGpa")
    List<Student> findStudentsWithMinimumGpa(@Param("minGpa") Double minGpa);

    @Query("SELECT s FROM Student s WHERE s.major = :major AND s.year = :year")
    List<Student> findByMajorAndYear(@Param("major") String major, @Param("year") Integer year);

    @Query("SELECT DISTINCT s.major FROM Student s WHERE s.major IS NOT NULL")
    List<String> findDistinctMajors();

    @Query("SELECT s FROM Student s WHERE SIZE(s.studyGroups) < :maxGroups")
    List<Student> findStudentsWithFewGroups(@Param("maxGroups") int maxGroups);
}
