package service;

import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    public AuthData register(UserData user) {
        return login(user);
    }
    public AuthData login(UserData user) {
        String authtoken = UUID.randomUUID().toString();
        return new AuthData(authtoken, user.username());
    }
    public void logout(AuthData auth) {}
}
