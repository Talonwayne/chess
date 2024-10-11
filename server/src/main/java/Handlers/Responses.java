package handlers;

import java.util.List;
import model.GameData;

public class Responses {

    public static class ErrorResponse extends Responses {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }   
    }

    public static class LogoutResponse extends Responses {
    }

    public static class ClearResponse extends Responses {
    }

    public static class LoginResponse extends Responses {
        public String authToken;
        public String username;

        public LoginResponse(String authToken, String username) {
            this.authToken = authToken;
            this.username = username;
        }
    }

    public static class RegisterResponse extends LoginResponse {
        public RegisterResponse(String authToken, String username) {
            super(authToken, username);
        }
    }

    public static class ListGamesResponse extends Responses {
        public List<GameData> games;

        public ListGamesResponse(List<GameData> games) {
            this.games = games;
        }
    }

    public static class CreateGameResponse extends Responses {
        public String gameName;

        public CreateGameResponse(String gameName) {
            this.gameName = gameName;
        }
    }

    public static class JoinGameResponse extends Responses {
    }
}
