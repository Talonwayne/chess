package dataaccess;

import model.UserData;

public class MySqlUserDAO implements UserDAO{
    public void clear(){

    }

    public void  createUser(String username, String password, String email) throws DataAccessException{

    }

    public UserData getUser(String username) throws DataAccessException{
        return new UserData("","","");

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(username),
              INDEX(password)
              INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
