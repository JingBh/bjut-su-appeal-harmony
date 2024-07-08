package tech.bjut.appeal.data.model;

public class Attachment {

    private String id;

    private String name;

    private long size;

    public Attachment() {}

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public boolean isImage() {
        return name.endsWith(".jpg")
            || name.endsWith(".jpeg")
            || name.endsWith(".png")
            || name.endsWith(".gif");
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
