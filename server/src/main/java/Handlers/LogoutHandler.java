package handlers;

import spark.Request;
import spark.Response;
import service.UserService;
import spark.Route;
import dataaccess.DataAccessException;
import dataaccess.UnauthorisedException;

public class LogoutHandler implements Route {
    private static UserService userService;

    public static void setUserService(UserService service) {
        userService = service;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        String requestBody = req.body();
        LogoutRequest logoutRequest = JsonSerializer.fromJson(requestBody, LogoutRequest.class);
        String authToken = logoutRequest.authorization;
        
        try {
            Validator.isValidAuth(authToken, userService.getAuthDAO());
            userService.logout(authToken);
            return JsonSerializer.makeSparkResponse(200, res,"{}");
        } catch (UnauthorisedException e) {
            return JsonSerializer.makeSparkResponse(401, res, new ErrorResponse("Error: unauthorized"));
        } catch (DataAccessException e) {
            return JsonSerializer.makeSparkResponse(500, res, new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

class LogoutRequest {
    public String authorization;
}
