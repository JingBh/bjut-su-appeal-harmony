package tech.bjut.appeal.data.model;

import java.util.List;

public class QuestionRequestDto {

    private String uid;

    private String name;

    private String contact;

    private CampusEnum campus;

    private String content;

    private List<String> attachmentIds;

    public QuestionRequestDto() {}

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public CampusEnum getCampus() {
        return campus;
    }

    public String getContent() {
        return content;
    }

    public List<String> getAttachmentIds() {
        return attachmentIds;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setCampus(CampusEnum campus) {
        this.campus = campus;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAttachmentIds(List<String> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }
}
