package tech.bjut.appeal.ui.fraction;

import ohos.aafwk.ability.fraction.Fraction;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import tech.bjut.appeal.ResourceTable;
import tech.bjut.appeal.ui.util.AboutUtil;
import tech.bjut.appeal.ui.ability.MainAbility;
import tech.bjut.appeal.ui.slice.LoginSlice;

public class MainProfileFraction extends Fraction {

    @Override
    protected Component onComponentAttached(LayoutScatter scatter, ComponentContainer container, Intent intent) {
        Component component = scatter.parse(ResourceTable.Layout_slice_profile, container, false);

        Image avatar = component.findComponentById(ResourceTable.Id_profile_avatar);
        avatar.setCornerRadius((float) avatar.getWidth() / 2);

        Text name = component.findComponentById(ResourceTable.Id_profile_name);
        name.setText(ResourceTable.String_profile_anonymous);

        Text uid = component.findComponentById(ResourceTable.Id_profile_uid);
        uid.setText("anonymous");

        Component loginButton = component.findComponentById(ResourceTable.Id_profile_login);
        loginButton.setClickedListener(comp -> {
            MainAbility ability = (MainAbility) getFractionAbility();
            ability.getCurrentSlice().present(new LoginSlice(), new Intent());
        });

        Text appVersion = component.findComponentById(ResourceTable.Id_profile_version_app);
        appVersion.setText(this.getString(ResourceTable.String_profile_version_app) + AboutUtil.appVersion(this));

        Text serviceVersion = component.findComponentById(ResourceTable.Id_profile_version_service);
        serviceVersion.setText(this.getString(ResourceTable.String_profile_version_service) + this.getString(ResourceTable.String_profile_version_loading));
        AboutUtil.serviceVersion(this.getApplicationContext(), version -> {
            try {
                serviceVersion.setText(this.getString(ResourceTable.String_profile_version_service) + version);
            } catch (Exception ignored) {}
        });

        return component;
    }
}
