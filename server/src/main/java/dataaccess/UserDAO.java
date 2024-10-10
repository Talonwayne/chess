package dataaccess;

import model.UserData;

import java.util.List;

public class UserDAO {
    private List<UserData> users;

    public UserDAO(List<UserData> users){
        this.users = users;
    }

    public void clear(){
        users.clear();
    }

    public void createUser(String username, String password, String email) throws DataAccessException{
        if (username.isBlank()|| password.isBlank()||email.isBlank()){
            throw new DataAccessException("Missing New User Parameter");
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
