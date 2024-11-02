package handlers;

import handlers.requests.CreateGameRequest;
import handlers.responses.CreateGameResponse;
import handlers.responses.ErrorResponse;
import service.Service;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    private static Service service;

    public static void setService(Service hservice) {service = hservice;}

    @Override
    public Object handle(Request req, Response res) {
        String requestBody = req.body();
        String authToken = req.headers("authorization");

        CreateGameRequest createGameRequest = JsonSerializer.fromJson(requestBody, CreateGameRequest.class);
        try {
            if(!service.isValidAuth(authToken)){
                return JsonSerializer.makeSparkResponse(401, res, new ErrorResponse("Error: unauthorized"));
            }
            int gameID = service.createGame(createGameRequest.gameName());
            return JsonSerializer.makeSparkResponse(200, res, new CreateGameResponse(gameID));
        } catch (Exception e) {
            return JsonSerializer.makeSparkResponse(500, res, new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

