package tech.bjut.appeal.data.model;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AnnouncementsResponseDto {

    @Nullable
    private String cursor;

    private List<Announcement> data;

    @Nullable
    private List<Announcement> pinned;

    public AnnouncementsResponseDto() {}

    @Nullable
    public String getCursor() {
        return cursor;
    }

    public List<Announcement> getData() {
        return data;
    }

    @Nullable
    public List<Announcement> getPinned() {
        return pinned;
    }

    public void setCursor(@Nullable String cursor) {
        this.cursor = cursor;
    }

    public void setData(List<Announcement> data) {
        this.data = data;
    }

    public void setPinned(@Nullable List<Announcement> pinned) {
        this.pinned = pinned;
    }
}
