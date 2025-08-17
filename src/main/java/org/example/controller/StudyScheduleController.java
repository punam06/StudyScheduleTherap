package org.example.controller;

import org.example.entity.StudySchedule;
import org.example.service.StudyScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/schedules")
public class StudyScheduleController {

    @Autowired
    private StudyScheduleService scheduleService;

    @GetMapping
    public String listSchedules(Model model) {
        List<StudySchedule> allSchedules = scheduleService.getAllSchedules();
        List<StudySchedule> upcomingSchedules = scheduleService.getUpcomingSchedules();
        List<StudySchedule> todaySchedules = scheduleService.getTodaySchedules();

        model.addAttribute("allSchedules", allSchedules);
        model.addAttribute("upcomingSchedules", upcomingSchedules);
        model.addAttribute("todaySchedules", todaySchedules);
        model.addAttribute("subjects", scheduleService.getAllSubjects());

        return "schedules";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("schedule", new StudySchedule());
        model.addAttribute("subjects", scheduleService.getAllSubjects());
        return "schedule-form";
    }

    @PostMapping("/save")
    public String saveSchedule(@ModelAttribute StudySchedule schedule,
                              @RequestParam String scheduledDate,
                              @RequestParam String scheduledTime,
                              RedirectAttributes redirectAttributes) {
        try {
            // Combine date and time
            String dateTimeString = scheduledDate + "T" + scheduledTime;
            LocalDateTime scheduledDateTime = LocalDateTime.parse(dateTimeString);
            schedule.setScheduledTime(scheduledDateTime);

            scheduleService.saveSchedule(schedule);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating schedule: " + e.getMessage());
        }

        return "redirect:/schedules";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        StudySchedule schedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        model.addAttribute("schedule", schedule);
        model.addAttribute("subjects", scheduleService.getAllSubjects());

        // Format date and time for form
        if (schedule.getScheduledTime() != null) {
            model.addAttribute("scheduledDate",
                schedule.getScheduledTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            model.addAttribute("scheduledTime",
                schedule.getScheduledTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        return "schedule-form";
    }

    @PostMapping("/update/{id}")
    public String updateSchedule(@PathVariable Long id,
                                @ModelAttribute StudySchedule schedule,
                                @RequestParam String scheduledDate,
                                @RequestParam String scheduledTime,
                                RedirectAttributes redirectAttributes) {
        try {
            String dateTimeString = scheduledDate + "T" + scheduledTime;
            LocalDateTime scheduledDateTime = LocalDateTime.parse(dateTimeString);
            schedule.setScheduledTime(scheduledDateTime);

            scheduleService.updateSchedule(id, schedule);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating schedule: " + e.getMessage());
        }

        return "redirect:/schedules";
    }

    @PostMapping("/complete/{id}")
    public String markAsCompleted(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            scheduleService.markAsCompleted(id);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule marked as completed!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating schedule: " + e.getMessage());
        }

        return "redirect:/schedules";
    }

    @PostMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            scheduleService.deleteSchedule(id);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting schedule: " + e.getMessage());
        }

        return "redirect:/schedules";
    }

    @GetMapping("/subject/{subject}")
    public String schedulesBySubject(@PathVariable String subject, Model model) {
        List<StudySchedule> schedules = scheduleService.getSchedulesBySubject(subject);
        model.addAttribute("schedules", schedules);
        model.addAttribute("subject", subject);
        model.addAttribute("subjects", scheduleService.getAllSubjects());

        return "schedules-by-subject";
    }
}
