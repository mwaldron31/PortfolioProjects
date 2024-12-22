import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomeScreenGUI extends JComponent implements Runnable {
    private static ChatService chatService = new ChatService();
    private JFrame frame;

    private JButton createButton, loginButton;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel usernameLabel, passwordLabel, statusLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new WelcomeScreenGUI());
    }

    public void run() {
        frame = new JFrame("Welcome Screen");
        frame.setLayout(null);

        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Username label and field
        usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 30, 80, 30);
        frame.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(140, 30, 200, 30);
        frame.add(usernameField);

        // Password label and field
        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 70, 80, 30);
        frame.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(140, 70, 200, 30);
        frame.add(passwordField);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setBounds(140, 110, 200, 30);
        loginButton.addActionListener(this::performLogin);
        frame.add(loginButton);

        // Create account button
        createButton = new JButton("Create Account");
        createButton.setBounds(140, 150, 200, 30);
        createButton.addActionListener(this::openCreateAccountDialog);
        frame.add(createButton);

        // Status label
        statusLabel = new JLabel("");
        statusLabel.setBounds(50, 190, 300, 30);
        frame.add(statusLabel);

        frame.setVisible(true);
    }

    private void performLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        if (chatService.authenticateUser(username, password)) {
            frame.dispose(); // Close the login window
            new ChatScreenGUI(username, chatService).setVisible(true); // Open the chat screen
        } else {
            statusLabel.setText("Login failed. Please check your username and password.");
        }
    }

    private void openCreateAccountDialog(ActionEvent e) {
        JDialog dialog = new JDialog(frame, "Create Account", true);
        dialog.setLayout(new FlowLayout());
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(frame);

        JTextField newUsernameField = new JTextField(20);
        JPasswordField newPasswordField = new JPasswordField(20);
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(ev -> {
            String username = newUsernameField.getText();
            String password = new String(newPasswordField.getPassword());
            createAccount(username, password);
            dialog.dispose(); // Close the dialog after account creation
        });

        dialog.add(new JLabel("Username:"));
        dialog.add(newUsernameField);
        dialog.add(new JLabel("Password:"));
        dialog.add(newPasswordField);
        dialog.add(submitButton);

        dialog.setVisible(true);
    }

    private void createAccount(String username, String password) {
        if (username != null && !username.isEmpty() && password.length() >= 8) {
            InterfaceUser newUser = chatService.addUser(username, password);
            if (newUser != null) {
                statusLabel.setText("User added successfully. Username: " + username);
            } else {
                statusLabel.setText("Failed to add user. Username may already exist.");
            }
        } else {
            if (username == null || username.isEmpty()) {
                statusLabel.setText("Missing username.");
            } else {
                statusLabel.setText("Password must be at least 8 characters.");
            }
        }
    }
}
