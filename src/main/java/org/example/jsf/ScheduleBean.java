package org.example.jsf;

import org.example.entity.StudySchedule;
import org.example.service.StudyScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;

@Component
@ViewScoped
public class ScheduleBean implements Serializable {

    @Autowired
    private StudyScheduleService scheduleService;

    private List<StudySchedule> schedules;
    private StudySchedule selectedSchedule;
    private String filterSubject;

    @PostConstruct
    public void init() {
        loadSchedules();
    }

    public void loadSchedules() {
        try {
            if (filterSubject != null && !filterSubject.trim().isEmpty()) {
                schedules = scheduleService.getSchedulesBySubject(filterSubject);
            } else {
                schedules = scheduleService.getAllSchedules();
            }
        } catch (Exception e) {
            schedules = List.of();
        }
    }

    public void filterBySubject() {
        loadSchedules();
    }

    public void selectSchedule(StudySchedule schedule) {
        this.selectedSchedule = schedule;
    }

    // Getters and Setters
    public List<StudySchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<StudySchedule> schedules) {
        this.schedules = schedules;
    }

    public StudySchedule getSelectedSchedule() {
        return selectedSchedule;
    }

    public void setSelectedSchedule(StudySchedule selectedSchedule) {
        this.selectedSchedule = selectedSchedule;
    }

    public String getFilterSubject() {
        return filterSubject;
    }

    public void setFilterSubject(String filterSubject) {
        this.filterSubject = filterSubject;
    }
}
