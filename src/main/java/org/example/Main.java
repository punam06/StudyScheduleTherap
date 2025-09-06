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
        System.out.println("🚀 Starting Study Schedule Therapy Application...");
        System.out.println("📚 Java Fest 2025 - AI-Powered Study Planning Portal");
        SpringApplication.run(Main.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("✅ Application successfully started!");
        System.out.println("🌐 Access the application at: http://localhost:8080");
        System.out.println("🔍 Health check available at: http://localhost:8080/actuator/health");
        System.out.println("📊 H2 Database console: http://localhost:8080/h2-console");
        System.out.println("🤖 Features available:");
        System.out.println("   • Study Schedule Management");
        System.out.println("   • AI-Powered Recommendations");
        System.out.println("   • Dashboard & Analytics");
        System.out.println("   • Responsive UI");
    }

    @GetMapping("/api/status")
    public Map<String, Object> getApplicationStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("application", "Study Schedule Therapy");
        status.put("version", "1.0.0");
        status.put("status", "RUNNING");
        status.put("timestamp", LocalDateTime.now());
        status.put("features", new String[]{
            "Study Schedule Management",
            "AI-Powered Recommendations",
            "Dashboard & Analytics",
            "Responsive Design"
        });
        status.put("competition", "Java Fest 2025");
        status.put("category", "Educational Technology");
        return status;
    }
}