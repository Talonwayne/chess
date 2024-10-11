package service;

import model.AuthData;
import model.UserData;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UnauthorisedException;
public class UserService {

    public UserDAO userDAO;
    public AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthDAO getAuthDAO() {
        return authDAO;
    }

    public AuthData register(String username, String password, String email) throws DataAccessException, UnauthorisedException {
        userDAO.createUser(username, password, email);
        return login(username, password);
    }
    public AuthData login(String username, String password) throws DataAccessException, UnauthorisedException {
        UserData user = userDAO.getUser(username);
        if (user.password().equals(password)) {
            return authDAO.createAuth(username);
        }
        throw new UnauthorisedException("Invalid username or password");
    }

    public void logout(String authorizationToken) throws DataAccessException {
        authDAO.deleteAuth(authorizationToken);
    }
}
