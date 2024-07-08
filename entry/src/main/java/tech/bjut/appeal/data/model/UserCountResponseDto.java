package tech.bjut.appeal.data.model;

public class UserCountResponseDto {
    private long history;

    private long unreplied;

    public UserCountResponseDto() {}

    public long getHistory() {
        return history;
    }

    public long getUnreplied() {
        return unreplied;
    }

    public void setHistory(long history) {
        this.history = history;
    }

    public void setUnreplied(long unreplied) {
        this.unreplied = unreplied;
    }
}
