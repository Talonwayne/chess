package service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import dataaccess.*;
import model.*;

import java.nio.file.FileAlreadyExistsException;
import java.util.HashSet;

public class ServiceTests {
    private UserService userService;
    private GameService gameService;
    private DatabaseService databaseService;

    @BeforeEach
    public void setUp() {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(userDAO, authDAO, gameDAO);
        databaseService = new DatabaseService(userDAO, gameDAO, authDAO);
    }

    @AfterEach
    public void tearDown() {
        databaseService.clearAllData();
    }

    // UserService Tests

    @Test
    public void testRegisterPositive() throws Exception {
        AuthData authData = userService.register("testUser", "password", "test@example.com");
        assertNotNull(authData);
        assertEquals("testUser", authData.username());
    }

    @Test
    public void testRegisterNegative() {
        assertThrows(UnauthorisedException.class, () -> {
            userService.register("testUser", "password", "test@example.com");
            userService.register("testUser", "password", "test@example.com"); // Duplicate registration
        });
    }

    @Test
    public void testLoginPositive() throws Exception {
        userService.register("testUser", "password", "test@example.com");
        AuthData authData = userService.login("testUser", "password");
        assertNotNull(authData);
        assertEquals("testUser", authData.username());
    }

    @Test
    public void testLoginNegative() {
        assertThrows(UnauthorisedException.class, () -> {
            userService.login("nonexistentUser", "wrongPassword");
        });
    }

    @Test
    public void testLogoutPositive() throws Exception {
        AuthData authData = userService.register("testUser", "password", "test@example.com");
        assertDoesNotThrow(() -> userService.logout(authData.authToken()));
    }

    @Test
    public void testLogoutNegative() {
        assertThrows(DataAccessException.class, () -> {
            userService.logout("invalidAuthToken");
        });
    }

    // GameService Tests

    @Test
    public void testCreateGamePositive() throws Exception {
        int gameId = gameService.createGame("TestGame");
        assertTrue(gameId > 0);
    }

    @Test
    public void testCreateGameNegative() {
        assertThrows(FileAlreadyExistsException.class, () -> {
            gameService.createGame("TestGame");
            gameService.createGame("TestGame"); // Duplicate game name
        });
    }

    @Test
    public void testJoinGamePositive() throws Exception {
        AuthData authData = userService.register("testUser", "password", "test@example.com");
        int gameId = gameService.createGame("TestGame");
        assertDoesNotThrow(() -> gameService.joinGame(authData.authToken(), "WHITE", gameId));
    }

    @Test
    public void testJoinGameNegative() throws Exception {
        AuthData authData = userService.register("testUser", "password", "test@example.com");
        int gameId = gameService.createGame("TestGame");
        gameService.joinGame(authData.authToken(), "WHITE", gameId);
        assertThrows(FileAlreadyExistsException.class, () -> {
            gameService.joinGame(authData.authToken(), "WHITE", gameId); // Try to join as WHITE again
        });
    }

    @Test
    public void testListGamesPositive() throws Exception {
        gameService.createGame("Game1");
        gameService.createGame("Game2");
        HashSet<GameData> games = gameService.listGames();
        assertEquals(2, games.size());
    }

    @Test
    public void testListGamesNegative() {
        HashSet<GameData> games = gameService.listGames();
        assertTrue(games.isEmpty());
    }

    // DatabaseService Test

    @Test
    public void testClearAllDataPositive() throws Exception {
        userService.register("testUser", "password", "test@example.com");
        gameService.createGame("TestGame");
        databaseService.clearAllData();
        assertTrue(gameService.listGames().isEmpty());
        assertThrows(UnauthorisedException.class, () -> userService.login("testUser", "password"));
    }
}
