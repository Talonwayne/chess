package dataaccess;

import model.AuthData;
import model.UserData;
import java.util.List;
import java.util.UUID;

public  class AuthDAO {
    private List<AuthData> auths;

    public  AuthDAO(){
    }

    public void clear(){
        auths.clear();
    }

    public String createAuth(UserData user){
        String authtoken = UUID.randomUUID().toString();
        auths.add(new AuthData(authtoken, user.username()));
        return authtoken;
    }

    public  AuthData getAuth(String authToken) throws DataAccessException{
        for (AuthData auth:auths){
            if (auth.authToken().equals(authToken)){
                return auth;
            }
        }
        throw new DataAccessException("authToken Does not Exist");
    }

    public  void deleteAuth(String authToken)throws DataAccessException{
        for (AuthData auth:auths){
            if (auth.authToken().equals(authToken)){
                auths.remove(auth);
            }
        }
        throw new DataAccessException("authToken Does not Exist");
    }
}
