package handlers;

import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;
import service.UserService;
import spark.Route;
import java.util.List;

public class ListGamesHandler implements Route {
    private static UserService userService;
    private static GameService gameService;

    public static void setUserService(UserService service) {
        userService = service;
    }
    public static void setGameService(GameService service) {
        gameService = service;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        String authToken = req.headers().toString();
        if (!Validator.isValidAuth(authToken,userService.getAuthDAO())){
            return JsonSerializer.makeSparkResponse(401, res, new ErrorResponse("Error: unauthorized"));
        }

        try {
            List<GameData> games = gameService.listGames();
            return JsonSerializer.makeSparkResponse(200, res, new ListGamesResponse(games));
        } catch (Exception e) {
            return JsonSerializer.makeSparkResponse(500, res, new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

class ListGamesResponse{
    public List<GameData> games;
    ListGamesResponse(List<GameData> games) {
        this.games = games;
    }
}