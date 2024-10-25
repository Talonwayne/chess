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
}
