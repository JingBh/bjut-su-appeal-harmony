package tech.bjut.appeal.data.model;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CursorPaginationDto<T> {

    @Nullable
    private String cursor;

    private List<T> data;

    @Nullable
    private List<T> pinned;

    public CursorPaginationDto() {}

    @Nullable
    public String getCursor() {
        return cursor;
    }

    public List<T> getData() {
        return data;
    }

    @Nullable
    public List<T> getPinned() {
        return pinned;
    }

    public void setCursor(@Nullable String cursor) {
        this.cursor = cursor;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public void setPinned(@Nullable List<T> pinned) {
        this.pinned = pinned;
    }
}
