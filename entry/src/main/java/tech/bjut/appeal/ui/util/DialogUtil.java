package tech.bjut.appeal.ui.util;

import ohos.agp.components.*;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Color;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import tech.bjut.appeal.ResourceTable;
import tech.bjut.appeal.data.model.CampusEnum;
import tech.bjut.appeal.data.util.ValueCallback;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;
import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_PARENT;

public class DialogUtil {

    public static void showToast(Context context, int contentResource) {
        showToast(context, context.getString(contentResource));
    }

    public static void showToast(Context context, String content) {
        new ToastDialog(context)
            .setText(content)
            .setAlignment(LayoutAlignment.BOTTOM)
            .setOffset(0, 256)
            .show();
    }

    public static CommonDialog showLoading(Context context) {
        Component loadingDialog = LayoutScatter.getInstance(context)
                .parse(ResourceTable.Layout_dialog_loading, null, false);

        CommonDialog dialog = new CommonDialog(context);
        dialog.setCornerRadius(((ShapeElement) loadingDialog.getBackgroundElement()).getCornerRadius());
        dialog.setAlignment(LayoutAlignment.CENTER);
        dialog.setSize(MATCH_CONTENT, MATCH_CONTENT);
        dialog.setContentCustomComponent(loadingDialog);
        dialog.show();

        return dialog;
    }

    public static CommonDialog showConfirm(Context context, int content, boolean danger, Runnable onConfirm) {
        Component component = LayoutScatter.getInstance(context)
            .parse(ResourceTable.Layout_dialog_confirm, null, false);

        Text contentComponent =  component.findComponentById(ResourceTable.Id_dialog_confirm_content);
        contentComponent.setText(content);

        Button cancelButton = component.findComponentById(ResourceTable.Id_dialog_confirm_cancel);
        Button confirmButton = component.findComponentById(ResourceTable.Id_dialog_confirm_confirm);
        if (danger) {
            confirmButton.setTextColor(new Color(context.getColor(ResourceTable.Color_red_500)));
        } else {
            confirmButton.setTextColor(new Color(context.getColor(ResourceTable.Color_brand)));
        }

        CommonDialog dialog = new CommonDialog(context);
        dialog.setCornerRadius(((ShapeElement) component.getBackgroundElement()).getCornerRadius());
        dialog.setAlignment(LayoutAlignment.CENTER);
        dialog.setSize(MATCH_CONTENT, MATCH_CONTENT);
        dialog.setContentCustomComponent(component);
        dialog.show();

        cancelButton.setClickedListener(listener1 -> {
            dialog.destroy();
        });

        confirmButton.setClickedListener(listener1 -> {
            dialog.destroy();
            onConfirm.run();
        });

        return dialog;
    }

    public static CommonDialog showCampusChoose(Context context, CampusEnum initial, ValueCallback<CampusEnum> callback) {
        Component component = LayoutScatter.getInstance(context)
            .parse(ResourceTable.Layout_dialog_campus_choose, null, false);

        Picker picker = component.findComponentById(ResourceTable.Id_dialog_campus_choose_picker);
        picker.setMinValue(0);
        picker.setMaxValue(CampusEnum.values().length);
        picker.setValue(initial == null ? 0 : initial.ordinal() + 1);
        picker.setFormatter(i -> {
            if (i == 0) {
                return context.getString(ResourceTable.String_feedback_campus_choose_unselected);
            }
            return context.getString(CampusEnum.toString(CampusEnum.values()[i - 1]));
        });
        picker.setValueChangedListener((listener1, oldValue, newValue) -> {
            callback.call(newValue == 0 ? null : CampusEnum.values()[newValue - 1]);
        });

        CommonDialog dialog = new CommonDialog(context);
        dialog.setContentCustomComponent(component);
        dialog.setSize(MATCH_PARENT, MATCH_CONTENT);
        dialog.setAlignment(LayoutAlignment.BOTTOM);
        dialog.setOffset(0, 0);
        dialog.setAutoClosable(true);
        dialog.show();

        return dialog;
    }
}
