package server;

import spark.*;
import handlers.*;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import service.DatabaseService;

public class Server {
    public Handlers handlers;
    public Server() {

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        
        Spark.delete("/db", ClearHandler::handle);
        Spark.post("/user", RegisterHandler::handle);
        Spark.post("/session", Handlers.LoginHandler::handle);
        Spark.delete("/session", Handlers.LogoutHandler::handle);
        Spark.get("/game", Handlers.ListGamesHandler::handle);
        Spark.post("/game", Handlers.CreateGameHandler::handle);
        Spark.put("/game", Handlers.JoinGameHandler::handle);

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
