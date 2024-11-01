package handlers;

import handlers.Responses.ErrorResponse;
import service.Service;
import spark.Request;
import spark.Response;
import spark.Route;
import dataaccess.DataAccessException;

public class LogoutHandler implements Route {
    private static Service service;

    public static void setService(Service hservice) {service = hservice;}

    @Override
    public Object handle(Request req, Response res){
        String authToken = req.headers("authorization");

        try {
            if(!service.isValidAuth(authToken)){
                return JsonSerializer.makeSparkResponse(401, res, new ErrorResponse("Error: unauthorized"));
            }
            service.logout(authToken);
            return "{}";
        } catch (DataAccessException e) {
            return JsonSerializer.makeSparkResponse(500, res, new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}
