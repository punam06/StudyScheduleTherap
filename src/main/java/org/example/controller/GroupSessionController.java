package org.example.controller;

import org.example.entity.GroupSession;
import org.example.entity.StudyGroup;
import org.example.service.GroupSessionService;
import org.example.service.StudyGroupService;
import org.example.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/sessions")
public class GroupSessionController {

    @Autowired
    private GroupSessionService sessionService;

    @Autowired
    private StudyGroupService groupService;

    @Autowired
    private NotificationService notificationService;

    // Web UI endpoints
    @GetMapping
    public String listSessions(Model model) {
        List<GroupSession> upcomingSessions = sessionService.getUpcomingSessions();
        List<GroupSession> conflictSessions = sessionService.getSessionsWithConflicts();

        model.addAttribute("upcomingSessions", upcomingSessions);
        model.addAttribute("conflictSessions", conflictSessions);
        model.addAttribute("allSessions", sessionService.getAllSessions());

        return "sessions/list";
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(required = false) Long groupId, Model model) {
        GroupSession session = new GroupSession();
        if (groupId != null) {
            Optional<StudyGroup> group = groupService.getGroupById(groupId);
            group.ifPresent(session::setStudyGroup);
        }

        model.addAttribute("session", session);
        model.addAttribute("groups", groupService.getAllGroups());
        model.addAttribute("sessionTypes", GroupSession.SessionType.values());

        return "sessions/form";
    }

    @PostMapping("/create")
    public String createSession(@ModelAttribute GroupSession session,
                               @RequestParam String scheduledDate,
                               @RequestParam String scheduledTime,
                               @RequestParam Long groupId,
                               RedirectAttributes redirectAttributes) {
        try {
            // Parse date and time
            String dateTimeString = scheduledDate + "T" + scheduledTime;
            LocalDateTime scheduledDateTime = LocalDateTime.parse(dateTimeString);
            session.setScheduledTime(scheduledDateTime);

            // Set study group
            Optional<StudyGroup> group = groupService.getGroupById(groupId);
            if (group.isPresent()) {
                session.setStudyGroup(group.get());

                GroupSession savedSession = sessionService.createSession(session);

                // Create notifications for all group members
                if (savedSession.getStudyGroup() != null && savedSession.getStudyGroup().getMembers() != null) {
                    savedSession.getStudyGroup().getMembers().forEach(member -> {
                        notificationService.createSessionCreatedNotification(savedSession, member);
                        notificationService.createSessionReminder(savedSession, member);
                    });
                }

                if (savedSession.hasConflicts()) {
                    redirectAttributes.addFlashAttribute("warningMessage",
                        "Session created with conflicts: " + String.join(", ", savedSession.getConflicts()));
                } else {
                    redirectAttributes.addFlashAttribute("successMessage",
                        "Session '" + savedSession.getTitle() + "' created successfully!");
                }

                return "redirect:/sessions/" + savedSession.getId();
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Study group not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Error creating session: " + e.getMessage());
        }

        return "redirect:/sessions";
    }

    @GetMapping("/{id}")
    public String viewSession(@PathVariable Long id, Model model) {
        Optional<GroupSession> sessionOpt = sessionService.getSessionById(id);
        if (sessionOpt.isEmpty()) {
            return "redirect:/sessions?error=Session not found";
        }

        GroupSession session = sessionOpt.get();
        model.addAttribute("session", session);

        // Add analytics for the group
        Long groupId = session.getStudyGroup().getId();
        model.addAttribute("analytics", sessionService.getSessionAnalytics(groupId));

        return "sessions/detail";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<GroupSession> sessionOpt = sessionService.getSessionById(id);
        if (sessionOpt.isEmpty()) {
            return "redirect:/sessions?error=Session not found";
        }

        GroupSession session = sessionOpt.get();
        model.addAttribute("session", session);
        model.addAttribute("groups", groupService.getAllGroups());
        model.addAttribute("sessionTypes", GroupSession.SessionType.values());

        // Format date and time for form
        if (session.getScheduledTime() != null) {
            model.addAttribute("scheduledDate",
                session.getScheduledTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            model.addAttribute("scheduledTime",
                session.getScheduledTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        return "sessions/form";
    }

    @PostMapping("/{id}/update")
    public String updateSession(@PathVariable Long id,
                               @ModelAttribute GroupSession session,
                               @RequestParam String scheduledDate,
                               @RequestParam String scheduledTime,
                               RedirectAttributes redirectAttributes) {
        try {
            String dateTimeString = scheduledDate + "T" + scheduledTime;
            LocalDateTime scheduledDateTime = LocalDateTime.parse(dateTimeString);
            session.setScheduledTime(scheduledDateTime);
            session.setId(id);

            GroupSession updatedSession = sessionService.updateSession(session);
            redirectAttributes.addFlashAttribute("successMessage", "Session updated successfully!");

            return "redirect:/sessions/" + updatedSession.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Error updating session: " + e.getMessage());
            return "redirect:/sessions/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/force-schedule")
    public String forceScheduleSession(@PathVariable Long id,
                                      @RequestParam String reason,
                                      RedirectAttributes redirectAttributes) {
        try {
            Optional<GroupSession> sessionOpt = sessionService.getSessionById(id);
            if (sessionOpt.isPresent()) {
                GroupSession forcedSession = sessionService.forceScheduleSession(sessionOpt.get(), reason);
                redirectAttributes.addFlashAttribute("successMessage",
                    "Session force-scheduled successfully!");
                return "redirect:/sessions/" + forcedSession.getId();
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Session not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Error force-scheduling session: " + e.getMessage());
        }

        return "redirect:/sessions/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteSession(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            sessionService.deleteSession(id);
            redirectAttributes.addFlashAttribute("successMessage", "Session deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Error deleting session: " + e.getMessage());
        }

        return "redirect:/sessions";
    }

    // REST API endpoints
    @GetMapping("/api/upcoming")
    @ResponseBody
    public List<GroupSession> getUpcomingSessionsAPI() {
        return sessionService.getUpcomingSessions();
    }

    @GetMapping("/api/group/{groupId}")
    @ResponseBody
    public List<GroupSession> getGroupSessionsAPI(@PathVariable Long groupId) {
        return sessionService.getSessionsByGroup(groupId);
    }

    @GetMapping("/api/student/{studentId}")
    @ResponseBody
    public List<GroupSession> getStudentSessionsAPI(@PathVariable Long studentId) {
        return sessionService.getStudentSessions(studentId);
    }

    @GetMapping("/api/conflicts")
    @ResponseBody
    public List<GroupSession> getConflictSessionsAPI() {
        return sessionService.getSessionsWithConflicts();
    }

    @PostMapping("/api/group/{groupId}/suggest-times")
    @ResponseBody
    public List<LocalDateTime> suggestOptimalTimesAPI(@PathVariable Long groupId,
                                                     @RequestParam int durationMinutes,
                                                     @RequestParam(defaultValue = "5") int suggestions) {
        return sessionService.suggestOptimalTimes(groupId, durationMinutes, suggestions);
    }

    @GetMapping("/api/group/{groupId}/analytics")
    @ResponseBody
    public Map<String, Object> getSessionAnalyticsAPI(@PathVariable Long groupId) {
        return sessionService.getSessionAnalytics(groupId);
    }

    @PostMapping("/api/{id}/force-schedule")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> forceScheduleSessionAPI(@PathVariable Long id,
                                                                      @RequestParam String reason) {
        try {
            Optional<GroupSession> sessionOpt = sessionService.getSessionById(id);
            if (sessionOpt.isPresent()) {
                GroupSession forcedSession = sessionService.forceScheduleSession(sessionOpt.get(), reason);

                Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Session force-scheduled successfully",
                    "sessionId", forcedSession.getId()
                );

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Session not found"
                );
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = Map.of(
                "success", false,
                "message", "Error: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(response);
        }
    }
}
