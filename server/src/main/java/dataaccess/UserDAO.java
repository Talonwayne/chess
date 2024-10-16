package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UserDAO {
    private HashSet<UserData> users;

    public UserDAO(){
        this.users = new HashSet<>();
    }

    public void clear() {
        users.clear();
    }

    public void createUser(String username, String password, String email) throws DataAccessException, UnauthorisedException{
        if (username.isBlank()|| password.isBlank()||email.isBlank()){
            throw new DataAccessException("Missing Parameter");
        }

            for (UserData user : users) {
                if (user.username().equals(username)) {
                    throw new UnauthorisedException("Username already taken");
                }
            }

        users.add(new UserData(username,password,email));
    }

    public UserData getUser(String username) throws UnauthorisedException{
        if(users == null || users.isEmpty()){
            throw new UnauthorisedException("List Users is Empty");
        }
        for (UserData user : users){
            if (user.username().equals(username)){
                return user;
            }
        }
        throw new UnauthorisedException("User not Found");
    }
}
