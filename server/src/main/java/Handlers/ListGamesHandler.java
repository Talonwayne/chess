package handlers;

import dataaccess.DataAccessException;
import handlers.Responses.ErrorResponse;
import handlers.Responses.ListGamesResponse;
import service.Service;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {
    private static Service service;

    public static void setService(Service hservice) {service = hservice;}

    @Override
    public Object handle(Request req, Response res) {
        String authToken = req.headers("authorization");
        try{
            service.isValidAuth(authToken);
        } catch (DataAccessException e){
            return JsonSerializer.makeSparkResponse(401, res, new ErrorResponse("Error: unauthorized"));
        }
        try {
            return JsonSerializer.makeSparkResponse(200, res, new ListGamesResponse(service.listGames()));
        }  catch (Exception e) {
            return JsonSerializer.makeSparkResponse(500, res, new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

