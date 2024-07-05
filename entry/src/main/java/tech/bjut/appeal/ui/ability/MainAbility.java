package tech.bjut.appeal.ui.ability;

import ohos.aafwk.ability.fraction.FractionAbility;
import ohos.aafwk.content.Intent;
import tech.bjut.appeal.ui.slice.MainSlice;

public class MainAbility extends FractionAbility {

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        this.setMainRoute(MainSlice.class.getName());
    }
}
