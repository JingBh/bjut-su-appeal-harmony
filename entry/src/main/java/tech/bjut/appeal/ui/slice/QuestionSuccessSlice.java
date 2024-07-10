package tech.bjut.appeal.ui.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import tech.bjut.appeal.ResourceTable;
import tech.bjut.appeal.data.util.TokenUtil;

public class QuestionSuccessSlice extends AbilitySlice {

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        this.setUIContent(ResourceTable.Layout_slice_question_success);

        if (TokenUtil.getToken() != null) {
            this.findComponentById(ResourceTable.Id_feedback_success_hint)
                .setVisibility(Component.HIDE);
        }

        this.findComponentById(ResourceTable.Id_feedback_success_action_home)
            .setClickedListener(listener -> terminate());

        this.findComponentById(ResourceTable.Id_feedback_success_action_mine)
            .setClickedListener(listener -> {
                present(new HistorySlice(), new Intent());
                terminate();
            });
    }
}
