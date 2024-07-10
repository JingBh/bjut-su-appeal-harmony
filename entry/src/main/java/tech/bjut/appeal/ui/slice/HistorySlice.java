package tech.bjut.appeal.ui.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.fraction.FractionAbility;
import ohos.aafwk.content.Intent;
import tech.bjut.appeal.ResourceTable;
import tech.bjut.appeal.data.util.TokenUtil;
import tech.bjut.appeal.ui.fraction.QuestionsFraction;

public class HistorySlice extends AbilitySlice {

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        this.setUIContent(ResourceTable.Layout_slice_history);

        if (TokenUtil.getToken() == null) {
            this.present(new LoginSlice(), new Intent());
            this.terminate();
            return;
        }

        FractionAbility ability = (FractionAbility) this.getAbility();
        ability.getFractionManager().startFractionScheduler()
            .replace(ResourceTable.Id_history_content, new QuestionsFraction(false, true))
            .submit();
    }
}
