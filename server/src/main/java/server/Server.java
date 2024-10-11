package server;

import spark.*;
import handlers.*;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import service.DatabaseService;

public class Server {
    private final DatabaseService databaseService;

    public Server() {
        this.databaseService = new DatabaseService(
            new UserDAO(),
            new GameDAO(),
            new AuthDAO()
        );
        ClearHandler.setDatabaseService(this.databaseService);
        // Initialize other handlers if needed
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.


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
