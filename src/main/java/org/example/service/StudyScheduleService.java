package org.example.service;

import org.example.entity.StudySchedule;
import org.example.repository.StudyScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StudyScheduleService {

    @Autowired
    private StudyScheduleRepository scheduleRepository;

    @Autowired
    private AIService aiService;

    public List<StudySchedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public List<StudySchedule> getUpcomingSchedules() {
        return scheduleRepository.findUpcomingSchedules(LocalDateTime.now());
    }

    public List<StudySchedule> getSchedulesBySubject(String subject) {
        return scheduleRepository.findBySubject(subject);
    }

    public List<StudySchedule> getCompletedSchedules() {
        return scheduleRepository.findByCompleted(true);
    }

    public List<StudySchedule> getPendingSchedules() {
        return scheduleRepository.findByCompleted(false);
    }

    public Optional<StudySchedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    public StudySchedule saveSchedule(StudySchedule schedule) {
        // Auto-generate AI recommendation for optimal study time if not set
        if (schedule.getDurationMinutes() == null) {
            schedule.setDurationMinutes(getRecommendedDuration(schedule.getSubject()));
        }
        schedule.setUpdatedAt(LocalDateTime.now());
        return scheduleRepository.save(schedule);
    }

    public StudySchedule updateSchedule(Long id, StudySchedule updatedSchedule) {
        return scheduleRepository.findById(id)
                .map(schedule -> {
                    schedule.setSubject(updatedSchedule.getSubject());
                    schedule.setTopic(updatedSchedule.getTopic());
                    schedule.setScheduledTime(updatedSchedule.getScheduledTime());
                    schedule.setDurationMinutes(updatedSchedule.getDurationMinutes());
                    schedule.setPriority(updatedSchedule.getPriority());
                    schedule.setStudyType(updatedSchedule.getStudyType());
                    schedule.setDescription(updatedSchedule.getDescription());
                    schedule.setCompleted(updatedSchedule.getCompleted());
                    schedule.setUpdatedAt(LocalDateTime.now());
                    return scheduleRepository.save(schedule);
                })
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + id));
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    public StudySchedule markAsCompleted(Long id) {
        return scheduleRepository.findById(id)
                .map(schedule -> {
                    schedule.setCompleted(true);
                    schedule.setUpdatedAt(LocalDateTime.now());
                    return scheduleRepository.save(schedule);
                })
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + id));
    }

    public List<String> getAllSubjects() {
        return scheduleRepository.findDistinctSubjects();
    }

    public List<StudySchedule> getTodaySchedules() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return scheduleRepository.findByScheduledTimeBetween(startOfDay, endOfDay);
    }

    public String getAIRecommendation(String subject, int totalHours) {
        return aiService.getSimpleRecommendation(subject, totalHours);
    }

    private Integer getRecommendedDuration(String subject) {
        // Simple AI logic for recommended study duration based on subject
        switch (subject.toLowerCase()) {
            case "mathematics":
            case "physics":
            case "chemistry":
                return 90; // 1.5 hours for technical subjects
            case "history":
            case "literature":
            case "biology":
                return 60; // 1 hour for reading-intensive subjects
            case "programming":
            case "computer science":
                return 120; // 2 hours for practical subjects
            default:
                return 75; // Default 1.25 hours
        }
    }
}
