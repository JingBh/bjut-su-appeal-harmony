package tech.bjut.appeal.ui.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.fraction.Fraction;
import ohos.aafwk.ability.fraction.FractionAbility;
import ohos.aafwk.content.Intent;
import tech.bjut.appeal.ResourceTable;
import tech.bjut.appeal.data.util.TokenUtil;
import tech.bjut.appeal.ui.fraction.QuestionsFraction;

public class HistorySlice extends AbilitySlice {

    private Fraction fraction = null;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        this.setUIContent(ResourceTable.Layout_slice_history);

        if (TokenUtil.getToken() == null) {
            this.fraction = null;
            this.present(new LoginSlice(), new Intent());
            this.terminate();
            return;
        }

        this.fraction = new QuestionsFraction(false, true);
        FractionAbility ability = (FractionAbility) this.getAbility();
        ability.getFractionManager().startFractionScheduler()
            .replace(ResourceTable.Id_history_content, this.fraction)
            .submit();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (this.fraction != null) {
            FractionAbility ability = (FractionAbility) this.getAbility();
            ability.getFractionManager().startFractionScheduler()
                    .remove(this.fraction)
                    .submit();
        }
    }
}
