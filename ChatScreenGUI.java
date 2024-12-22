import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ChatScreenGUI extends JFrame {
    private JButton logoutButton, sendMessageButton, blockUserButton, unblockUserButton, searchUserButton, listUsersButton;
    private JTextField messageField, targetUserField;
    private JLabel messageLabel, targetUserLabel, statusLabel;
    private ChatService chatService;
    private String username;

    public ChatScreenGUI(String username, ChatService chatService) {
        this.username = username;
        this.chatService = chatService;
        initializeComponents();
        setupGUI();
    }

    private void setupGUI() {
        setTitle("Chat Screen - Logged in as: " + username);
        setSize(600, 500); // Increased height to accommodate new button
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void initializeComponents() {
        int labelWidth = 80;
        int fieldWidth = 200;

        // Message label and field
        messageLabel = new JLabel("Message:");
        messageLabel.setBounds(20, 50, labelWidth, 30);
        add(messageLabel);

        messageField = new JTextField();
        messageField.setBounds(110, 50, fieldWidth, 30);
        add(messageField);

        // Target user label and field
        targetUserLabel = new JLabel("To User:");
        targetUserLabel.setBounds(320, 50, labelWidth, 30);
        add(targetUserLabel);

        targetUserField = new JTextField();
        targetUserField.setBounds(400, 50, fieldWidth, 30);
        add(targetUserField);

        // Buttons for actions
        sendMessageButton = new JButton("Send Message");
        sendMessageButton.setBounds(110, 90, fieldWidth, 30);
        sendMessageButton.addActionListener(this::sendMessage);
        add(sendMessageButton);

        blockUserButton = new JButton("Block User");
        blockUserButton.setBounds(110, 130, fieldWidth, 30);
        blockUserButton.addActionListener(this::blockUser);
        add(blockUserButton);

        unblockUserButton = new JButton("Unblock User");
        unblockUserButton.setBounds(400, 130, fieldWidth, 30);
        unblockUserButton.addActionListener(this::unblockUser);
        add(unblockUserButton);

        searchUserButton = new JButton("Search User");
        searchUserButton.setBounds(110, 170, fieldWidth, 30);
        searchUserButton.addActionListener(this::searchUser);
        add(searchUserButton);

        listUsersButton = new JButton("List All Users");
        listUsersButton.setBounds(400, 170, fieldWidth, 30);
        listUsersButton.addActionListener(this::listAllUsers);
        add(listUsersButton);

        logoutButton = new JButton("Logout");
        logoutButton.setBounds(250, 210, fieldWidth, 30);
        logoutButton.addActionListener(this::logout);
        add(logoutButton);

        // Status label
        statusLabel = new JLabel("");
        statusLabel.setBounds(20, 250, 560, 30);
        add(statusLabel);
    }

    private void sendMessage(ActionEvent e) {
        String receiver = targetUserField.getText();
        String message = messageField.getText();
        String response = chatService.sendMessage(username, receiver, message);
        statusLabel.setText(response);
    }

    private void blockUser(ActionEvent e) {
        String userToBlock = targetUserField.getText();
        String response = chatService.blockUser(username, userToBlock);
        statusLabel.setText(response);
    }

    private void unblockUser(ActionEvent e) {
        String userToUnblock = targetUserField.getText();
        String response = chatService.unblockUser(username, userToUnblock);
        statusLabel.setText(response);
    }

    private void searchUser(ActionEvent e) {
        List<InterfaceUser> usersFound = chatService.searchUser(targetUserField.getText());
        if (usersFound.isEmpty()) {
            statusLabel.setText("No users found.");
        } else {
            StringBuilder sb = new StringBuilder("Users found:");
            for (InterfaceUser user : usersFound) {
                sb.append(" ").append(user.getUsername());
            }
            statusLabel.setText(sb.toString());
        }
    }

    private void listAllUsers(ActionEvent e) {
        List<String> usernames = chatService.getAllUsernames();
        if (usernames.isEmpty()) {
            statusLabel.setText("No users found.");
        } else {
            StringBuilder sb = new StringBuilder("All users:");
            for (String username : usernames) {
                sb.append(" ").append(username);
            }
            statusLabel.setText(sb.toString());
        }
    }

    private void logout(ActionEvent e) {
        username = null;
        statusLabel.setText("Logged out successfully.");
        dispose();
        new WelcomeScreenGUI().run();
    }
}
