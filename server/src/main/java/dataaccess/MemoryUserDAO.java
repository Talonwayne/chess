package dataaccess;

import model.UserData;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO{
    private HashSet<UserData> users;

    public MemoryUserDAO(){
        this.users = new HashSet<>();
    }

    public void clear() {
        users.clear();
    }

    public void createUser(String username, String password, String email) throws DataAccessException{
        if (username.isBlank()|| password.isBlank()||email.isBlank()){
            throw new DataAccessException("Missing Parameter");
        }
        for (UserData user : users) {
            if (user.username().equals(username)) {
                throw new DataAccessException("Username already taken");
            }
        }
        users.add(new UserData(username,password,email));
    }

    public UserData getUser(String username) throws DataAccessException{
        if(users == null || users.isEmpty()){
            throw new DataAccessException("List Users is Empty");
        }
        for (UserData user : users){
            if (user.username().equals(username)){
                return user;
            }
        }
        throw new DataAccessException("User not Found");
    }
}
