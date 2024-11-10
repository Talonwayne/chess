package ui;

import com.google.gson.Gson;
import model.requests.LoginRequest;
import model.requests.RegisterRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import model.*;
import model.responses.LoginResponse;

public class ServerFacade {
    private final String serverUrl;
    private static final Gson GSON = new Gson();
    public ServerFacade(String serverUrl){
        this.serverUrl = serverUrl;
    }

    public LoginResponse register(String username, String password, String email) throws HttpRetryException{
        RegisterRequest rr = new RegisterRequest(username,password,email);
        return this.makeRequest("POST","/user", rr, LoginResponse.class);
    }

    public LoginResponse login(String username, String password) throws HttpRetryException{
        LoginRequest lr = new LoginRequest(username,password);
        return this.makeRequest("POST", "/session", lr, LoginResponse.class);
    }

    public LoginResponse login(String username, String password) throws HttpRetryException{
        LoginRequest lr = new LoginRequest(username,password);
        return this.makeRequest("POST", "/session", lr, LoginResponse.class);
    }


    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws HttpRetryException  {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new HttpRetryException(ex.getMessage(), 500);
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException  {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = GSON.toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, HttpRetryException   {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new HttpRetryException("Not 200", status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = GSON.fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
