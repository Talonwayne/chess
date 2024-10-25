package handlers;

import dataaccess.UnauthorisedException;
import handlers.Requests.CreateGameRequest;
import handlers.Responses.CreateGameResponse;
import handlers.Responses.ErrorResponse;
import service.GameService;
import spark.Request;
import spark.Response;
import service.UserService;
import spark.Route;

import java.nio.file.FileAlreadyExistsException;

public class CreateGameHandler implements Route {
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
        String requestBody = req.body();
        String authToken = req.headers("authorization");

        CreateGameRequest createGameRequest = JsonSerializer.fromJson(requestBody, CreateGameRequest.class);

        try {
            Validator.isValidAuth(authToken,userService.getAuthDAO());
            int gameID = gameService.createGame(createGameRequest.gameName);
            return JsonSerializer.makeSparkResponse(200, res, new CreateGameResponse(gameID));
        }catch (UnauthorisedException e){
            return JsonSerializer.makeSparkResponse(401, res, new ErrorResponse("Error: unauthorized"));
        } catch (FileAlreadyExistsException e){
            return JsonSerializer.makeSparkResponse(400, res, new ErrorResponse("Error: bad request"));
        } catch (Exception e) {
            return JsonSerializer.makeSparkResponse(500, res, new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

