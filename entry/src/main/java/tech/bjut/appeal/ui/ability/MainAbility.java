package tech.bjut.appeal.ui.ability;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.fraction.FractionAbility;
import ohos.aafwk.content.Intent;
import tech.bjut.appeal.ui.slice.HistorySlice;
import tech.bjut.appeal.ui.slice.LoginSlice;
import tech.bjut.appeal.ui.slice.MainSlice;
import tech.bjut.appeal.ui.slice.QuestionSuccessSlice;

public class MainAbility extends FractionAbility {

    private AbilitySlice currentSlice = null;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);

        this.setMainRoute(MainSlice.class.getName());
        this.addActionRoute("action.main", MainSlice.class.getName());
        this.addActionRoute("action.login", LoginSlice.class.getName());
        this.addActionRoute("action.history", HistorySlice.class.getName());
        this.addActionRoute("action.question_success", QuestionSuccessSlice.class.getName());
    }

    public AbilitySlice getCurrentSlice() {
        return currentSlice;
    }

    public void setCurrentSlice(AbilitySlice currentSlice) {
        this.currentSlice = currentSlice;
    }
}
