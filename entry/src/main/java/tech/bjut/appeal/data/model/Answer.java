package tech.bjut.appeal.data.model;

import java.util.List;
import java.util.stream.Collectors;

public class Answer {

    private long id;

    private String content;

    private List<Attachment> attachments;

    private long likesCount;

    private String createdAt;

    private String updatedAt;

    public Answer() {}

    public long getId() {
        return id;
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

    public long getLikesCount() {
        return likesCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
