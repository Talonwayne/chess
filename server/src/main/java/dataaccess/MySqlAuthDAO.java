package dataaccess;

import model.AuthData;

public class MySqlAuthDAO implements AuthDAO{
    public void clear(){

    }

    public AuthData createAuth(String username){
        return new AuthData("not","done");
    }

    public boolean validateAuth(String authToken) throws DataAccessException {
        return false;
    }

    public void deleteAuth(String authToken) throws DataAccessException{

    }

    public String getUsername(String authToken){
        return "not done";
    }
}
