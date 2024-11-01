package handlers;

import dataaccess.DataAccessException;
import handlers.Requests.JoinGameRequest;
import handlers.Responses.ErrorResponse;
import service.Service;
import spark.Request;
import spark.Response;
import spark.Route;

import java.nio.file.FileAlreadyExistsException;

public class JoinGameHandler implements Route {
    private static Service service;

    public static void setService(Service hservice) {service = hservice;}

    @Override
    public Object handle(Request req, Response res) {
        String requestBody = req.body();
        String authToken = req.headers("authorization");
        JoinGameRequest joinGameRequest = JsonSerializer.fromJson(requestBody, JoinGameRequest.class);
        try {
            if(!service.isValidAuth(authToken)){
                return JsonSerializer.makeSparkResponse(401, res, new ErrorResponse("Error: unauthorized"));
            }
            service.joinGame(authToken,joinGameRequest.playerColor(), joinGameRequest.gameID());
            return "{}";
        }catch (FileAlreadyExistsException e) {
            return JsonSerializer.makeSparkResponse(403, res, new ErrorResponse("Error: already taken"));
        }catch (DataAccessException e){
            return JsonSerializer.makeSparkResponse(400, res, new ErrorResponse("Error: bad request"));
        } catch (Exception e) {
            return JsonSerializer.makeSparkResponse(500, res, new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

