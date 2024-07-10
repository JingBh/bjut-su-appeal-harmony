package tech.bjut.appeal.data.util;

import ohos.data.preferences.Preferences;
import org.jetbrains.annotations.Nullable;
import tech.bjut.appeal.data.model.TokenResponseDto;

import java.time.Instant;

public class TokenUtil {

    private static Preferences preferences;

    @Nullable
    public static String getToken() {
        Preferences preferences = getPreferences();
        if (Instant.now().getEpochSecond() < preferences.getLong("expires_at", 0)) {
            return preferences.getString("token", null);
        }
        return null;
    }

    public static void setToken(TokenResponseDto token) {
        Preferences preferences = getPreferences();
        preferences.putString("token", token.getAccessToken());
        preferences.putLong("expires_at", Instant.now().getEpochSecond() + token.getExpiresIn());
        preferences.flush();
    }

    public static void deleteToken() {
        Preferences preferences = getPreferences();
        preferences.delete("token");
        preferences.delete("expires_at");
        preferences.flush();
    }

    public static Preferences getPreferences() {
        if (preferences == null) {
            throw new RuntimeException("TokenUtil not initialized");
        }
        return preferences;
    }

    public static void setPreferences(Preferences preferences) {
        TokenUtil.preferences = preferences;
    }
}
