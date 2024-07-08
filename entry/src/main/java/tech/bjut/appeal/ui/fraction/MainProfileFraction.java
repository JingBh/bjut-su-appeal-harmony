package tech.bjut.appeal.ui.fraction;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.LifecycleObserver;
import ohos.aafwk.ability.fraction.Fraction;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import okhttp3.Call;
import tech.bjut.appeal.ResourceTable;
import tech.bjut.appeal.data.service.WebService;
import tech.bjut.appeal.data.util.TokenUtil;
import tech.bjut.appeal.ui.ability.MainAbility;
import tech.bjut.appeal.ui.slice.LoginSlice;
import tech.bjut.appeal.ui.util.AboutUtil;

public class MainProfileFraction extends Fraction {

    private Text name;

    private Text uid;

    private Component loginButton;

    private Component logoutButton;

    private Component feedbacksButton;

    private Call userRequest = null;

    private Call userCountRequest = null;

    @Override
    protected Component onComponentAttached(LayoutScatter scatter, ComponentContainer container, Intent intent) {
        Component component = scatter.parse(ResourceTable.Layout_slice_profile, container, false);

        Image avatar = component.findComponentById(ResourceTable.Id_profile_avatar);
        name = component.findComponentById(ResourceTable.Id_profile_name);
        uid = component.findComponentById(ResourceTable.Id_profile_uid);
        loginButton = component.findComponentById(ResourceTable.Id_profile_login);
        logoutButton = component.findComponentById(ResourceTable.Id_profile_logout);
        feedbacksButton = component.findComponentById(ResourceTable.Id_profile_feedbacks);
        avatar.setCornerRadius((float) avatar.getWidth() / 2);

        loginButton.setClickedListener(comp -> {
            MainAbility ability = (MainAbility) getFractionAbility();
            AbilitySlice slice = ability.getCurrentSlice();
            slice.presentForResult(new LoginSlice(), new Intent(), 1);
            slice.getLifecycle().addObserver(new LifecycleObserver() {
                @Override
                public void onForeground(Intent intent) {
                    slice.getLifecycle().removeObserver(this);
                    loadUser();
                }
            });
        });

        logoutButton.setClickedListener(comp -> {
            TokenUtil.deleteToken(MainProfileFraction.this);
            loadUser();
        });

        feedbacksButton.setClickedListener(comp -> {
            MainAbility ability = (MainAbility) getFractionAbility();
            // TODO
        });

        loadUser();

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

    @Override
    protected void onComponentDetach() {
        if (userRequest != null) {
            userRequest.cancel();
        }
        if (userCountRequest != null) {
            userCountRequest.cancel();
        }
    }

    private void loadUser() {
        final String token = TokenUtil.getToken(MainProfileFraction.this);
        // if token not present
        if (token == null) {
            name.setText(ResourceTable.String_profile_anonymous);
            uid.setVisibility(Component.HIDE);
            loginButton.setVisibility(Component.VISIBLE);
            logoutButton.setVisibility(Component.HIDE);
            return;
        }

        this.getMainTaskDispatcher().asyncDispatch(() -> {
            if (userRequest != null) {
                userRequest.cancel();
            }
            userRequest = WebService.getUser(token, user -> this.getUITaskDispatcher().syncDispatch(() -> {
                if (user != null) {
                    name.setText(user.getName());
                    uid.setText(user.getUid());
                    uid.setVisibility(Component.VISIBLE);
                    loginButton.setVisibility(Component.HIDE);
                    logoutButton.setVisibility(Component.VISIBLE);
                    loadCount();
                } else {
                    name.setText(ResourceTable.String_profile_anonymous);
                    uid.setVisibility(Component.HIDE);
                    loginButton.setVisibility(Component.VISIBLE);
                    logoutButton.setVisibility(Component.HIDE);
                }
            }));
        });
    }

    private void loadCount() {
        ComponentContainer container = (ComponentContainer) feedbacksButton;
        Text count = (Text) container.getComponentAt(2);
        Image linkIcon = (Image) container.getComponentAt(3);

        this.getMainTaskDispatcher().asyncDispatch(() -> {
            if (userCountRequest != null) {
                userCountRequest.cancel();
            }
            userCountRequest = WebService.getUserCount(TokenUtil.getToken(MainProfileFraction.this), userCount -> {
                this.getUITaskDispatcher().syncDispatch(() -> {
                    if (userCount != null) {
                        container.setClickable(true);
                        count.setText(String.valueOf(userCount.getHistory()));
                        linkIcon.setVisibility(Component.VISIBLE);
                    } else {
                        container.setClickable(false);
                        count.setText(ResourceTable.String_profile_requires_login);
                        linkIcon.setVisibility(Component.HIDE);
                    }
                });
            });
        });
    }
}
