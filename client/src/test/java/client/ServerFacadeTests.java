package client;

import org.junit.jupiter.api.*;
import server.Server


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void testRegister() {

    }

    @Test
    public void testLogin() {

    }

    @Test
    public void testQuit() {

    }

    @Test
    public void testHelp() {

    }

    @Test
    public void testCreate() {

    }

    @Test
    public void testList() {

    }

    @Test
    public void testJoin() {
    }

    @Test
    public void testObserve() {

    }

    @Test
    public void testLogout() {

    }


}
