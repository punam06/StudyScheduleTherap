package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, @RequestParam(value = "lang", required = false) String lang) {
        // Set language-specific attributes if needed
        if ("bn".equals(lang)) {
            model.addAttribute("currentLang", "bn");
            model.addAttribute("isRTL", false); // Bengali is LTR like English
        } else {
            model.addAttribute("currentLang", "en");
            model.addAttribute("isRTL", false);
        }

        model.addAttribute("message", "Welcome to Oddhoyon Songho - অধ্যয়ন সঙ্ঘ");
        return "home-bilingual"; // Use the new bilingual template
    }

    @GetMapping("/about")
    public String about(Model model, @RequestParam(value = "lang", required = false) String lang) {
        model.addAttribute("title", "About Our Platform");
        model.addAttribute("currentLang", lang != null ? lang : "en");
        return "about";
    }

    @GetMapping("/language")
    public String changeLanguage(@RequestParam("lang") String lang,
                                @RequestParam(value = "redirect", required = false, defaultValue = "/") String redirectUrl) {
        // Language switching endpoint
        return "redirect:" + redirectUrl + "?lang=" + lang;
    }
}
