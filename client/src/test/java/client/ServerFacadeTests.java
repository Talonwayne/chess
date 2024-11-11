package client;

import chess.ChessGame;
import model.GameData;
import model.responses.CreateGameResponse;
import model.responses.ListGamesResponse;
import model.responses.LoginResponse;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import org.junit.jupiter.api.Test;

import java.net.HttpRetryException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        serverFacade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void clearDatabase(){
        try {
            serverFacade.clear();
        } catch (HttpRetryException e) {
            fail("Clear Failed");
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    private LoginResponse testRegister() {
        try {
            LoginResponse response = serverFacade.register("user", "password", "email");
            assertNotNull(response.authToken());
            assertEquals(response.username(),"user");
            return response;
        } catch (Exception e){
            fail("Register failed");
            return null;
        }
    }

    @Test
    public void posTestLogout() {
        try {
            LoginResponse response = testRegister();
            serverFacade.logout(response.authToken());
        } catch (Exception e){
            fail("Logout failed");
        }
        try {
            serverFacade.login("user","password");
        } catch (Exception e){
            assertTrue(true);
        }
    }

    @Test
    public void negTestLogout() {
        try {
            serverFacade.logout(null);
            fail("Did not throw an error when one was expected");
        } catch (Exception e){
            assertTrue(true);
        }
    }

    @Test
    public void posTestRegister() {
        try {
            testRegister();
        } catch (Exception e){
            fail("Register failed");
        }
    }

    @Test
    public void negTestRegister() {
        try {
            testRegister();
        } catch (Exception e){
            fail("Register failed");
        }
        try {
            testRegister();
            fail("Failed to Catch duplicate Registration");
        } catch (Exception e){
            assertTrue(true);
        }
    }

    @Test
    public void posTestLogin() {
        try {
            LoginResponse response = testRegister();
            serverFacade.logout(response.authToken());
            LoginResponse response2 = serverFacade.login("user","password");
            assertNotNull(response2.authToken());
        } catch (Exception e){
            fail("Login failed");
        }
    }

    @Test
    public void negTestLogin() {
        try {
            LoginResponse response = testRegister();
            serverFacade.logout(response.authToken());
            LoginResponse response2 = serverFacade.login("u","p");
            fail("Failed to stop faulty logins");
        } catch (Exception e){
            assertTrue(true);
        }
    }

    @Test
    public void posTestCreate() {
        try {
            LoginResponse response = testRegister();
            CreateGameResponse cr = serverFacade.create(response.authToken(),"testName");
            assertNotNull(cr);
        } catch (Exception e){
            fail("Create failed");
        }
    }

    @Test
    public void negTestCreate() {
        LoginResponse response = testRegister();
        try {
            CreateGameResponse cr = serverFacade.create(response.authToken(),"testName");
            assertNotNull(cr);
        } catch (Exception e){
            fail("Create failed");
        }
        try {
            CreateGameResponse cr = serverFacade.create(response.authToken(),"testName");
            fail("Create worked on duplicate names");
        } catch (Exception e){
            assertTrue(true);
        }
    }

    @Test
    public void posTestList() {
        try {
            LoginResponse response = testRegister();
            CreateGameResponse cr1 = serverFacade.create(response.authToken(),"testName1");
            CreateGameResponse cr2 = serverFacade.create(response.authToken(),"testName2");
            CreateGameResponse cr3 = serverFacade.create(response.authToken(),"testName3");
            CreateGameResponse cr4 = serverFacade.create(response.authToken(),"testName4");
            CreateGameResponse cr5 = serverFacade.create(response.authToken(),"testName5");
            ListGamesResponse listGamesResponse = serverFacade.list(response.authToken());
            assertEquals(listGamesResponse.games().size(), 5);
        } catch (Exception e){
            fail("List failed");
        }
    }

    @Test
    public void negTestList() {
        try {
            LoginResponse response = testRegister();
            ListGamesResponse listGamesResponse = serverFacade.list(response.authToken());
            assertEquals(listGamesResponse.games().size(), 0);
        } catch (Exception e){
            fail("List failed");
        }
    }

    @Test
    public void posTestJoin() {
        try {
            LoginResponse response = testRegister();
            CreateGameResponse cr1 = serverFacade.create(response.authToken(),"testName1");
            serverFacade.join(response.authToken(),cr1.gameID(),"WHITE");
            GameData expectedGame = new GameData(cr1.gameID(),response.username(),null,"testName1", null);
            HashSet<GameData> games = serverFacade.list(response.authToken()).games();
            boolean gameFound = false;
            for (GameData game : games) {
                 gameFound = game.equals(expectedGame);
            }
            assertTrue(gameFound);
        } catch (Exception e){
            fail("Join failed");
        }
    }

    @Test
    public void negTestJoin() {
        try {
            LoginResponse response = testRegister();
            CreateGameResponse cr1 = serverFacade.create(response.authToken(),"testName1");
            serverFacade.join(response.authToken(),cr1.gameID(),"WHITE");
            serverFacade.join(response.authToken(),cr1.gameID(),"WHITE");
            fail("Join failed to catch duplicate White Joins");
        } catch (Exception e){
            assertTrue(true);
        }
    }
}
