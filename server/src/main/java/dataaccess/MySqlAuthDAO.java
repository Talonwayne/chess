package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;

public class MySqlAuthDAO implements AuthDAO{
    MySqlHelper helper;
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths(
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (authToken)
            )
            """
    };
    public MySqlAuthDAO(){
        try {
            helper = new MySqlHelper(createStatements);
        }catch (Exception e){
            int i = 1;
        }
    }

    public void clear(){
        var statement = "TRUNCATE auths";
        try {
            helper.executeUpdate(statement);
        } catch (DataAccessException e){
            int i = 1;
        }
    }

    public AuthData createAuth(String username) throws DataAccessException{
        var statement = "INSERT INTO auths (authToken, username) VALUES (?,?)";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        try{
            var id = helper.executeUpdate(statement, authData.authToken(), authData.username());
        } catch (DataAccessException e ){
            int i = 1;
        }
        return authData;
    }

    public boolean validateAuth(String authToken) throws DataAccessException {
        var statement = "SELECT COUNT(1) AS token_exists FROM auths WHERE authToken = ?";
        try (var resultSet = helper.executeQuery(statement, authToken)) {
            if (resultSet.next()) {
                int count = resultSet.getInt("token_exists");
                return count == 1;
            }
            return false;
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Error checking authToken: " + e.getMessage());
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auths WHERE authToken = ?"; // Use parameterized query
        try {
            helper.executeUpdate(statement, authToken); // Pass authToken as parameter
        } catch (DataAccessException e) {
            throw new DataAccessException("Error deleting auth token: " + e.getMessage());
        }
    }

    public String getUsername(String authToken) throws DataAccessException{
        var statement = "SELECT username FROM auths WHERE authToken=?";
        try {
            var resultSet = helper.executeQuery(statement, authToken);
            if (resultSet.next()) {
                return resultSet.getString("username"); 
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("Error retrieving username: " + e.getMessage());
        } catch (SQLException e) {
            throw new DataAccessException("Error of SQL " + e.getMessage());
        }
        return null;
    }
}
