package handlers;

import spark.Request;
import spark.Response;
import com.google.gson.Gson;

public class Handlers {
    private static final Gson gson = new Gson();

    private static <T> T deserialize(Request req, Class<T> clazz) {
        return gson.fromJson(req.body(), clazz);
    }

    private static String serialize(Object obj) {
        return gson.toJson(obj);
    }

    private static void setResponseStatus(Response res, int status) {
        res.status(status);
    }

    private static String errorResponse(int status, String message) {
        setResponseStatus(res, status);
        return serialize(new Response.ErrorResponse(message));
    }
}
