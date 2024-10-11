package handlers;

public class Requests {

    public static class ClearRequest extends Requests {
    }

    public static class LoginRequest extends Requests {
        public String username;
        public String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static class RegisterRequest extends LoginRequest {
        public String email;

        public RegisterRequest(String username, String password, String email) {
            super(username, password);
            this.email = email;
        }
    }

    public static class AuthenticatedRequest extends Requests {
        public String authToken;

        public AuthenticatedRequest(String authToken) {
            this.authToken = authToken;
        }
    }

    public static class LogoutRequest extends AuthenticatedRequest {
        public LogoutRequest(String authToken) {
            super(authToken);
        }
    }

    public static class ListGamesRequest extends AuthenticatedRequest {
        public ListGamesRequest(String authToken) {
            super(authToken);
        }
    }

    public static class CreateGameRequest extends AuthenticatedRequest {
        public String gameName;

        public CreateGameRequest(String authToken, String gameName) {
            super(authToken);
            this.gameName = gameName;
        }
    }

    public static class JoinGameRequest extends AuthenticatedRequest {
        public String gameID;
        public String playerColor;

        public JoinGameRequest(String authToken, String gameID, String playerColor) {
            super(authToken);
            this.gameID = gameID;
            this.playerColor = playerColor;
        }
    }
}
