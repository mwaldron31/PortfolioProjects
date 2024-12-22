import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ServerTest.java
 *
 * This program tests the Server.java
 *
 * @author lab 7, team 1
 *
 * @version April 7, 2024
 */

public class ServerTest {
    private Server server;
    private Thread serverThread;
    private final int testPort = 1234;

    @BeforeEach
    void setup() throws IOException {
        server = new Server(testPort);
        serverThread = new Thread(() -> {
            server.run();
        });
    }

    @AfterEach
    void tearDown() {
        serverThread.interrupt();
    }

    @Test
    void testServerStartsAndListensOnPort() {
        assertDoesNotThrow(() -> {
            serverThread.start();
            Thread.sleep(500);
        });
        try (Socket client = new Socket("localhost", testPort)) {
            assertTrue(client.isConnected(), "Server should accept connections on port " + testPort);
        } catch (IOException e) {
            fail("Should be able to connect to the server on port " + testPort);
        }
    }

    @Test
    void testServerFailsWhenPortInUse() throws IOException, InterruptedException {
        ServerSocket blockingSocket = new ServerSocket(testPort);
        try {
            serverThread.start();
            Thread.sleep(100);

            serverThread.join(1000);

            assertFalse(serverThread.isAlive(), "Server thread should have terminated due to port being in use");
        } finally {
            blockingSocket.close();
        }
    }

}
