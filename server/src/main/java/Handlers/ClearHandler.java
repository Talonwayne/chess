package handlers;

import model.responses.ErrorResponse;
import service.Service;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {
    private static Service service;

    public static void setService(Service hservice) {service = hservice;}

    @Override
    public Object handle(Request req, Response res) {
        res.type("application/json");
        try {
            service.clearAllData();
            return "{}";
        } catch (Exception e) {
            return JsonSerializer.makeSparkResponse(500, res, new ErrorResponse(e.getMessage()));
        }
    }
}
