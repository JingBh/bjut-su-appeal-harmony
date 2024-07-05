package tech.bjut.appeal.ui.slice;

import ohos.aafwk.ability.fraction.FractionAbility;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.Image;
import ohos.agp.components.LayoutScatter;
import ohos.agp.components.Text;
import ohos.agp.components.element.Element;
import ohos.agp.components.element.VectorElement;
import ohos.agp.utils.Color;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import tech.bjut.appeal.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import tech.bjut.appeal.ui.util.NavbarItem;

import java.util.stream.IntStream;

public class MainSlice extends AbilitySlice {

    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0, "MainAbility");

    int activeNav = 3;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        this.setUIContent(ResourceTable.Layout_slice_main);

        initNavbar();
    }

    private void initNavbar() {
        ComponentContainer navbar = (ComponentContainer) this.findComponentById(ResourceTable.Id_main_navbar);
        IntStream.range(0, NavbarItem.ITEMS.length).forEachOrdered(i -> {
            final NavbarItem item = NavbarItem.ITEMS[i];

            ComponentContainer container = (ComponentContainer) LayoutScatter.getInstance(this)
                .parse(ResourceTable.Layout_component_navbar_item, navbar, false);
            Text text = (Text) container.findComponentById(ResourceTable.Id_navbar_item_text);
            text.setText(item.getTextId());
            updateNavItem(i, container, i == activeNav);

            container.setClickedListener(component -> {
                if (i == activeNav) return;
                updateNavItem(activeNav, false);
                updateNavItem(i, true);
                activeNav = i;
                updateFraction();
            });

            navbar.addComponent(container);
        });

        updateFraction();
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

        Element iconElement = new VectorElement(
            this.getContext(),
            active ? NavbarItem.ITEMS[index].getActiveImageId() : NavbarItem.ITEMS[index].getInactiveImageId()
        );
        icon.setImageElement(iconElement);
        text.setTextColor(active ? activeColor : inactiveColor);
    }

    private void updateFraction() {
        FractionAbility ability = (FractionAbility) this.getAbility();
        ability.getFractionManager().startFractionScheduler()
            .replace(ResourceTable.Id_main_content, NavbarItem.ITEMS[activeNav].getFraction())
            .submit();
    }
}
