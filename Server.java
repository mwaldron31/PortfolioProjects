import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Server.java
 *
 * This program handles the server side and processing for our platform.
 *
 * @author lab 7, team 1
 *
 * @version April 7, 2024
 */

public class Server implements ServerInterface{
    private int port;
    private ExecutorService pool;

    public Server(int port) {
        this.port = port;
        this.pool = Executors.newCachedThreadPool();
    }

    public synchronized void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    pool.execute(new ClientHandler(clientSocket));
                }
            } finally {
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(1234);
        server.run();
    }
}