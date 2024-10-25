package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void clear();

    AuthData createAuth(String username) throws DataAccessException;

    boolean validateAuth(String authToken) throws DataAccessException;

    String getUsername(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

}
