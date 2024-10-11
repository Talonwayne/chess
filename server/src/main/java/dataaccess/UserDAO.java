package dataaccess;

import model.UserData;
import java.util.Set;

public class UserDAO {
    private Set<UserData> users;

    public UserDAO(){
    }

    public void clear() {
        users.clear();
    }

    public void createUser(String username, String password, String email) throws DataAccessException{
        if (username.isBlank()|| password.isBlank()||email.isBlank()){
            throw new DataAccessException("Missing Parameter");
        }
        for (UserData user:users){
            if (user.username().equals(username)){
                throw new DataAccessException("Username already taken");
            }
        }
        users.add(new UserData(username,password,email));
    }

    public UserData getUser(String username) throws DataAccessException{
        for (UserData user : users){
            if (user.username().equals(username)){
                return user;
            }
        }
        throw new DataAccessException("User not Found");
    }

}
