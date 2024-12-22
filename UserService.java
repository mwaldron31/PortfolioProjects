import java.util.*;
import java.io.*;
/**
 *UserService.java
 *
 *Handles all user inputs like registering a user, login, adding friends, blocking and unblocking people,and
 * setting/getting message preferences.
 *
 *@author lab section 007, team 1
 *
 *@version April 15, 2024
 *
 */

public class UserService implements InterfaceUserService{
    private final String usersFile = "userDatabase.txt";
    private final String friendsFile = "friends.txt";
    private final String blockListFile = "blocklist.txt";

    public synchronized boolean registerUser(String username, String password) {
        // Define the file path
        String usersFile = "userDatabase.txt";

        // Check if the user already exists
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] userDetails = currentLine.split(":");
                if (userDetails[0].equals(username)) {
                    // User already exists
                    return false;
                }
            }
        } catch (FileNotFoundException e) {
            // File not found, will be created when writing the new user
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Append the new user to the file
        try (FileWriter fw = new FileWriter(usersFile, true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(username + ":" + password);
            bw.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized boolean loginUser(String username, String password) {
        String usersFile = "userDatabase.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // Splitting the line into username and password
                String[] userDetails = currentLine.split(":");
                if (userDetails.length < 2) continue; // Skip if the format is incorrect

                String fileUsername = userDetails[0];
                String filePassword = userDetails[1];

                if (fileUsername.equals(username) && filePassword.equals(password)) {
                    // Successful login
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            // File not found implies no users are registered, hence login should fail
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // If we reach this point, it means no matching user was found
        return false;
    }

    public synchronized List<String> searchUsers(String searchTerm) {
        List<String> matchingUsers = new ArrayList<>();
        String usersFile = "userDatabase.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // Assuming the format of each line is "username:password"
                String[] userDetails = currentLine.split(":");
                if (userDetails.length < 2) continue; // Skip if the format is incorrect

                String username = userDetails[0];

                // Check if the username contains the search term
                if (username.toLowerCase().contains(searchTerm.toLowerCase())) {
                    matchingUsers.add(username);
                }
            }
        } catch (FileNotFoundException e) {
            // File not found implies no users are registered
        } catch (IOException e) {
            e.printStackTrace();
            // Depending on your error handling policy, you might want to return an empty list
            // or rethrow the exception as a runtime exception
        }

        return matchingUsers;
    }

    public synchronized boolean addFriend(String username, String friendUsername) {
        String friendsFile = "friends.txt";

        // Load current friendships to check if the friendship already exists
        List<String> currentFriendships = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(friendsFile))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.contains(username + ":" + friendUsername) || currentLine.contains(friendUsername + ":" + username)) {
                    // The friendship already exists
                    return false;
                }
                currentFriendships.add(currentLine);
            }
        } catch (FileNotFoundException e) {
            // If the file doesn't exist, it's okay - we'll create it when adding the new friendship
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Add the new friendship
        currentFriendships.add(username + ":" + friendUsername);

        // Save the updated friendships back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(friendsFile))) {
            for (String friendship : currentFriendships) {
                writer.write(friendship);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public synchronized boolean removeFriend(String username, String friendUsername) {
        boolean isRemoved = false;
        List<String> updatedFriendsList = new ArrayList<>();

        // Read the current friends list and filter out the friendship to be removed
        try (BufferedReader reader = new BufferedReader(new FileReader(friendsFile))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // Keep all friendships except the ones to be removed
                if (!currentLine.equals(username + ":" + friendUsername) && !currentLine.equals(friendUsername + ":" + username)) {
                    updatedFriendsList.add(currentLine);
                } else {
                    isRemoved = true; // Mark as removed if found
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Friends file not found.");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Only update the file if the friendship entry was found and removed
        if (isRemoved) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(friendsFile, false))) {
                for (String friendship : updatedFriendsList) {
                    writer.write(friendship);
                    writer.newLine();
                }
                return true; // Successfully removed the friendship and updated the file
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // The friendship entry was not found, no need to update the file
            return false;
        }
    }

    public synchronized boolean blockUser(String username, String blockUsername) {
        String blockListFile = "blockList.txt";

        // Load current block list to check if the block already exists
        List<String> currentBlocks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(blockListFile))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.equals(username + ":" + blockUsername)) {
                    // The block already exists
                    return false;
                }
                currentBlocks.add(currentLine);
            }
        } catch (FileNotFoundException e) {
            // If the file doesn't exist, it's okay - we'll create it when adding the new block
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Add the new block to the list
        currentBlocks.add(username + ":" + blockUsername);

        // Save the updated block list back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(blockListFile))) {
            for (String block : currentBlocks) {
                writer.write(block);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public synchronized boolean unblockUser(String username, String unblockUsername) {
        boolean isRemoved = false;
        List<String> updatedBlockList = new ArrayList<>();

        // Read the current block list and filter out the unblock entry
        try (BufferedReader reader = new BufferedReader(new FileReader(blockListFile))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // Keep all blocks except the one to be removed
                if (!currentLine.equals(username + ":" + unblockUsername)) {
                    updatedBlockList.add(currentLine);
                } else {
                    isRemoved = true; // Mark as removed if found
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Block list file not found.");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Only update the file if the block entry was found and removed
        if (isRemoved) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(blockListFile, false))) {
                for (String blockEntry : updatedBlockList) {
                    writer.write(blockEntry);
                    writer.newLine();
                }
                return true; // Successfully removed the block and updated the file
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // The block entry was not found, no need to update the file
            return false;
        }
    }
    public synchronized boolean setMessagePreference(String username, String preference) {
        List<String> updatedUsers = new ArrayList<>();
        boolean found = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username)) {
                    line = parts[0] + ":" + parts[1] + ":" + preference;
                    found = true;
                }
                updatedUsers.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (!found) return false; // User not found

        // Write the updated users back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile, false))) {
            for (String user : updatedUsers) {
                writer.write(user);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public synchronized boolean getMessagePreference(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username)) {
                    return "allUsers".equals(parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true; // Default to allow messages from all users if not specified
    }
}

