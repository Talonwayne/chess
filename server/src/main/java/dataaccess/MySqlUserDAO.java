package dataaccess;

import model.UserData;
import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO{
    MySqlHelper helper;

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(username),
              INDEX(password),
              INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public MySqlUserDAO(){
        try {
            helper = new MySqlHelper(createStatements);
            helper.configureDatabase();
        } catch (Exception e) {
            int i = 1;
        }
    }

    public void clear(){
        var statement = "TRUNCATE users";
        try {
            helper.executeUpdate(statement);
        } catch (DataAccessException e) {
            int i = 1;
        }
    }

    public void createUser(String username, String password, String email) throws DataAccessException{
        var statement = "INSERT INTO users (username, password, email) VALUES (?,?,?)";
        try {
            helper.executeUpdate(statement, username, password, email);
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
                throw new DataAccessException("User not Found");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("Error retrieving user: " + e.getMessage());
        } catch (SQLException e) {
            throw new DataAccessException("Error of SQL " + e.getMessage());
        }
    }
}
