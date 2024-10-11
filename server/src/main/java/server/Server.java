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

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (req, res) -> new RegisterHandler().handle(req, res));
        Spark.post("/session", (req, res) -> new LoginHandler().handle(req, res));
        Spark.delete("/session", (req, res) -> new LogoutHandler().handle(req, res));
        Spark.get("/game", (req, res) -> new ListGamesHandler().handle(req, res));
        Spark.post("/game", (req, res) -> new CreateGameHandler().handle(req, res));
        Spark.put("/game", (req, res) -> new JoinGameHandler().handle(req, res));
        Spark.delete("/db", ClearHandler::handle);

        Spark.exception(Exception.class, (e, req, res) -> {
            res.status(500);
            res.body("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        });

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
