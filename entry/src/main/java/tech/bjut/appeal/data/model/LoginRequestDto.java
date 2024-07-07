package tech.bjut.appeal.data.model;

import okhttp3.FormBody;

public class LoginRequestDto {

    private String username;
    private String password;

    public LoginRequestDto() {}

    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FormBody toFormBody() {
        return new FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build();
    }
}
