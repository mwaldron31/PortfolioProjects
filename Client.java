import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
/**
 * Client.java
 *
 * Runs the Client side of the platform (thread compatible)
 *
 * @author lab 7, team 1
 *
 * @version April 7, 2024
 */

public class Client implements ClientInterface{

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;

    public Client(String address, int port) throws Exception {
        this.socket = new Socket(address, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.scanner = new Scanner(System.in);
    }

    public synchronized void start() {
        Thread readThread = new Thread(() -> {
            try {
                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    System.out.println("Server: " + serverResponse);
                }
            } catch (Exception e) {
                System.out.println("Connection closed.");
            }
        });
        readThread.start();
        SwingUtilities.invokeLater(new WelcomeScreenGUI());

        try {
            System.out.println("Enter command:");
            System.out.println("Available commands:");
            System.out.println("  createaccount <username> <password>");
            System.out.println("  login <username> <password>");
            System.out.println("  blockUser <requesterUsername> <userToBlockUsername>");
            System.out.println("  unblockUser <requesterUsername> <userToUnblockUsername>");
            System.out.println("  sendMessage <senderUsername> <receiverUsername> <message>");
            System.out.println(" addfriend <yourUsername> <friendUsername>");
            System.out.println(" removefriend <yourUsername> <friendUsername>");
            System.out.println(" setmessagepref <yourUsername> <Preference (<allUsers> <friends>)>");
            System.out.println("  exit");
            while (true) {
                String userInput = scanner.nextLine();
                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }
                out.println(userInput);
            }
        } finally {
            close();
        }
    }

    public synchronized void close() {
        try {
            scanner.close();
            out.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Failed to close resources: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 1234);
        client.start();
    }
}
