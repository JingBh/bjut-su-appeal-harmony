package tech.bjut.appeal.constant;

import tech.bjut.appeal.ResourceTable;
import tech.bjut.appeal.ui.util.NavbarItem;

public class NavbarConstant {

    public static final NavbarItem[] ITEMS = {
            new NavbarItem(
                    ResourceTable.String_nav_announcements,
                    ResourceTable.Graphic_bi_megaphone_active,
                    ResourceTable.Graphic_bi_megaphone_inactive
            ),
            new NavbarItem(
                    ResourceTable.String_nav_question,
                    ResourceTable.Graphic_bi_pencil_square_active,
                    ResourceTable.Graphic_bi_pencil_square_inactive
            ),
            new NavbarItem(
                    ResourceTable.String_nav_answers,
                    ResourceTable.Graphic_bi_chat_left_text_active,
                    ResourceTable.Graphic_bi_chat_left_text_inactive
            ),
            new NavbarItem(
                    ResourceTable.String_nav_profile,
                    ResourceTable.Graphic_bi_person_circle_active,
                    ResourceTable.Graphic_bi_person_circle_inactive
            )
    };
}
