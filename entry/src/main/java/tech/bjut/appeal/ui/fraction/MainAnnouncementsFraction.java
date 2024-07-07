package tech.bjut.appeal.ui.fraction;

import ohos.aafwk.ability.fraction.Fraction;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.LayoutScatter;
import ohos.agp.components.Text;
import tech.bjut.appeal.ResourceTable;

public class MainAnnouncementsFraction extends Fraction {

    @Override
    protected Component onComponentAttached(LayoutScatter scatter, ComponentContainer container, Intent intent) {
        Component component = scatter.parse(ResourceTable.Layout_slice_placeholder, container, false);

        Text text = component.findComponentById(ResourceTable.Id_placeholder_text);
        text.setText(ResourceTable.String_nav_announcements);

        return component;
    }
}
