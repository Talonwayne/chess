package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;

public class MySqlAuthDAO implements AuthDAO{
    MySqlHelper helper;
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `id` int NOT NULL AUTO_INCREMENT,
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(authToken),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
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
        var statement = "SELECT EXISTS(SELECT 1 FROM auths WHERE authToken = '" +authToken +"') AS object_exists";
        try{
            return helper.executeUpdate(statement) == 1;
        } catch (DataAccessException e ){
            return false;
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        var statement = "DELETE FROM auths\n" +
                        "WHERE authToken = " + authToken;
        var id = helper.executeUpdate(statement);
    }

    public String getUsername(String authToken){
        var statement = "SELECT username FROM auths WHERE authToken=?";
        return "not done";
    }


}
