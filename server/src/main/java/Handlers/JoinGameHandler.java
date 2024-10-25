package handlers;

import dataaccess.DataAccessException;
import handlers.Requests.JoinGameRequest;
import handlers.Responses.ErrorResponse;
import service.Service;
import spark.Request;
import spark.Response;
import spark.Route;

import java.nio.file.FileAlreadyExistsException;
import java.util.InputMismatchException;

public class JoinGameHandler implements Route {
    private static Service service;

    public static void setService(Service hservice) {service = hservice;}

    @Override
    public Object handle(Request req, Response res) {
        String requestBody = req.body();
        String authToken = req.headers("authorization");
        JoinGameRequest joinGameRequest = JsonSerializer.fromJson(requestBody, JoinGameRequest.class);

        try{
            service.isValidAuth(authToken);
        } catch (DataAccessException e){
            return JsonSerializer.makeSparkResponse(401, res, new ErrorResponse("Error: unauthorized"));
        }

        try {
            service.joinGame(authToken,joinGameRequest.playerColor(), joinGameRequest.gameID());
            return "{}";
        }catch (FileAlreadyExistsException e) {
            return JsonSerializer.makeSparkResponse(403, res, new ErrorResponse("Error: already taken"));
        }catch (InputMismatchException e){
            return JsonSerializer.makeSparkResponse(400, res, new ErrorResponse("Error: bad request"));
        } catch (Exception e) {
            return JsonSerializer.makeSparkResponse(500, res, new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

