package handlers;

import com.google.gson.Gson;
import spark.Response;

public class JsonSerializer {
    private static final Gson GSON = new Gson();

    public static <T> T fromJson(String json, Class<T> classIWant) {
        return GSON.fromJson(json, classIWant);
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static String makeSparkResponse(int status, Response res, Object obj){
        res.status(status);
        return toJson(obj);
    }

}