package dataaccess;

import model.AuthData;

import java.util.List;

public class AuthDAO {
    private List<AuthData> auths;

    public AuthDAO(List<AuthData> auths){
        this.auths = auths;
    }

    public void clear(){
        auths.clear();
    }

    public void createAuth(){}

    public AuthData getAuth(String authToken) throws DataAccessException{
        for (AuthData auth:auths){
            if (auth.authToken().equals(authToken)){
                return auth;
            }
        }
        throw new DataAccessException("authToken Does not Exist");
    }

    public void deleteAuth(String authToken)throws DataAccessException{
        for (AuthData auth:auths){
            if (auth.authToken().equals(authToken)){
                auths.remove(auth);
            }
        }
        throw new DataAccessException("authToken Does not Exist");
    }
}
