package tech.bjut.appeal.ui.fraction;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import ohos.aafwk.ability.fraction.Fraction;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.CommonDialog;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.system.version.SystemVersion;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.bjut.appeal.ResourceTable;
import tech.bjut.appeal.data.model.CampusEnum;
import tech.bjut.appeal.data.model.QuestionRequestDto;
import tech.bjut.appeal.data.model.User;
import tech.bjut.appeal.data.service.WebService;
import tech.bjut.appeal.data.util.TokenUtil;
import tech.bjut.appeal.ui.util.DialogUtil;

import java.io.IOException;
import java.util.Map;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;
import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_PARENT;

public class MainQuestionFraction extends Fraction {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(HiLog.LOG_APP, 0, "MainQuestionFraction");

    private final JsonAdapter<QuestionRequestDto> jsonAdapter;

    private final JsonAdapter<Map<String, String>> formErrorJsonAdapter;

    @Nullable
    private User user = null;

    @Nullable
    private CampusEnum selectedCampus = null;

    private TextField formUid;

    private Text formUidError;

    private TextField formName;

    private Text formNameError;

    private TextField formContact;

    private Text formContactError;

    private Text formCampusText;

    private Text formCampusError;

    private TextField formContent;

    private Text formContentError;

    private Text formContentCount;

    public MainQuestionFraction() {
        Moshi moshi = new Moshi.Builder().build();
        jsonAdapter = moshi.adapter(QuestionRequestDto.class);
        formErrorJsonAdapter = moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));
    }

    @Override
    protected Component onComponentAttached(LayoutScatter scatter, ComponentContainer container, Intent intent) {
        Component component = scatter.parse(ResourceTable.Layout_slice_question, container, false);

        formUid = component.findComponentById(ResourceTable.Id_feedback_uid);
        formUidError = component.findComponentById(ResourceTable.Id_feedback_uid_error);
        formName = component.findComponentById(ResourceTable.Id_feedback_name);
        formNameError = component.findComponentById(ResourceTable.Id_feedback_name_error);
        formContact = component.findComponentById(ResourceTable.Id_feedback_contact);
        formContactError = component.findComponentById(ResourceTable.Id_feedback_contact_error);
        formCampusText = component.findComponentById(ResourceTable.Id_feedback_campus_text);
        formCampusError = component.findComponentById(ResourceTable.Id_feedback_campus_error);
        formContent = component.findComponentById(ResourceTable.Id_feedback_content);
        formContentError = component.findComponentById(ResourceTable.Id_feedback_content_error);
        formContentCount = component.findComponentById(ResourceTable.Id_feedback_content_count);

        user = null;
        if (TokenUtil.getToken() != null) {
            this.getMainTaskDispatcher().asyncDispatch(() -> {
                WebService.getUser(value -> {
                    user = value;
                    if (this.getUITaskDispatcher() == null) {
                        return;
                    }
                    this.getUITaskDispatcher().asyncDispatch(() -> {
                        if (user != null) {
                            formUid.setText(user.getUid());
                            formName.setText(user.getName());
                            if (SystemVersion.getApiVersion() >= 7) {
                                formUid.setEditable(false);
                                formName.setEditable(false);
                            }
                        }
                    });
                });
            });
        }

        selectedCampus = null;
        component.findComponentById(ResourceTable.Id_feedback_campus).setClickedListener(listener -> {
            Component chooseComponent = LayoutScatter.getInstance(this)
                .parse(ResourceTable.Layout_dialog_campus_choose, null, false);

            Picker picker = chooseComponent.findComponentById(ResourceTable.Id_dialog_campus_choose_picker);
            picker.setMinValue(0);
            picker.setMaxValue(CampusEnum.values().length);
            picker.setValue(selectedCampus == null ? 0 : selectedCampus.ordinal() + 1);
            picker.setFormatter(i -> {
                if (i == 0) {
                    return this.getString(ResourceTable.String_feedback_campus_choose_unselected);
                }
                return this.getString(CampusEnum.toString(CampusEnum.values()[i - 1]));
            });
            picker.setValueChangedListener((listener1, oldValue, newValue) -> {
                if (newValue == 0) {
                    selectedCampus = null;
                    formCampusText.setText(null);
                } else {
                    selectedCampus = CampusEnum.values()[newValue - 1];
                    formCampusText.setText(CampusEnum.toString(selectedCampus));
                }
            });

            CommonDialog dialog = new CommonDialog(this);
            dialog.setContentCustomComponent(chooseComponent);
            dialog.setSize(MATCH_PARENT, MATCH_CONTENT);
            dialog.setAlignment(LayoutAlignment.BOTTOM);
            dialog.setOffset(0, 0);
            dialog.setAutoClosable(true);
            dialog.show();
        });

        formContentCount.setText("0/20000");
        formContent.addTextObserver((text, start, end, count) -> {
            if (formContent.getText() == null) {
                formContentCount.setText("0/20000");
            } else {
                formContentCount.setText(formContent.getText().length() + "/20000");
            }
        });

        component.findComponentById(ResourceTable.Id_feedback_action_draft).setClickedListener(listener -> {
            saveDraft();
        });

        component.findComponentById(ResourceTable.Id_feedback_action_submit).setClickedListener(listener -> {
            Component confirmComponent = LayoutScatter.getInstance(this)
                .parse(ResourceTable.Layout_dialog_confirm, null, false);

            Text content = confirmComponent.findComponentById(ResourceTable.Id_dialog_confirm_content);
            content.setText(ResourceTable.String_feedback_submit_confirm);

            CommonDialog dialog = new CommonDialog(this);
            dialog.setCornerRadius(((ShapeElement) confirmComponent.getBackgroundElement()).getCornerRadius());
            dialog.setAlignment(LayoutAlignment.CENTER);
            dialog.setSize(MATCH_CONTENT, MATCH_CONTENT);
            dialog.setContentCustomComponent(confirmComponent);
            dialog.show();

            confirmComponent.findComponentById(ResourceTable.Id_dialog_confirm_cancel)
                .setClickedListener(listener1 -> {
                    dialog.destroy();
                });

            confirmComponent.findComponentById(ResourceTable.Id_dialog_confirm_confirm)
                .setClickedListener(listener1 -> {
                    dialog.destroy();
                    submit();
                });
        });

        resetFormErrors();
        restoreDraft();

        return component;
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        saveDraft();
    }

    private QuestionRequestDto getFormData() {
        QuestionRequestDto dto = new QuestionRequestDto();
        dto.setUid(formUid.getText());
        dto.setName(formName.getText());
        dto.setContact(formContact.getText());
        dto.setCampus(selectedCampus);
        dto.setContent(formContent.getText());
        return dto;
    }

    private void saveDraft() {
        Preferences preferences = new DatabaseHelper(this).getPreferences("app");
        if (formContent.getText() == null || formContent.getText().trim().isEmpty()) {
            preferences.delete("question_draft");
        } else {
            preferences.putString("question_draft", jsonAdapter.toJson(getFormData()));
            DialogUtil.showToast(this, ResourceTable.String_feedback_draft_saved);
        }
        preferences.flush();
    }

    private void restoreDraft() {
        Preferences preferences = new DatabaseHelper(this).getPreferences("app");
        String draft = preferences.getString("question_draft", null);
        try {
            QuestionRequestDto dto = jsonAdapter.fromJson(draft);
            if (dto == null) {
                return;
            }
            formUid.setText(dto.getUid());
            formName.setText(dto.getName());
            formContact.setText(dto.getContact());
            selectedCampus = dto.getCampus();
            if (selectedCampus == null) {
                formCampusText.setText(null);
            } else {
                formCampusText.setText(CampusEnum.toString(selectedCampus));
            }
            formContent.setText(dto.getContent());
            if (formContent.getText() != null) {
                formContentCount.setText(formContent.getText().length() + "/20000");
            }
        } catch (Exception ignored) {}
    }

    private void resetFormErrors() {
        formUidError.setVisibility(Component.HIDE);
        formNameError.setVisibility(Component.HIDE);
        formContactError.setVisibility(Component.HIDE);
        formCampusError.setVisibility(Component.HIDE);
        formContentError.setVisibility(Component.HIDE);
    }

    private void submit() {
        CommonDialog dialog = DialogUtil.showLoading(this);
        resetFormErrors();
        this.getMainTaskDispatcher().asyncDispatch(() -> WebService.postQuestion(getFormData(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HiLog.error(LOG_LABEL, e.getMessage());
                if (MainQuestionFraction.this.getUITaskDispatcher() == null) {
                    return;
                }
                MainQuestionFraction.this.getUITaskDispatcher().syncDispatch(() -> {
                    dialog.destroy();
                    DialogUtil.showToast(MainQuestionFraction.this, ResourceTable.String_feedback_submit_failed);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (MainQuestionFraction.this.getUITaskDispatcher() == null) {
                    return;
                }
                if (response.code() == 429) {
                    MainQuestionFraction.this.getUITaskDispatcher().syncDispatch(() -> {
                        dialog.destroy();
                        DialogUtil.showToast(MainQuestionFraction.this, ResourceTable.String_feedback_submit_too_frequent);
                    });
                } else if (response.code() == 422 && response.body() != null) {
                    Map<String, String> errors = formErrorJsonAdapter.fromJson(response.body().source());
                    MainQuestionFraction.this.getUITaskDispatcher().syncDispatch(() -> {
                        dialog.destroy();
                        if (errors == null) {
                            resetFormErrors();
                            DialogUtil.showToast(MainQuestionFraction.this, ResourceTable.String_feedback_submit_failed);
                            return;
                        }
                        if (errors.containsKey("uid")) {
                            formUidError.setText(errors.get("uid"));
                            formUidError.setVisibility(Component.VISIBLE);
                        } else {
                            formUidError.setVisibility(Component.HIDE);
                        }
                        if (errors.containsKey("name")) {
                            formNameError.setText(errors.get("name"));
                            formNameError.setVisibility(Component.VISIBLE);
                        } else {
                            formNameError.setVisibility(Component.HIDE);
                        }
                        if (errors.containsKey("contact")) {
                            formContactError.setText(errors.get("contact"));
                            formContactError.setVisibility(Component.VISIBLE);
                        } else {
                            formContactError.setVisibility(Component.HIDE);
                        }
                        if (errors.containsKey("campus")) {
                            formCampusError.setText(errors.get("campus"));
                            formCampusError.setVisibility(Component.VISIBLE);
                        } else {
                            formCampusError.setVisibility(Component.HIDE);
                        }
                        if (errors.containsKey("content")) {
                            formContentError.setText(errors.get("content"));
                            formContentError.setVisibility(Component.VISIBLE);
                        } else {
                            formContentError.setVisibility(Component.HIDE);
                        }
                    });
                } else if (!response.isSuccessful()) {
                    HiLog.error(LOG_LABEL, "submit failed with status code " + response.code());
                    MainQuestionFraction.this.getUITaskDispatcher().syncDispatch(() -> {
                        dialog.destroy();
                        DialogUtil.showToast(MainQuestionFraction.this, ResourceTable.String_feedback_submit_failed);
                    });
                } else {
                    MainQuestionFraction.this.getUITaskDispatcher().syncDispatch(() -> {
                        dialog.destroy();
                        // TODO: navigate to success page
                    });
                }
            }
        }));
    }
}
