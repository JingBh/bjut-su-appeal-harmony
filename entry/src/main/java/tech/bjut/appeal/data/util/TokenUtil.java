package tech.bjut.appeal.data.util;

import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import org.jetbrains.annotations.Nullable;
import tech.bjut.appeal.data.model.TokenResponseDto;

import java.time.Instant;

public class TokenUtil {

    @Nullable
    public static String getToken(Context context) {
        Preferences preferences = getPreferences(context);
        if (Instant.now().getEpochSecond() < preferences.getLong("expires_at", 0)) {
            return preferences.getString("token", null);
        }
        return null;
    }

    public static void setToken(Context context, TokenResponseDto token) {
        Preferences preferences = getPreferences(context);
        preferences.putString("token", token.getAccessToken());
        preferences.putLong("expires_at", Instant.now().getEpochSecond() + token.getExpiresIn());
    }

    public static void deleteToken(Context context) {
        Preferences preferences = getPreferences(context);
        preferences.delete("token");
        preferences.delete("expires_at");
    }

    public static Preferences getPreferences(Context context) {
        return new DatabaseHelper(context)
            .getPreferences("auth");
    }
}
