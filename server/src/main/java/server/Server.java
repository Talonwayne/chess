package server;

import spark.*;
import handlers.*;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import service.DatabaseService;
import service.UserService;
import service.GameService;

public class Server {
    private final DatabaseService databaseService;

    public Server() {
        this.databaseService = new DatabaseService(
            new UserDAO(),
            new GameDAO(),
            new AuthDAO()
        );
        ClearHandler.setDatabaseService(this.databaseService);
        initializeServices();
    }

    private void initializeServices() {
        UserService userService = new UserService(databaseService.getUserDAO(), databaseService.getAuthDAO());
        GameService gameService = new GameService(databaseService.getUserDAO(), databaseService.getAuthDAO(), databaseService.getGameDAO());

        RegisterHandler.setUserService(userService);
        LoginHandler.setUserService(userService);
        LogoutHandler.setUserService(userService);
        ListGamesHandler.setUserService(userService);
        ListGamesHandler.setGameService(gameService);
        CreateGameHandler.setUserService(userService);
        CreateGameHandler.setGameService(gameService);
        JoinGameHandler.setUserService(userService);
        JoinGameHandler.setGameService(gameService);
    }

    public void setDataAccess(boolean sql){

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", new RegisterHandler());
        Spark.post("/session", new LoginHandler());
        Spark.delete("/session", new LogoutHandler());
        Spark.get("/game", new ListGamesHandler());
        Spark.post("/game", new CreateGameHandler());
        Spark.put("/game", new JoinGameHandler());
        Spark.delete("/db", ClearHandler::handle); 

        Spark.init();
        Spark.awaitInitialization();
        System.out.println("Listening on port " + desiredPort);
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
