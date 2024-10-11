package handlers;

import spark.Request;
import spark.Response;
import service.DatabaseService;

public class ClearHandler {
    private static DatabaseService databaseService;

    public static void setDatabaseService(DatabaseService service) {
        databaseService = service;
    }

    public static Object handle(Request req, Response res) {
        try {
            databaseService.clearAllData();
            res.status(200);
            return "{}";
        } catch (Exception e) {
            res.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }
}
