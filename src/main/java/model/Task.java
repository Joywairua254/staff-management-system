package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Task implements Serializable {
    private int taskId;
    private String description;
    private LocalDateTime deadline;
    private boolean completed;
    private LocalDateTime createdAt;
    private String document;
    private String docName;
    private String adminComment;
    private String staffComment;

    public Task() {}

    public Task(int taskId, String description, LocalDateTime deadline, boolean completed, LocalDateTime createdAt) {
        this.taskId = taskId;
        this.description = description;
        this.deadline = deadline;
        this.completed = completed;
        this.createdAt = createdAt;
    }

    public Task(int taskId, String description, LocalDateTime deadline, boolean completed, LocalDateTime createdAt, String document, String docName) {
        this.taskId = taskId;
        this.description = description;
        this.deadline = deadline;
        this.completed = completed;
        this.createdAt = createdAt;
        this.document = document;
        this.docName = docName;
    }

    public Task(int taskId, String description, LocalDateTime deadline, boolean completed, LocalDateTime createdAt, String document, String docName, String adminComment, String staffComment) {
        this.taskId = taskId;
        this.description = description;
        this.deadline = deadline;
        this.completed = completed;
        this.createdAt = createdAt;
        this.document = document;
        this.docName = docName;
        this.adminComment = adminComment;
        this.staffComment = staffComment;
    }

    // Getters and Setters
    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDocument() { return document; }
    public void setDocument(String document) { this.document = document; }

    public String getDocName() { return docName; }
    public void setDocName(String docName) { this.docName = docName; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getAdminComment() { return adminComment; }
    public void setAdminComment(String adminComment) { this.adminComment = adminComment; }

    public String getStaffComment() { return staffComment; }
    public void setStaffComment(String staffComment) { this.staffComment = staffComment; }
}
