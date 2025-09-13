package org.example.repository;

import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email already exists
     */
    boolean existsByEmail(String email);

    /**
     * Find users by role
     */
    List<User> findByRole(User.UserRole role);

    /**
     * Find users by major
     */
    List<User> findByMajor(String major);

    /**
     * Find active users
     */
    List<User> findByIsActiveTrue();

    /**
     * Find users by learning style
     */
    List<User> findByLearningStyle(String learningStyle);

    /**
     * Find users by institution
     */
    List<User> findByInstitution(String institution);

    /**
     * Custom query to find users with similar interests (same major and year)
     */
    @Query("SELECT u FROM User u WHERE u.major = :major AND u.year = :year AND u.id != :userId AND u.isActive = true")
    List<User> findSimilarUsers(@Param("major") String major, @Param("year") Integer year, @Param("userId") Long userId);

    /**
     * Count active users
     */
    long countByIsActiveTrue();

    /**
     * Find users created in the last N days
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= CURRENT_TIMESTAMP - :days DAY")
    List<User> findRecentUsers(@Param("days") int days);
}
