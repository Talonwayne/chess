package handlers;

import dataaccess.UnauthorisedException;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;
import service.UserService;
import spark.Route;

import java.util.HashSet;
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
        String authToken = req.headers("authorization");
        try {
            Validator.isValidAuth(authToken,userService.getAuthDAO());
            return JsonSerializer.makeSparkResponse(200, res, new ListGamesResponse(gameService.listGames()));
        } catch (UnauthorisedException e){
            return JsonSerializer.makeSparkResponse(401, res, new ErrorResponse("Error: unauthorized"));
        } catch (Exception e) {
            return JsonSerializer.makeSparkResponse(500, res, new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

class ListGamesResponse{
    public HashSet<GameData> games;
    ListGamesResponse(HashSet<GameData> games) {
        this.games = games;
    }
}