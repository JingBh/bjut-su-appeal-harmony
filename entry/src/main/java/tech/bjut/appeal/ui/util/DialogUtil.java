package tech.bjut.appeal.ui.util;

import ohos.agp.components.Component;
import ohos.agp.components.LayoutScatter;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import tech.bjut.appeal.ResourceTable;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;

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
}
