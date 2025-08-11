package org.example.controller;

import org.example.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AIController {

    @Autowired
    private AIService aiService;

    @GetMapping("/ai-recommendations")
    public String aiRecommendations() {
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
        return "ai-recommendations";
    }
}
