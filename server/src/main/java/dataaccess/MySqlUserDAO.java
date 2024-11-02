package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO{
    MySqlHelper helper;

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users(
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            )
            """
    };

    public MySqlUserDAO(){
        try {
            helper = new MySqlHelper(createStatements);
        } catch (Exception e) {
            System.out.println("Error on User Helper");
        }
    }

    public void clear(){
        var statement = "TRUNCATE users";
        try {
            helper.executeUpdate(statement);
        } catch (DataAccessException e) {
            System.out.println("Error on clear");
        }
    }

    public void createUser(String username, String password, String email) throws DataAccessException{
        var statement = "INSERT INTO users (username, password, email) VALUES (?,?,?)";
        if (getUser(username) != null){
            throw new DataAccessException("Username Already Exists");
        }
        try {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            helper.executeUpdate(statement, username, hashedPassword, email);
        } catch (DataAccessException e) {
            throw new DataAccessException("Error creating user: " + e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException{
        var statement = "SELECT * FROM users WHERE username = ?";
        try {
            var resultSet = helper.executeQuery(statement, username);
            if (resultSet.next()) {
                return new UserData(
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    resultSet.getString("email")
                );
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("Error retrieving user: " + e.getMessage());
        } catch (SQLException e) {
            throw new DataAccessException("Error of SQL " + e.getMessage());
        }
    }

    public boolean verifyUser(String username, String providedClearTextPassword) {
        try {
            if (getUser(username) == null){
                return false;
            }
            var hashedPassword = getUser(username).password();
            return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
        } catch (DataAccessException e){
            return false;
        }
    }
}
