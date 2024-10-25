package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public  class MemoryAuthDAO implements AuthDAO{
    private List<AuthData> auths;

    public  MemoryAuthDAO(){
        this.auths = new ArrayList<>();
    }

    public void clear(){
        auths.clear();
    }

    public AuthData createAuth(String username){
        String authToken = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(authToken, username);
        auths.add(newAuth);
        return newAuth;
    }

    public  boolean validateAuth(String authToken) throws DataAccessException{
        if (auths == null || auths.isEmpty()){
            throw new DataAccessException("authToken Does not Exist");
        }
        for (AuthData auth:auths){
            if (auth.authToken().equals(authToken)){
                return true;
            }
        }
        throw new DataAccessException("authToken Does not Exist");
    }

    public  void deleteAuth(String authToken)throws DataAccessException{
        if (auths == null || auths.isEmpty()){
            throw new DataAccessException("authToken Does not Exist");
        }
        for (AuthData auth:auths){
            if (auth.authToken().equals(authToken)){
                auths.remove(auth);
                return;
            }
        }
        throw new DataAccessException("authToken Does not Exist");
    }

    public String getUsername(String authToken) throws DataAccessException{
        if (auths == null || auths.isEmpty()){
            throw new DataAccessException("authToken Does not Exist");
        }
        for (AuthData auth:auths){
            if(auth.authToken().equals(authToken)){
                return auth.username();
            }
        }
        throw new DataAccessException("authToken Does not Exist");
    }
}
