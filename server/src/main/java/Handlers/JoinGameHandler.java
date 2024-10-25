package handlers;

import dataaccess.UnauthorisedException;
import handlers.Requests.JoinGameRequest;
import handlers.Responses.ErrorResponse;
import service.GameService;
import spark.Request;
import spark.Response;
import service.UserService;
import spark.Route;

import java.nio.file.FileAlreadyExistsException;
import java.util.InputMismatchException;

public class JoinGameHandler implements Route {
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
        JoinGameRequest joinGameRequest = JsonSerializer.fromJson(requestBody, JoinGameRequest.class);

        try {
            Validator.isValidAuth(authToken,userService.getAuthDAO());
            gameService.joinGame(authToken,joinGameRequest.playerColor, joinGameRequest.gameID);
            return "{}";
        }catch (UnauthorisedException e){
            return JsonSerializer.makeSparkResponse(401, res, new ErrorResponse("Error: unauthorized"));
        }catch (FileAlreadyExistsException e) {
            return JsonSerializer.makeSparkResponse(403, res, new ErrorResponse("Error: already taken"));
        }catch (InputMismatchException e){
            return JsonSerializer.makeSparkResponse(400, res, new ErrorResponse("Error: bad request"));
        } catch (Exception e) {
            return JsonSerializer.makeSparkResponse(500, res, new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

