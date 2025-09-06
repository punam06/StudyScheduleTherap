package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class Main {

    public static void main(String[] args) {
        System.out.println("🚀 Starting অধ্যয়ন সঙ্ঘ (Study Association) Application...");
        System.out.println("📚 Java Fest 2025 - AI-Powered Study Planning Portal");
        SpringApplication.run(Main.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("✅ অধ্যয়ন সঙ্ঘ successfully started!");
        System.out.println("🌐 Access the application at: http://localhost:8080");
        System.out.println("🔍 Health check available at: http://localhost:8080/actuator/health");
        System.out.println("📊 H2 Database console: http://localhost:8080/h2-console");
        System.out.println("🤖 Features available:");
        System.out.println("   • AI-Enhanced Study Schedule Management");
        System.out.println("   • Smart Group Matching & Collaboration");
        System.out.println("   • Predictive Analytics & Natural Language Scheduling");
        System.out.println("   • Group Dynamics Insights & Spaced Repetition AI");
    }

    @GetMapping("/api/status")
    public Map<String, Object> getApplicationStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("application", "অধ্যয়ন সঙ্ঘ (Study Association)");
        status.put("version", "2.0.0-AI-Enhanced");
        status.put("status", "RUNNING");
        status.put("timestamp", LocalDateTime.now());
        status.put("features", new String[]{
            "AI-Enhanced Study Schedule Management",
            "Smart Group Matching & Collaboration",
            "Predictive Analytics Dashboard",
            "Natural Language Scheduling",
            "Group Dynamics Insights",
            "Spaced Repetition AI",
            "Collaborative Content Generation"
        });
        status.put("competition", "Java Fest 2025");
        status.put("category", "Educational Technology - AI Enhanced");
        status.put("language", "Bengali (অধ্যয়ন সঙ্ঘ) + English");
        return status;
    }
}