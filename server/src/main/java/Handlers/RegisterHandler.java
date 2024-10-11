package handlers;

import spark.Request;
import spark.Response;
import service.UserService;
import spark.Route;
import model.AuthData;
import dataaccess.DataAccessException;
import dataaccess.UnauthorisedException;

public class RegisterHandler implements Route {
    private static UserService userService;

    public static void setUserService(UserService service) {
        userService = service;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        String requestBody = req.body();
        RegisterRequest registerRequest = JsonSerializer.fromJson(requestBody, RegisterRequest.class);
        
        if (!isValidInput(registerRequest)) {
            return JsonSerializer.makeSparkResponse(400, res, new ErrorResponse("Error: bad request"));
        }
        
        String username = registerRequest.username;
        String password = registerRequest.password;
        String email = registerRequest.email;
        
        try {
            userService.register(username, password, email);
            AuthData authData = userService.login(username, password);
            LoginResponse loginResponse = new LoginResponse(authData.authToken(), authData.username());
            return JsonSerializer.makeSparkResponse(200, res, loginResponse);
        } catch (UnauthorisedException e) {
            return JsonSerializer.makeSparkResponse(403, res, new ErrorResponse("Error: already taken"));
        } catch (DataAccessException e) {
            return JsonSerializer.makeSparkResponse(500, res, new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private boolean isValidInput(RegisterRequest request) {
        return request != null &&
               request.username != null && !request.username.isEmpty() &&
               request.password != null && !request.password.isEmpty() &&
               request.email != null && !request.email.isEmpty();
    }
}

class RegisterRequest {
    public String username;
    public String password;
    public String email;
}
