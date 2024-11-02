package handlers;

import handlers.requests.RegisterRequest;
import handlers.responses.ErrorResponse;
import handlers.responses.LoginResponse;
import service.Service;
import spark.Request;
import spark.Response;
import spark.Route;
import model.AuthData;
import dataaccess.DataAccessException;

public class RegisterHandler implements Route {
    private static Service service;

    public static void setService(Service hservice) {service = hservice;}

    @Override
    public Object handle(Request req, Response res) {
        String requestBody = req.body();
        RegisterRequest registerRequest = JsonSerializer.fromJson(requestBody, RegisterRequest.class);
        
        if (!isValidInput(registerRequest)) {
            return JsonSerializer.makeSparkResponse(400, res, new ErrorResponse("Error: bad request"));
        }
        
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();
        
        try {
            AuthData authData = service.register(username, password, email);
            LoginResponse loginResponse = new LoginResponse(authData.authToken(), authData.username());
            return JsonSerializer.makeSparkResponse(200, res, loginResponse);
        } catch (DataAccessException e) {
            return JsonSerializer.makeSparkResponse(403, res, new ErrorResponse("Error: already taken"));
        } catch (Exception e) {
            return JsonSerializer.makeSparkResponse(500, res, new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private boolean isValidInput(RegisterRequest request) {
        return request != null &&
               request.username() != null && !request.username().isEmpty() &&
               request.password() != null && !request.password().isEmpty() &&
               request.email() != null && !request.email().isEmpty();
    }
}

