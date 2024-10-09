package server;

import com.google.gson.Gson;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginHandler {
}

record LoginRequest(String username, String password) {}

record LoginResult(String username, String authToken) {}

