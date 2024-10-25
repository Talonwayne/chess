package dataaccess;

public class MySqlAuthDAO implements AuthDAO{
    void clear(){}

    void createAuth(String username){}

    boolean validateAuth(String authToken) throws DataAccessException {
        return false;
    }
}
