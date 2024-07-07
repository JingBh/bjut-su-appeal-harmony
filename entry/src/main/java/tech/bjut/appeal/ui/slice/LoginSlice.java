package tech.bjut.appeal.ui.slice;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Text;
import ohos.agp.components.TextField;
import ohos.agp.window.dialog.ToastDialog;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import tech.bjut.appeal.ResourceTable;
import tech.bjut.appeal.data.model.LoginRequestDto;
import tech.bjut.appeal.data.model.TokenResponseDto;
import tech.bjut.appeal.data.service.WebService;
import tech.bjut.appeal.data.util.TokenUtil;
import tech.bjut.appeal.ui.ability.MainAbility;

import java.io.IOException;

public class LoginSlice extends AbilitySlice {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(HiLog.LOG_APP, 0, "LoginSlice");

    private Call request = null;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        this.setUIContent(ResourceTable.Layout_slice_login);

        TextField usernameInput = this.findComponentById(ResourceTable.Id_login_username);
        TextField passwordInput = this.findComponentById(ResourceTable.Id_login_password);
        Text errorMessage = this.findComponentById(ResourceTable.Id_login_error);
        Component button = this.findComponentById(ResourceTable.Id_login_button);
        Component loading = this.findComponentById(ResourceTable.Id_login_button_loading);
        Text buttonText = this.findComponentById(ResourceTable.Id_login_button_text);

        button.setClickedListener(listener -> {
            String username = usernameInput.getText().trim();
            String password = passwordInput.getText().trim();
            if (username.isEmpty() || password.isEmpty()) {
                errorMessage.setVisibility(Component.VISIBLE);
                errorMessage.setText(ResourceTable.String_login_fail_required);
                return;
            }

            // initialize loading
            errorMessage.setVisibility(Component.HIDE);
            button.setClickable(false);
            loading.setVisibility(Component.VISIBLE);
            buttonText.setText(ResourceTable.String_login_button_loading);

            this.getMainTaskDispatcher().asyncDispatch(() -> {
                request = WebService.postToken(new LoginRequestDto(username, password), new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        HiLog.error(LOG_LABEL, e.getMessage());
                        LoginSlice.this.getMainTaskDispatcher().syncDispatch(() -> {
                            errorMessage.setVisibility(Component.VISIBLE);
                            errorMessage.setText(ResourceTable.String_login_fail_network);

                            loading.setVisibility(Component.HIDE);
                            buttonText.setText(ResourceTable.String_login_button);
                            button.setClickable(true);
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        int errorMessageRes;
                        if (response.code() == 401) {
                            errorMessageRes = ResourceTable.String_login_fail_401;
                        } else if (response.code() == 429) {
                            errorMessageRes = ResourceTable.String_login_fail_429;
                        } else if (response.code() != 200 || response.body() == null) {
                            errorMessageRes = ResourceTable.String_login_fail;
                        } else {
                            Moshi moshi = new Moshi.Builder().build();
                            JsonAdapter<TokenResponseDto> jsonAdapter = moshi.adapter(TokenResponseDto.class);
                            TokenResponseDto tokenResponse = jsonAdapter.fromJson(response.body().source());
                            if (tokenResponse == null) {
                                errorMessageRes = ResourceTable.String_login_fail;
                            } else {
                                TokenUtil.setToken(LoginSlice.this, tokenResponse);
                                LoginSlice.this.getUITaskDispatcher().syncDispatch(() -> {
                                    loading.setVisibility(Component.HIDE);
                                    buttonText.setText(ResourceTable.String_login_button);

                                    new ToastDialog(LoginSlice.this)
                                        .setText(LoginSlice.this.getString(ResourceTable.String_login_success))
                                        .show();

                                    terminate();
                                });
                                return;
                            }
                        }

                        LoginSlice.this.getUITaskDispatcher().syncDispatch(() -> {
                            errorMessage.setVisibility(Component.VISIBLE);
                            errorMessage.setText(errorMessageRes);

                            loading.setVisibility(Component.HIDE);
                            buttonText.setText(ResourceTable.String_login_button);
                            button.setClickable(true);
                        });
                    }
                });
            });
        });
    }

    @Override
    protected void onActive() {
        MainAbility ability = (MainAbility) this.getAbility();
        ability.setCurrentSlice(this);
    }

    @Override
    protected void onStop() {
        if (request != null) {
            request.cancel();
        }
    }
}
