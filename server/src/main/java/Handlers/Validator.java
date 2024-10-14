package handlers;
import dataaccess.AuthDAO;
import dataaccess.UnauthorisedException;

public class Validator {
    public static void isValidAuth(String authToken, AuthDAO authDAO) throws UnauthorisedException {
        authDAO.validateAuth(authToken);
    }
}