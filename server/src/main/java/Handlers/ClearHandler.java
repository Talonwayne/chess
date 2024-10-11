package handlers;

import spark.Request;
import spark.Response;
import service.DatabaseService;
import java.util.HashMap;
import java.util.Map;

public class ClearHandler {
    private static DatabaseService databaseService;

    public static void setDatabaseService(DatabaseService service) {
        databaseService = service;
    }

    public static Object handle(Request req, Response res) {
        res.type("application/json");
        try {
            databaseService.clearAllData();
            return "{}";
        } catch (Exception e) {
            return JsonSerializer.makeSparkResponse(500, res, Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
