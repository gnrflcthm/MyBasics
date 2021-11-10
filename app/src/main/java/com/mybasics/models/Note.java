package com.mybasics.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Note implements Serializable {
    private String id;
    private String title;
    private String content;
    private LocalDateTime dateCreated;
    private LocalDateTime dateLastModified;

    private boolean isSelected;

    public Note() {}

    public Note(String id, String title, String content, LocalDateTime dateCreated, LocalDateTime dateLastModified) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.dateCreated = dateCreated;
        this.dateLastModified = dateLastModified;
        this.isSelected = false;
    }

    public Note(String content) {
        this(UUID.randomUUID().toString(), "Untitled Note", content, LocalDateTime.now(), LocalDateTime.now());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(LocalDateTime dateLastModified) {
        this.dateLastModified = dateLastModified;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void toggleSelected() {
        isSelected = !isSelected;
    }

    public static Note newInstance(String id, String title, String content, LocalDateTime dateCreated, LocalDateTime dateLastModified) {
        return new Note(id, title, content, dateCreated, dateLastModified);
    }

    public static Note newInstance(String content) {
        return new Note(content);
    }
}
