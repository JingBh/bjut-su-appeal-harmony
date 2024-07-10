package tech.bjut.appeal;

import ohos.aafwk.ability.AbilityPackage;
import ohos.data.DatabaseHelper;
import tech.bjut.appeal.data.util.CacheUtil;
import tech.bjut.appeal.data.util.TokenUtil;

public class AppealApplication extends AbilityPackage {
    @Override
    public void onInitialize() {
        super.onInitialize();

        // initialize utils
        CacheUtil.setCacheDir(this.getCacheDir());
        TokenUtil.setPreferences(new DatabaseHelper(this).getPreferences("auth"));
    }
}
