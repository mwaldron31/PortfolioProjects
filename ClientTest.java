import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.*;

public class ClientTest {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private final String newline = System.lineSeparator();

    @BeforeEach
    public void setUp() throws IOException {
        System.setOut(new PrintStream(outContent));
        serverSocket = new ServerSocket(0);
        new Thread(() -> {
            try {
                clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String input;
                while ((input = reader.readLine()) != null) {
                    writer.println("Echo: " + input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Test
    public void testClientOutputAndInput() throws Exception {
        ByteArrayInputStream inContent = new ByteArrayInputStream(("exit" + newline).getBytes());
        System.setIn(inContent);

        Client client = new Client("localhost", serverSocket.getLocalPort());
        Thread clientThread = new Thread(client::start);
        clientThread.start();


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertTrue(outContent.toString().contains("Enter command:"));
        assertFalse(outContent.toString().contains("Echo: exit"));

        client.close();
        clientThread.join();
    }

    @AfterEach
    public void restoreStreams() throws IOException {
        System.setOut(originalOut);
        System.setIn(originalIn);
        serverSocket.close();
        if (clientSocket != null && !clientSocket.isClosed()) {
            clientSocket.close();
        }
    }
}
