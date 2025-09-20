package org.example.controller;

import org.example.entity.StudySchedule;
import org.example.entity.StudyGroup;
import org.example.service.StudyScheduleService;
import org.example.service.StudyGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/jsp")
public class JspController {

    @Autowired
    private StudyScheduleService scheduleService;

    @Autowired
    private StudyGroupService groupService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            List<StudySchedule> allSchedules = scheduleService.getAllSchedules();
            List<StudyGroup> allGroups = groupService.getAllGroups();

            long totalSchedules = allSchedules.size();
            long completedSchedules = allSchedules.stream()
                    .mapToLong(s -> s.getCompleted() ? 1 : 0)
                    .sum();
            long pendingSchedules = totalSchedules - completedSchedules;

            model.addAttribute("totalSchedules", totalSchedules);
            model.addAttribute("completedSchedules", completedSchedules);
            model.addAttribute("pendingSchedules", pendingSchedules);
            model.addAttribute("totalGroups", allGroups.size());
            model.addAttribute("recentSchedules", allSchedules.stream()
                    .limit(5)
                    .toList());
        } catch (Exception e) {
            model.addAttribute("totalSchedules", 0);
            model.addAttribute("completedSchedules", 0);
            model.addAttribute("pendingSchedules", 0);
            model.addAttribute("totalGroups", 0);
            model.addAttribute("recentSchedules", List.of());
        }

        return "dashboard";
    }

    @GetMapping("/schedules")
    public String schedules(Model model) {
        try {
            List<StudySchedule> schedules = scheduleService.getAllSchedules();
            model.addAttribute("schedules", schedules);
        } catch (Exception e) {
            model.addAttribute("schedules", List.of());
            model.addAttribute("error", "Unable to load schedules");
        }
        return "schedules";
    }
}
