package tech.bjut.appeal.ui.ability;

import ohos.aafwk.ability.fraction.FractionAbility;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.components.element.Element;
import ohos.agp.components.element.VectorElement;
import ohos.agp.utils.Color;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import tech.bjut.appeal.ResourceTable;
import tech.bjut.appeal.constant.NavbarConstant;
import tech.bjut.appeal.ui.util.NavbarItem;

import java.util.Arrays;
import java.util.stream.IntStream;

public class MainAbility extends FractionAbility {

    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0, "MainAbility");

    int activeNav = 0;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        this.setUIContent(ResourceTable.Layout_ability_main);

        initNavbar();
        updateFraction();
    }

    private void initNavbar() {
        ComponentContainer navbar = (ComponentContainer) this.findComponentById(ResourceTable.Id_main_navbar);
        IntStream.range(0, NavbarConstant.ITEMS.length).forEachOrdered(i -> {
            final NavbarItem item = NavbarConstant.ITEMS[i];

            ComponentContainer container = (ComponentContainer) LayoutScatter.getInstance(this).parse(ResourceTable.Layout_navbar_item, navbar, false);
            Text text = (Text) container.findComponentById(ResourceTable.Id_navbar_item_text);
            text.setText(item.getTextId());
            updateNavItem(i, container, i == activeNav);

            container.setClickedListener(component -> {
                if (i == activeNav) return;
                updateNavItem(activeNav, false);
                updateNavItem(i, true);
                activeNav = i;
            });

            navbar.addComponent(container);
        });

    }

    private void updateNavItem(int index, boolean active) {
        ComponentContainer navbar = (ComponentContainer) this.findComponentById(ResourceTable.Id_main_navbar);
        ComponentContainer container = (ComponentContainer) navbar.getComponentAt(index);
        updateNavItem(index, container, active);
    }

    private void updateNavItem(int index, ComponentContainer container, boolean active) {
        Color activeColor = Color.BLUE, inactiveColor = Color.BLACK;
        try {
            activeColor = new Color(this.getResourceManager().getElement(ResourceTable.Color_navbar_active).getColor());
            inactiveColor = new Color(this.getResourceManager().getElement(ResourceTable.Color_navbar_inactive).getColor());
        } catch (Exception e) {
            HiLog.error(LABEL, "updateNavItem: %s", HiLog.getStackTrace(e));
        }

        Image icon = (Image) container.findComponentById(ResourceTable.Id_navbar_item_icon);
        Text text = (Text) container.findComponentById(ResourceTable.Id_navbar_item_text);

        Element iconElement = new VectorElement(this.getContext(), active ? NavbarConstant.ITEMS[index].getActiveImageId() : NavbarConstant.ITEMS[index].getInactiveImageId());
        icon.setImageElement(iconElement);
        text.setTextColor(active ? activeColor : inactiveColor);
    }

    private void updateFraction() {

    }
}
