package service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import dataaccess.*;
import model.*;

import java.nio.file.FileAlreadyExistsException;
import java.util.HashSet;

public class ServiceTests {
    private Service service;

    @BeforeEach
    public void setUp() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        service = new Service(userDAO, gameDAO, authDAO);
    }

    @AfterEach
    public void tearDown() {
        service.clearAllData();
    }
    

    @Test
    public void testRegisterPositive() throws Exception {
        AuthData authData = service.register("testUser", "password", "test@example.com");
        assertNotNull(authData);
        assertEquals("testUser", authData.username());
    }

    @Test
    @DisplayName("Duplicate Registration")
    public void testRegisterNegative() {
        assertThrows(DataAccessException.class, () -> {
            service.register("testUser", "password", "test@example.com");
            service.register("testUser", "password", "test@example.com");
        });
    }

    @Test
    public void testLoginPositive() throws Exception {
        AuthData auth =  service.register("testUser", "password1", "test@example.com");
        service.logout(auth.authToken());
        AuthData authData = service.login("testUser", "password1");
        assertNotNull(authData);
        assertEquals("testUser", authData.username());
    }

    @Test
    public void testLoginNegative() {
        assertThrows(NullPointerException.class, () -> service.login("nonexistentUser", "wrongPassword"));
    }

    @Test
    public void testLogoutPositive() throws Exception {
        AuthData authData = service.register("testUser", "password", "test@example.com");
        assertDoesNotThrow(() -> service.logout(authData.authToken()));
    }

    @Test
    public void testLogoutNegative() {
        assertThrows(DataAccessException.class, () -> service.logout("invalidAuthToken"));
    }

    // service Tests

    @Test
    public void testCreateGamePositive() throws Exception {
        int gameId = service.createGame("TestGame");
        assertTrue(gameId > 0);
    }

    @Test
    public void testCreateGameNegative() {
        assertThrows(DataAccessException.class, () -> {
            service.createGame("TestGame");
            service.createGame("TestGame"); // Duplicate game name
        });
    }

    @Test
    public void testJoinGamePositive() throws Exception {
        AuthData authData = service.register("testUser", "password", "test@example.com");
        int gameId = service.createGame("TestGame");
        assertDoesNotThrow(() -> service.joinGame(authData.authToken(), "WHITE", gameId));
    }

    @Test
    public void testJoinGameNegative() throws Exception {
        AuthData authData = service.register("testUser", "password", "test@example.com");
        int gameId = service.createGame("TestGame");
        service.joinGame(authData.authToken(), "WHITE", gameId);
        assertThrows(FileAlreadyExistsException.class, () -> {
            service.joinGame(authData.authToken(), "WHITE", gameId); // Try to join as WHITE again
        });
    }

    @Test
    public void testListGamesPositive() throws Exception {
        service.createGame("Game1");
        service.createGame("Game2");
        HashSet<GameData> games = service.listGames();
        assertEquals(2, games.size());
    }

    @Test
    public void testListGamesNegative() {
        try {
            HashSet<GameData> games = service.listGames();
            assertTrue(games.isEmpty());
        }catch (DataAccessException e){
            fail();
        }
    }



    @Test
    public void testClearAllDataPositive() throws Exception {
        AuthData authData = service.register("testUser", "password", "test@example.com");
        service.createGame("TestGame");
        service.clearAllData();
        assertTrue(service.listGames().isEmpty());
        assertThrows(NullPointerException.class, () -> service.login("testUser", "password"));
        assertDoesNotThrow(() -> service.createGame("TestGame"));
        assertThrows(DataAccessException.class, () -> service.isValidAuth(authData.authToken()));
    }
}