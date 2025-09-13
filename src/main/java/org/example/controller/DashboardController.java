package org.example.controller;

import org.example.entity.GroupSession;
import org.example.entity.Student;
import org.example.entity.StudyGroup;
import org.example.service.GroupSessionService;
import org.example.service.StudentService;
import org.example.service.StudyGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudyGroupService groupService;

    @Autowired
    private GroupSessionService sessionService;

    @GetMapping
    public String showDashboard(HttpSession session, Model model) {
        Student currentUser = (Student) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // Refresh user data
        Student refreshedUser = studentService.getStudentById(currentUser.getId())
                .orElse(currentUser);
        session.setAttribute("currentUser", refreshedUser);

        // Get user's study groups
        List<StudyGroup> userGroups = refreshedUser.getStudyGroups();

        // Get upcoming sessions for user's groups
        List<GroupSession> upcomingSessions = sessionService.getUpcomingSessionsForStudent(refreshedUser.getId());

        // Get recent sessions
        List<GroupSession> recentSessions = sessionService.getRecentSessionsForStudent(refreshedUser.getId());

        // Get recommended groups
        List<StudyGroup> recommendedGroups = groupService.getRecommendedGroupsForStudent(refreshedUser);

        // Add data to model
        model.addAttribute("user", refreshedUser);
        model.addAttribute("userGroups", userGroups);
        model.addAttribute("upcomingSessions", upcomingSessions);
        model.addAttribute("recentSessions", recentSessions);
        model.addAttribute("recommendedGroups", recommendedGroups);
        model.addAttribute("totalGroups", userGroups.size());
        model.addAttribute("totalSessions", upcomingSessions.size() + recentSessions.size());

        return "dashboard/index";
    }
}
