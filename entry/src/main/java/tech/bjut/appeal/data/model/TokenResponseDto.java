package tech.bjut.appeal.data.model;

import com.squareup.moshi.Json;

public class TokenResponseDto {

    @Json(name = "access_token")
    private String accessToken;

    @Json(name = "expires_in")
    private long expiresIn;

    public TokenResponseDto() {}

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
