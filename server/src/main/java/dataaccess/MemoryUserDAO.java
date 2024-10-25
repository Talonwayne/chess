package dataaccess;

import model.UserData;

import java.util.HashSet;

public class MemoryUserDAO {
    private HashSet<UserData> users;

    public MemoryUserDAO(){
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
