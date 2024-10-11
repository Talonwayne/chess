package handlers;
import dataaccess.AuthDAO;
import dataaccess.UnauthorisedException;

public class Validator {
    public static boolean isValidAuth(String authToken, AuthDAO authDAO) throws UnauthorisedException {
        return authDAO.validateAuth(authToken);
    }
}