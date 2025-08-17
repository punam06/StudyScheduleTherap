package org.example.controller;

import org.example.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class AIController {

    @Autowired
    private AIService aiService;

    @GetMapping("/ai-recommendations")
    public String aiRecommendations(Model model) {
        model.addAttribute("optimalTimes", aiService.getOptimalStudyTimes());
        return "ai-recommendations";
    }

    @PostMapping("/get-recommendation")
    public String getRecommendation(@RequestParam String subject,
                                  @RequestParam int studyHours,
                                  Model model) {
        String recommendation = aiService.getSimpleRecommendation(subject, studyHours);
        model.addAttribute("recommendation", recommendation);
        model.addAttribute("subject", subject);
        model.addAttribute("studyHours", studyHours);
        model.addAttribute("optimalTimes", aiService.getOptimalStudyTimes());
        return "ai-recommendations";
    }

    @PostMapping("/get-detailed-recommendation")
    public String getDetailedRecommendation(@RequestParam String subject,
                                          @RequestParam int currentHours,
                                          @RequestParam String difficulty,
                                          @RequestParam String goalType,
                                          Model model) {
        Map<String, Object> detailedRecommendation = aiService.getDetailedStudyRecommendation(
            subject, currentHours, difficulty, goalType);

        Map<String, String> subjectAdvice = aiService.getSubjectSpecificAdvice(subject);

        model.addAttribute("detailedRecommendation", detailedRecommendation);
        model.addAttribute("subjectAdvice", subjectAdvice);
        model.addAttribute("subject", subject);
        model.addAttribute("currentHours", currentHours);
        model.addAttribute("difficulty", difficulty);
        model.addAttribute("goalType", goalType);
        model.addAttribute("optimalTimes", aiService.getOptimalStudyTimes());

        return "ai-recommendations";
    }

    @PostMapping("/get-subject-advice")
    public String getSubjectAdvice(@RequestParam String subject, Model model) {
        Map<String, String> advice = aiService.getSubjectSpecificAdvice(subject);

        model.addAttribute("subjectAdvice", advice);
        model.addAttribute("selectedSubject", subject);
        model.addAttribute("optimalTimes", aiService.getOptimalStudyTimes());

        return "ai-recommendations";
    }
}
