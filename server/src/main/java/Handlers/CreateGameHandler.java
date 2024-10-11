package handlers;

import service.GameService;
import spark.Request;
import spark.Response;
import service.UserService;
import spark.Route;

import java.nio.file.FileAlreadyExistsException;
import java.util.InputMismatchException;

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
        String authToken = req.headers().toString();
        if (!Validator.isValidAuth(authToken,userService.getAuthDAO())){
            return JsonSerializer.makeSparkResponse(401, res, new ErrorResponse("Error: unauthorized"));
        }
        CreateGameRequest createGameRequest = JsonSerializer.fromJson(requestBody, CreateGameRequest.class);

        try {
            int gameID = gameService.createGame(createGameRequest.gameName);
            return JsonSerializer.makeSparkResponse(200, res, new CreateGameResponse(gameID));
        }catch (FileAlreadyExistsException e){
            return JsonSerializer.makeSparkResponse(400, res, new ErrorResponse("Error: bad request"));
        } catch (Exception e) {
            return JsonSerializer.makeSparkResponse(500, res, new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

class CreateGameRequest {
    public String gameName;
}
class CreateGameResponse{
    public int gameID;
    CreateGameResponse(int gameID) {
        this.gameID = gameID;
    }
}