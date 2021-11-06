package com.mybasics.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Todo {
    private String id;
    private String content;
    private boolean completed;
    private LocalDateTime dateAdded;

    public Todo() {}

    public Todo(String id, String content, boolean completed, LocalDateTime dateAdded) {
        this.id = id;
        this.content = content;
        this.completed = completed;
        this.dateAdded = dateAdded;
    }

    public Todo(String content) {
        this(UUID.randomUUID().toString(), content, false, LocalDateTime.now());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    public static Todo newInstance(String id, String content, boolean completed, LocalDateTime dateAdded) {
        return new Todo(id, content, completed, dateAdded);
    }

    public static Todo newInstance(String content) {
        return new Todo(content);
    }
}
