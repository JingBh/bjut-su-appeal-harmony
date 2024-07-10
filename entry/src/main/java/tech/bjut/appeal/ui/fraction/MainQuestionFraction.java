package tech.bjut.appeal.ui.fraction;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.fraction.Fraction;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
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
import tech.bjut.appeal.ui.ability.MainAbility;
import tech.bjut.appeal.ui.slice.QuestionSuccessSlice;
import tech.bjut.appeal.ui.util.DialogUtil;

import java.io.IOException;
import java.util.Map;

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

        component.findComponentById(ResourceTable.Id_feedback_campus).setClickedListener(listener -> {
            DialogUtil.showCampusChoose(this, selectedCampus, campus -> {
                selectedCampus = campus;
                if (campus == null) {
                    formCampusText.setText(null);
                } else {
                    formCampusText.setText(CampusEnum.toString(campus));
                }
            });
        });

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
            DialogUtil.showConfirm(this, ResourceTable.String_feedback_submit_confirm, false, this::submit);
        });

        resetForm();
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

    private void resetForm() {
        if (user != null) {
            formUid.setText(user.getUid());
            formName.setText(user.getName());
        } else {
            formUid.setText(null);
            formName.setText(null);
        }
        formContact.setText(null);
        selectedCampus = null;
        formCampusText.setText(null);
        formContent.setText(null);
        formContentCount.setText("0/20000");
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
                        resetForm();
                        saveDraft();
                        dialog.destroy();
                        MainAbility ability = (MainAbility) MainQuestionFraction.this.getFractionAbility();
                        AbilitySlice slice = ability.getCurrentSlice();
                        slice.present(new QuestionSuccessSlice(), new Intent());
                    });
                }
            }
        }));
    }
}
