package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void clear() throws DataAccessException;

    AuthData createAuth(String username) throws DataAccessException;

    boolean validateAuth(String authToken) throws DataAccessException;



}
