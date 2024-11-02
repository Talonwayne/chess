package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import server.Server;

import java.util.HashSet;

public class DataAccessTests {
    private MySqlUserDAO userDAO;
    private MySqlAuthDAO authDAO;
    private MySqlGameDAO gameDAO;
    private static Server server;

    @BeforeAll
    public static void startServer() {
        server = new Server();
        var port = server.run(1234);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void setUp() {
        userDAO = new MySqlUserDAO();
        authDAO = new MySqlAuthDAO();
        gameDAO = new MySqlGameDAO();
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    // MySqlUserDAO Tests
    @Test
    public void testUserClearPositive() throws DataAccessException {
        userDAO.createUser("testUser", "password", "test@example.com");
        userDAO.clear();
        assertNull(userDAO.getUser("testUser"));
    }

    @Test
    public void testAuthClearPositive() throws DataAccessException {
        AuthData auth =  authDAO.createAuth("Test");
        authDAO.clear();
        assertNull(authDAO.getUsername(auth.authToken()));
    }

    @Test
    public void testGameClearPositive() throws DataAccessException {
        int gameID = gameDAO.createGame("test");
        gameDAO.clear();
        assertNull(gameDAO.getGame(gameID));
    }

    @Test
    public void testUserClearNegative() throws DataAccessException {
        userDAO.createUser("testUser", "password", "test@example.com");
        userDAO.clear();
        userDAO.createUser("testUser", "password", "test@example.com");
        assertNotNull(userDAO.getUser("testUser"));
    }

    @Test
    public void testAuthClearNegative() throws DataAccessException {
        authDAO.createAuth("Test");
        authDAO.clear();
        AuthData auth =  authDAO.createAuth("Test");
        assertNotNull(authDAO.getUsername(auth.authToken()));
    }

    @Test
    public void testGameClearNegative() throws DataAccessException {
        gameDAO.createGame("test");
        gameDAO.clear();
        int gameID = gameDAO.createGame("test");
        assertNotNull(gameDAO.getGame(gameID));
    }


    @Test
    public void testCreateUserPositive() throws DataAccessException {
        userDAO.createUser("testUser", "password", "test@example.com");
        UserData user = userDAO.getUser("testUser");
        assertNotNull(user);
        assertEquals("testUser", user.username());
    }

    @Test
    public void testCreateUserNegative() {
        try{
            userDAO.createUser("testUser", "password", "test@example.com");
        } catch (DataAccessException e){
            assertFalse(true);
        }
        try{
            userDAO.createUser("testUser", "password", "test@example.com");
        } catch (DataAccessException e){
            assertFalse(false);
        }
    }

    @Test
    public void testVerifyUserPositive() throws DataAccessException {
        userDAO.createUser("testUser", "password", "test@example.com");
        assertTrue(userDAO.verifyUser("testUser", "password"));
    }

    @Test
    public void testVerifyUserNegative() throws DataAccessException {
        userDAO.createUser("testUser", "password", "test@example.com");
        assertFalse(userDAO.verifyUser("testUser", "wrongPassword")); // Wrong password
    }

    // MySqlAuthDAO Tests
    @Test
    public void testCreateAuthPositive() throws DataAccessException {
        try {
            AuthData auth = authDAO.createAuth("testUser");
            assertTrue(authDAO.validateAuth(auth.authToken()));
        }catch (DataAccessException e){
            assertFalse(false);
        }
    }

    @Test
    public void testCreateAuthNegative() throws DataAccessException{
        AuthData auth = authDAO.createAuth("testUser1");
        AuthData auth2 = authDAO.createAuth("testUser2");
        assertNotEquals(auth.authToken(),auth2.authToken());
    }

    @Test
    public void testValidateAuthPositive() throws DataAccessException {
        AuthData authData = authDAO.createAuth("testUser");
        assertTrue(authDAO.validateAuth(authData.authToken()));
    }

    @Test
    public void testValidateAuthNegative() throws DataAccessException {
        assertFalse(authDAO.validateAuth("invalidToken")); // Invalid token
    }

    // MySqlGameDAO Tests
    @Test
    public void testCreateGamePositive() throws DataAccessException {
        int gameId = gameDAO.createGame("TestGame");
        assertTrue(gameId > 0);
    }

    @Test
    public void testCreateGameNegative() {
        try {
            gameDAO.createGame("1");
            gameDAO.createGame("1");
            assertFalse(true);
        }catch (DataAccessException e){
            assertFalse(false);
        }
    }

    @Test
    public void testGetGamePositive() throws DataAccessException {
        int gameId = gameDAO.createGame("TestGame");
        GameData game = gameDAO.getGame(gameId);
        assertNotNull(game);
        assertEquals("TestGame", game.gameName());
    }

    @Test
    public void testGetGameNegative() throws DataAccessException{
        assertNull(gameDAO.getGame(999)); // Non-existent game ID
    }

    @Test
    public void testListGamesPositive() throws DataAccessException {
        int gameId1 = gameDAO.createGame("TestGame1");
        int gameId2 = gameDAO.createGame("TestGame2");
        int gameId3 = gameDAO.createGame("TestGame3");
        HashSet<GameData> games = gameDAO.listGames();
        assertEquals(games.size(),3);
    }

    @Test
    public void testListGamesNegative() throws DataAccessException{
        int gameId1 = gameDAO.createGame("TestGame1");
        int gameId2 = gameDAO.createGame("TestGame2");
        int gameId3 = gameDAO.createGame("TestGame3");
        assertNotNull(gameDAO.listGames());
    }

    @Test
    public void testUpdateGamePositive() throws DataAccessException {
        int gameId = gameDAO.createGame("TestGame1");
        ChessGame newGame = new ChessGame();
        GameData updatedGame = new GameData(gameId, "white", "black", "TestGame1",newGame);
        gameDAO.updateGame(gameId,updatedGame);
        GameData databaseGame = gameDAO.getGame(gameId);
        assertEquals(updatedGame,databaseGame);
    }

    @Test
    public void testUpdateGameNegative() throws DataAccessException {
        int gameId = gameDAO.createGame("TestGame1");
        GameData oldGame = gameDAO.getGame(gameId);
        ChessGame newGame = new ChessGame();
        GameData updatedGame = new GameData(gameId, "white", "black", "TestGame1",newGame);
        gameDAO.updateGame(gameId,updatedGame);
        GameData databaseGame = gameDAO.getGame(gameId);
        assertNotEquals(oldGame,databaseGame);
    }
}
