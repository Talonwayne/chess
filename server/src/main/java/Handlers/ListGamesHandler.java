package handlers;

import dataaccess.UnauthorisedException;
import handlers.Responses.ErrorResponse;
import handlers.Responses.ListGamesResponse;
import service.GameService;
import spark.Request;
import spark.Response;
import service.UserService;
import spark.Route;

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

