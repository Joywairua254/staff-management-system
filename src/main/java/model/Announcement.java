package model;

import java.time.LocalDateTime;

public class Announcement {
    private int announcementId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String postedBy;

    public Announcement() {}

    public Announcement(int announcementId, String title, String content, LocalDateTime createdAt, String postedBy) {
        this.announcementId = announcementId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.postedBy = postedBy;
    }

    // Getters and Setters
    public int getAnnouncementId() { return announcementId; }
    public void setAnnouncementId(int announcementId) { this.announcementId = announcementId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }
}
