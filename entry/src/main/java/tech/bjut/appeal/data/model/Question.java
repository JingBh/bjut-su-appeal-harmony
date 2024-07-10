package tech.bjut.appeal.data.model;

import java.util.List;
import java.util.stream.Collectors;

public class Question {

    private long id;

    private CampusEnum campus;

    private String content;

    private List<Attachment> attachments;

    private Answer answer;

    private boolean published;

    private String createdAt;

    private String updatedAt;

    public Question() {}

    public long getId() {
        return id;
    }

    public CampusEnum getCampus() {
        return campus;
    }

    public String getContent() {
        return content.trim();
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public Answer getAnswer() {
        return answer;
    }

    public boolean isPublished() {
        return published;
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

    public void setCampus(CampusEnum campus) {
        this.campus = campus;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Attachment> getImageAttachments() {
        return attachments.stream().filter(Attachment::isImage).collect(Collectors.toList());
    }
}
