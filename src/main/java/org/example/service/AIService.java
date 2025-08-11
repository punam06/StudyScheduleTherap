package org.example.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AIService {

    private final WebClient webClient;

    public AIService() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8085") // AI model service URL
                .build();
    }

    public Mono<String> getStudyRecommendation(String studentData) {
        return webClient.post()
                .uri("/prediction")
                .bodyValue(studentData)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("Unable to get AI recommendation at this time");
    }

    public String getSimpleRecommendation(String subject, int studyHours) {
        // Simple AI-like logic for demonstration
        if (studyHours < 2) {
            return "Recommended: Increase study time for " + subject + " to at least 2 hours daily";
        } else if (studyHours > 6) {
            return "Recommended: Take breaks! Consider 4-6 hours for " + subject + " with rest periods";
        } else {
            return "Good study schedule for " + subject + "! Consider adding practice sessions";
        }
    }
}
