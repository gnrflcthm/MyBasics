package com.mybasics.models;

import java.util.UUID;

public class Todo {
    private String id;
    private String content;
    private boolean completed;

    public Todo() {}

    public Todo(String id, String content, boolean completed) {
        this.id = id;
        this.content = content;
        this.completed = completed;
    }

    public Todo(String content) {
        this(UUID.randomUUID().toString(), content, false);
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

    public static Todo newInstance(String id, String content, boolean completed) {
        return new Todo(id, content, completed);
    }

    public static Todo newInstance(String content) {
        return new Todo(content);
    }
}
