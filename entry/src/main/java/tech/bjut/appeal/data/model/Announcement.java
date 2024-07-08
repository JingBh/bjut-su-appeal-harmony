package tech.bjut.appeal.data.model;

import java.util.List;
import java.util.stream.Collectors;

public class Announcement {

    private long id;

    private String title;

    private String content;

    private List<Attachment> attachments;

    private boolean pinned;

    private String createdAt;

    private String updatedAt;

    private User user;

    public Announcement() {}

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title.trim();
    }

    public String getContent() {
        return content.trim();
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public List<Attachment> getImageAttachments() {
        return attachments.stream().filter(Attachment::isImage).collect(Collectors.toList());
    }

    public boolean isPinned() {
        return pinned;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
