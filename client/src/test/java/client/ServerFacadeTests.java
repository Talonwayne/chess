package client;

import model.responses.CreateGameResponse;
import model.responses.ListGamesResponse;
import model.responses.LoginResponse;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import org.junit.jupiter.api.Test;

import java.net.HttpRetryException;

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

    private LoginResponse _testRegister() {
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
    public void testLogout() {
        try {
            LoginResponse response = _testRegister();
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
    public void testRegister() {
        try {
            _testRegister();
        } catch (Exception e){
            fail("Register failed");
        }
    }

    @Test
    public void testLogin() {
        try {
            LoginResponse response = _testRegister();
            serverFacade.logout(response.authToken());
            LoginResponse response2 = serverFacade.login("user","password");
            assertNotNull(response2.authToken());
        } catch (Exception e){
            fail("Login failed");
        }
    }

    @Test
    public void testCreate() {
        try {
            LoginResponse response = _testRegister();
            CreateGameResponse cr = serverFacade.create(response.authToken(),"testName");
            assertNotNull(cr);
        } catch (Exception e){
            fail("Create failed");
        }
    }

    @Test
    public void testList() {
        try {
            LoginResponse response = _testRegister();
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
    public void testJoin() {
        try {
            LoginResponse response = _testRegister();
            CreateGameResponse cr1 = serverFacade.create(response.authToken(),"testName1");
            serverFacade.join(response.authToken(),cr1.gameID(),"WHITE");
        } catch (Exception e){
            fail("Join failed");
        }
    }
}
