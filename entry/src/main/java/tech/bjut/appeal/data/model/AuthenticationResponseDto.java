package tech.bjut.appeal.data.model;

public class AuthenticationResponseDto {

    private User user;

    public AuthenticationResponseDto() {}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
