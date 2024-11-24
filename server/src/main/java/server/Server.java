package server;

import dataaccess.*;
import server.websocket.WebSocketHandler;
import service.Service;
import spark.*;
import handlers.*;

public class Server {
    private Service service;
    private WebSocketHandler webSocketHandler;

    public Server() {
        try{
            DatabaseManager.createDatabase();
        } catch (DataAccessException e){
            System.out.print("Database not made");
        }
        UserDAO userDAO = new MySqlUserDAO();
        GameDAO gameDAO = new MySqlGameDAO();
        AuthDAO authDAO = new MySqlAuthDAO();
        service = new Service(userDAO,gameDAO,authDAO);
        webSocketHandler = new WebSocketHandler(service);
        giveHandlersTheService();
    }
    public void setService(Service hservice){
        service = hservice;
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
        Spark.webSocket("/ws", webSocketHandler);

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
