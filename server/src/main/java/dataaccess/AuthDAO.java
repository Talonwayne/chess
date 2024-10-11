package dataaccess;

import model.AuthData;
import java.util.List;
import java.util.UUID;

public  class AuthDAO {
    private List<AuthData> auths;

    public  AuthDAO(){
    }

    public void clear(){
        auths.clear();
    }

    public AuthData createAuth(String username){
        String authtoken = UUID.randomUUID().toString();
        AuthData newauth = new AuthData(authtoken, username);
        auths.add(newauth); 
        return newauth;
    }

    public  boolean validateAuth(String authToken) throws UnauthorisedException{
        for (AuthData auth:auths){
            if (auth.authToken().equals(authToken)){
                return true;
            }
        }
        throw new UnauthorisedException("authToken Does not Exist");
    }

    public  void deleteAuth(String authToken)throws DataAccessException{
        for (AuthData auth:auths){
            if (auth.authToken().equals(authToken)){
                auths.remove(auth);
                return;
            }
        }
        throw new DataAccessException("authToken Does not Exist");
    }

    public String getUsername(String authToken) throws UnauthorisedException{
        for (AuthData auth:auths){
            if(auth.authToken().equals(authToken)){
                return auth.username();
            }
        }
        throw new UnauthorisedException("Authtoken Does not Exist");
    }
}
