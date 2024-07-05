package tech.bjut.appeal.ui.util;

import ohos.aafwk.ability.fraction.Fraction;
import tech.bjut.appeal.ResourceTable;
import tech.bjut.appeal.ui.fraction.MainAnnouncementsFraction;
import tech.bjut.appeal.ui.fraction.MainAnswersFraction;
import tech.bjut.appeal.ui.fraction.MainProfileFraction;
import tech.bjut.appeal.ui.fraction.MainQuestionFraction;

public class NavbarItem {

    private final Fraction fraction;

    private final int textId;

    private final int activeImageId;

    private final int inactiveImageId;

    public NavbarItem(Fraction fraction, int textId, int activeImageId, int inactiveImageId) {
        this.fraction = fraction;
        this.textId = textId;
        this.activeImageId = activeImageId;
        this.inactiveImageId = inactiveImageId;
    }

    public Fraction getFraction() {
        return fraction;
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

    public static final NavbarItem[] ITEMS = {
        new NavbarItem(
            new MainAnnouncementsFraction(),
            ResourceTable.String_nav_announcements,
            ResourceTable.Graphic_bi_megaphone_active,
            ResourceTable.Graphic_bi_megaphone_inactive
        ),
        new NavbarItem(
            new MainQuestionFraction(),
            ResourceTable.String_nav_question,
            ResourceTable.Graphic_bi_pencil_square_active,
            ResourceTable.Graphic_bi_pencil_square_inactive
        ),
        new NavbarItem(
            new MainAnswersFraction(),
            ResourceTable.String_nav_answers,
            ResourceTable.Graphic_bi_chat_left_text_active,
            ResourceTable.Graphic_bi_chat_left_text_inactive
        ),
        new NavbarItem(
            new MainProfileFraction(),
            ResourceTable.String_nav_profile,
            ResourceTable.Graphic_bi_person_circle_active,
            ResourceTable.Graphic_bi_person_circle_inactive
        )
    };
}
