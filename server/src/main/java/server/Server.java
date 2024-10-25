package server;

import service.Service;
import spark.*;
import handlers.*;

public class Server {
    private final Service service;

    public Server(Service service) {
        this.service = service;
        giveHandlersTheService();
    }

    private void giveHandlersTheService() {
        RegisterHandler.setService(service);
        LoginHandler.setService(service);
        LogoutHandler.setService(service);
        ListGamesHandler.setService(service);
        CreateGameHandler.setService(service);
        JoinGameHandler.setService(service);
        ClearHandler.setService(service);
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
        Spark.delete("/db", new ClearHandler());

        Spark.init();
        Spark.awaitInitialization();
        System.out.println("Listening on port " + desiredPort);
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }
}
