package com.mybasics.models;

import java.time.LocalDateTime;

public class Reminder {

    private int id;
    private String title;
    private LocalDateTime schedule;

    public Reminder() {}

    public Reminder(String title, LocalDateTime schedule) {
        this.title = title;
        this.schedule = schedule;
    }

    public Reminder(int id, String title, LocalDateTime schedule) {
        this.id = id;
        this.title = title;
        this.schedule = schedule;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getSchedule() {
        return schedule;
    }

    public void setSchedule(LocalDateTime schedule) {
        this.schedule = schedule;
    }

    public static Reminder newInstance(String title, LocalDateTime schedule) {
        return new Reminder(title, schedule);
    }

    public static Reminder newInstance(int id, String title, LocalDateTime schedule) {
        return new Reminder(id, title, schedule);
    }
}
