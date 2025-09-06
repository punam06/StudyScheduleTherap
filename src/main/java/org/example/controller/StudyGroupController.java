package org.example.controller;

import org.example.entity.Student;
import org.example.entity.StudyGroup;
import org.example.repository.StudentRepository;
import org.example.service.StudyGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/groups")
public class StudyGroupController {

    @Autowired
    private StudyGroupService groupService;

    @Autowired
    private StudentRepository studentRepository;

    // Web UI endpoints
    @GetMapping
    public String listGroups(Model model, @RequestParam(required = false) String subject) {
        List<StudyGroup> groups;
        if (subject != null && !subject.isEmpty()) {
            groups = groupService.getGroupsBySubject(subject);
            model.addAttribute("selectedSubject", subject);
        } else {
            groups = groupService.getAllGroups();
        }

        model.addAttribute("groups", groups);
        model.addAttribute("availableGroups", groupService.getAvailableGroups());
        model.addAttribute("subjects", groupService.getAllSubjects());
        return "groups/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("group", new StudyGroup());
        model.addAttribute("students", studentRepository.findByRole(Student.UserRole.STUDENT));
        return "groups/form";
    }

    @PostMapping("/create")
    public String createGroup(@ModelAttribute StudyGroup group,
                             @RequestParam Long coordinatorId,
                             RedirectAttributes redirectAttributes) {
        try {
            Optional<Student> coordinator = studentRepository.findById(coordinatorId);
            if (coordinator.isPresent()) {
                group.setCoordinator(coordinator.get());
                group.getMembers().add(coordinator.get());
                group.setCurrentMembers(1);

                StudyGroup savedGroup = groupService.createGroup(group);
                redirectAttributes.addFlashAttribute("successMessage",
                    "Study group '" + savedGroup.getName() + "' created successfully!");
                return "redirect:/groups/" + savedGroup.getId();
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Coordinator not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Error creating group: " + e.getMessage());
        }

        return "redirect:/groups";
    }

    @GetMapping("/{id}")
    public String viewGroup(@PathVariable Long id, Model model) {
        Optional<StudyGroup> groupOpt = groupService.getGroupById(id);
        if (groupOpt.isEmpty()) {
            return "redirect:/groups?error=Group not found";
        }

        StudyGroup group = groupOpt.get();
        model.addAttribute("group", group);
        model.addAttribute("recommendations", groupService.getGroupRecommendations(id));
        model.addAttribute("optimalTimes", groupService.findOptimalMeetingTimes(id));
        model.addAttribute("availableStudents", getEligibleStudents(group));

        return "groups/detail";
    }

    @PostMapping("/{id}/join")
    public String joinGroup(@PathVariable Long id,
                           @RequestParam Long studentId,
                           RedirectAttributes redirectAttributes) {
        try {
            boolean joined = groupService.joinGroup(id, studentId);
            if (joined) {
                redirectAttributes.addFlashAttribute("successMessage", "Successfully joined the group!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                    "Unable to join group. It may be full or you may already be a member.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Error joining group: " + e.getMessage());
        }

        return "redirect:/groups/" + id;
    }

    @GetMapping("/search")
    public String searchGroups(@RequestParam String subject,
                              @RequestParam(required = false) String learningStyle,
                              @RequestParam(required = false) String availability,
                              Model model) {

        List<String> userAvailability = availability != null ?
            List.of(availability.split(",")) : List.of();

        List<StudyGroup> matchingGroups = groupService.findMatchingGroups(
            subject, learningStyle, userAvailability);

        model.addAttribute("groups", matchingGroups);
        model.addAttribute("searchSubject", subject);
        model.addAttribute("searchLearningStyle", learningStyle);
        model.addAttribute("subjects", groupService.getAllSubjects());

        return "groups/search-results";
    }

    // REST API endpoints
    @GetMapping("/api/all")
    @ResponseBody
    public List<StudyGroup> getAllGroupsAPI() {
        return groupService.getAllGroups();
    }

    @GetMapping("/api/available")
    @ResponseBody
    public List<StudyGroup> getAvailableGroupsAPI() {
        return groupService.getAvailableGroups();
    }

    @GetMapping("/api/{id}/recommendations")
    @ResponseBody
    public Map<String, Object> getGroupRecommendations(@PathVariable Long id) {
        return groupService.getGroupRecommendations(id);
    }

    @GetMapping("/api/{id}/optimal-times")
    @ResponseBody
    public List<String> getOptimalMeetingTimes(@PathVariable Long id) {
        return groupService.findOptimalMeetingTimes(id);
    }

    @PostMapping("/api/{id}/join/{studentId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> joinGroupAPI(@PathVariable Long id,
                                                           @PathVariable Long studentId) {
        try {
            boolean joined = groupService.joinGroup(id, studentId);

            Map<String, Object> response = Map.of(
                "success", joined,
                "message", joined ? "Successfully joined group" : "Unable to join group"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = Map.of(
                "success", false,
                "message", "Error: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/api/search")
    @ResponseBody
    public List<StudyGroup> searchGroupsAPI(@RequestBody Map<String, Object> searchCriteria) {
        String subject = (String) searchCriteria.get("subject");
        String learningStyle = (String) searchCriteria.get("learningStyle");

        @SuppressWarnings("unchecked")
        List<String> availability = (List<String>) searchCriteria.getOrDefault("availability", List.of());

        return groupService.findMatchingGroups(subject, learningStyle, availability);
    }

    @GetMapping("/api/student/{studentId}")
    @ResponseBody
    public List<StudyGroup> getStudentGroupsAPI(@PathVariable Long studentId) {
        return groupService.getStudentGroups(studentId);
    }

    @PostMapping("/api/{id}/conflicts")
    @ResponseBody
    public List<String> checkGroupConflicts(@PathVariable Long id,
                                           @RequestParam Long studentId) {
        Optional<StudyGroup> groupOpt = groupService.getGroupById(id);
        Optional<Student> studentOpt = studentRepository.findById(studentId);

        if (groupOpt.isPresent() && studentOpt.isPresent()) {
            return groupService.detectScheduleConflicts(studentOpt.get(), groupOpt.get());
        }

        return List.of("Group or student not found");
    }

    // Helper methods
    private List<Student> getEligibleStudents(StudyGroup group) {
        return studentRepository.findByRole(Student.UserRole.STUDENT)
                .stream()
                .filter(student -> !group.getMembers().contains(student))
                .limit(10) // Limit for performance
                .toList();
    }

    @GetMapping("/api/subjects")
    @ResponseBody
    public List<String> getAllSubjectsAPI() {
        return groupService.getAllSubjects();
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteGroupAPI(@PathVariable Long id) {
        try {
            groupService.deleteGroup(id);
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "Group deleted successfully"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = Map.of(
                "success", false,
                "message", "Error deleting group: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(response);
        }
    }
}
