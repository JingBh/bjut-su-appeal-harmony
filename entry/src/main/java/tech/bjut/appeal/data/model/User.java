package tech.bjut.appeal.data.model;

public class User {

    private long id;

    private String name;

    private String uid;

    public User() {}

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
