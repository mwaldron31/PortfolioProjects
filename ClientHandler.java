import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
/**
 *ClientHandler.java
 *
 *Handles client requests that are sent to the server
 *
 *@author lab section 007, team 1
 *
 *@version April 15, 2024
 *
 */

public class ClientHandler implements ClientHandlerInterface {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private UserService userService;
    private MessageService messageService;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.userService = new UserService();
        this.messageService = new MessageService();
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Error setting up client streams: " + e.getMessage());
        }
    }

    @Override
    public synchronized void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String response = processCommand(inputLine);
                out.println(response);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private synchronized String processCommand(String command) {
        String[] parts = command.split(" ");
        if (parts.length == 0) return "Invalid command format.";

        switch (parts[0].toLowerCase()) {
            case "createaccount":
                if (parts.length < 2) return "Invalid createaccount command.";
                return userService.registerUser(parts[1], parts[2]) ? "Account created successfully." : "Failed to create account.";
            case "login":
                if (parts.length < 2) return "Invalid login command.";
                return userService.loginUser(parts[1], parts[2]) ? "Login successful." : "Login failed.";
            case "blockuser":
                if (parts.length < 3) return "Invalid blockuser command.";
                return userService.blockUser(parts[1], parts[2]) ? "User blocked successfully." : "Failed to block user.";
            case "unblockuser":
                if (parts.length < 3) return "Invalid unblockuser command.";
                return userService.unblockUser(parts[1], parts[2]) ? "User unblocked successfully." : "Failed to unblock user.";
            case "addfriend":
                if (parts.length < 3) return "Invalid addfriend command.";
                return userService.addFriend(parts[1], parts[2]) ? "Friend added successfully." : "Failed to add friend.";
            case "removefriend":
                if (parts.length < 3) return "Invalid removefriend command.";
                return userService.removeFriend(parts[1], parts[2]) ? "Friend removed successfully." : "Failed to remove friend.";
            case "sendmessage":
                if (parts.length < 4) return "Invalid sendmessage command.";
                return messageService.sendMessage(parts[1], parts[2], parts[3]) ? "Message sent successfully." : "Failed to send message.";
            case "setmessagepref":
                if (parts.length < 3) return "Invalid setmessagepref command format.";
                return userService.setMessagePreference(parts[1], parts[2]) ? "Message preference updated successfully." : "Failed to update message preference.";
            default:
                return "Unknown command.";
        }
    }


    public void closeResources() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Error closing client handler resources: " + e.getMessage());
        }
    }
}
