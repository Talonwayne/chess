package handlers;

import handlers.requests.LoginRequest;
import handlers.responses.ErrorResponse;
import handlers.responses.LoginResponse;
import service.Service;
import spark.Request;
import spark.Response;
import spark.Route;
import model.AuthData;
import dataaccess.DataAccessException;

public class LoginHandler implements Route {
    private static Service service;

    public static void setService(Service hservice) {service = hservice;}

    @Override
    public Object handle(Request req, Response res) {
        String requestBody = req.body();
        LoginRequest loginRequest = JsonSerializer.fromJson(requestBody, LoginRequest.class);
        
        String username = loginRequest.username();
        String password = loginRequest.password();
        
        try {
            AuthData authData = service.login(username, password);
            LoginResponse loginResponse = new LoginResponse(authData.authToken(), authData.username());
            return JsonSerializer.makeSparkResponse(200, res, loginResponse);
        } catch (DataAccessException e) {
            return JsonSerializer.makeSparkResponse(401, res, new ErrorResponse("Error: unauthorized"));
        } catch (Exception e) {
            return JsonSerializer.makeSparkResponse(500, res, new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}

