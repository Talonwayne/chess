package handlers;

import handlers.Requests.LoginRequest;
import handlers.Responses.ErrorResponse;
import handlers.Responses.LoginResponse;
import spark.Request;
import spark.Response;
import service.UserService;
import spark.Route;
import model.AuthData;
import dataaccess.DataAccessException;
import dataaccess.UnauthorisedException;

public class LoginHandler implements Route {
    private static UserService userService;

    public static void setUserService(UserService service) {
        userService = service;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        String requestBody = req.body();
        LoginRequest loginRequest = JsonSerializer.fromJson(requestBody, LoginRequest.class);
        
        String username = loginRequest.username;
        String password = loginRequest.password;
        
        try {
            AuthData authData = userService.login(username, password);
            LoginResponse loginResponse = new LoginResponse(authData.authToken(), authData.username());
            return JsonSerializer.makeSparkResponse(200, res, loginResponse);
        } catch (UnauthorisedException e) {
            return JsonSerializer.makeSparkResponse(401, res, new ErrorResponse("Error: unauthorized"));
        } catch (DataAccessException e) {
            return JsonSerializer.makeSparkResponse(500, res, new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

