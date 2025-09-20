package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.study")
public class StudyProperties {

    private int defaultSessionDuration = 75;
    private int maxSessionDuration = 180;
    private int minSessionDuration = 30;
    private int breakInterval = 15;

    // Getters and Setters
    public int getDefaultSessionDuration() {
        return defaultSessionDuration;
    }

    public void setDefaultSessionDuration(int defaultSessionDuration) {
        this.defaultSessionDuration = defaultSessionDuration;
    }

    public int getMaxSessionDuration() {
        return maxSessionDuration;
    }

    public void setMaxSessionDuration(int maxSessionDuration) {
        this.maxSessionDuration = maxSessionDuration;
    }

    public int getMinSessionDuration() {
        return minSessionDuration;
    }

    public void setMinSessionDuration(int minSessionDuration) {
        this.minSessionDuration = minSessionDuration;
    }

    public int getBreakInterval() {
        return breakInterval;
    }

    public void setBreakInterval(int breakInterval) {
        this.breakInterval = breakInterval;
    }
}
