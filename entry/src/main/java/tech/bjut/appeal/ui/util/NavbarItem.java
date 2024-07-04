package tech.bjut.appeal.ui.util;

public class NavbarItem {

    private final int textId;

    private final int activeImageId;

    private final int inactiveImageId;

    public NavbarItem(int textId, int activeImageId, int inactiveImageId) {
        this.textId = textId;
        this.activeImageId = activeImageId;
        this.inactiveImageId = inactiveImageId;
    }

    public int getTextId() {
        return textId;
    }

    public int getActiveImageId() {
        return activeImageId;
    }

    public int getInactiveImageId() {
        return inactiveImageId;
    }
}
