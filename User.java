import java.util.ArrayList;
import java.io.*;
import java.util.Iterator;
import java.util.List;

public class User implements InterfaceUser {
    private String username;
    private String password;
    private ArrayList<User> friendList;
    private ArrayList<InterfaceUser> friendsList = new ArrayList<>();
    private ArrayList<User> blockList = new ArrayList<>();
    private List<InterfaceUser> blockedusers = new ArrayList<>();
    private ArrayList<Integer> chatList;
    private String profilePicturePath; // For extra credit=
    private String bio;

    private boolean friendsOnlyRestriction;

    //TODO: determine if it is okay to have this be static
    private static ArrayList<User> users = new ArrayList<>();

    /*
    A constructor meant to initalize a new User.
    */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.friendList = new ArrayList<>();
        this.blockList = new ArrayList<>();
        this.chatList = new ArrayList<>();
        this.profilePicturePath = null; // Default to empty

        users.add(this);
    }

    /*
    A constructor meant to initialize a returning User who already has a friendList and blockList.
    */
    public User(String username, String password, ArrayList<User> friendList, ArrayList<User> blockList,
                ArrayList<Integer> chatList, String profilePicturePath, String bio) {
        this.username = username;
        this.password = password;
        this.friendList = friendList;
        this.blockList = blockList;
        this.chatList = chatList;
        this.profilePicturePath = profilePicturePath;
        this.bio = bio;

        users.add(this);
    }

    public synchronized void addFriend(User user) {
        friendsList.add(user);
    }
    public synchronized void removeFriend(InterfaceUser user){
        friendsList.remove(user);
    }

    public synchronized void addBlock(InterfaceUser user) {
        if(!blockedusers.contains(user)) {
            blockedusers.add(user);
        }
    }


    @Override
    public synchronized void unblockUser(InterfaceUser user) {
        this.blockedusers.remove(user);
    }

    public synchronized void unblockUser(String requesterUsername, String userToUnblockUsername) {
        InterfaceUser requester = this.findUserByUsername(requesterUsername);
        InterfaceUser userToUnblock = this.findUserByUsername(userToUnblockUsername);
        if (requester != null && userToUnblock != null) {
            requester.unblockUser((User)userToUnblock);
            System.out.println(requesterUsername + " has successfully unblocked " + userToUnblockUsername + "!");
        } else {
            System.out.println("One or both users not found.");
        }
    }
    private InterfaceUser findUserByUsername(String username) {
        Iterator var2 = users.iterator();

        InterfaceUser user;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            user = (InterfaceUser)var2.next();
        } while(!user.getUsername().equals(username));

        return user;
    }


    @Override
    public synchronized boolean hasBlocked(InterfaceUser user) {
        return blockedusers.contains(user);
    }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public ArrayList<InterfaceUser> getFriendList() { return friendsList; }
    public ArrayList<User> getBlockList() { return blockList; }
    public ArrayList<Integer> getChatList() { return chatList; }
    public String getProfilePicturePath() { return profilePicturePath; }
    public String getBio() {
        return bio;
    }

    public static synchronized ArrayList<User> getUsers() {
        return users;
    }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setProfilePicturePath(String path) { this.profilePicturePath = path; }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setFriendList(ArrayList<User> friendList) {
        this.friendList = friendList;
    }

    public void setBlockList(ArrayList<User> blockList) {
        this.blockList = blockList;
    }

    public void setChatList(ArrayList<Integer> chatList) {
        this.chatList = chatList;
    }

    /*
    Reads the userDatabase file and creates User objects based on that.
     */
    public static synchronized void parseUserDatabase() {

        try {

            //Defining the file reader
            BufferedReader reader = new BufferedReader(new FileReader(new File("userDatabase.txt")));

            //these variables will be used to initialize the Users from the file
            String tempUsername = null;
            String tempPassword = null;
            ArrayList<User> tempFriendList = new ArrayList<>();
            ArrayList<User> tempBlockList = new ArrayList<>();
            ArrayList<Integer> tempChatList = new ArrayList<>();
            String tempBio = "";
            boolean parsingBio = false; //to make it easier to parse the bio

            /*
            The data is going to be stored in an ArrayList of lines before it is used, because it is going to have
            to be parsed more than once.
            This is to address the challenge of parsing a friendlist or a blocklist from a file when the Users
            mentioned on those lists might not be defined until later in the file.
             */
            ArrayList<String> lines = new ArrayList<>();

            //The line that the reader is currently on
            String readingLine = reader.readLine();

            while (readingLine != null) {

                lines.add(readingLine);

                //Update the reader to the next line
                readingLine = reader.readLine();
            }

            /*
            Now the lines will be searched for just the username and password information and initialize Users
            based off of that
             */
            for (String line : lines) {

                if (line.length() >= 10 && line.substring(0,10).equals("USERNAME: ")) {
                    tempUsername = line.substring(10);
                } else if (line.length() >= 10 && line.substring(0,10).equals("PASSWORD: ")) {
                    tempPassword = line.substring(10);
                } else if (line.substring(0,3).equals("---")) {
                    //The --- is the end marker, so initialize the User.
                    User u = new User(tempUsername, tempPassword);
                }

            }

            /*
            Now go through the lines again, filling in the friend list, block list, and chat list.
             */
            for (String line : lines) {

                if (line.length() >= 10 && line.substring(0,10).equals("USERNAME: ")) {
                    //Going through username again so the program knows which User to update
                    tempUsername = line.substring(10);
                } if (line.length() >= 8 && line.substring(0,8).equals("FRIEND: ")) {
                    tempFriendList.add(userWithName(line.substring(8)));
                } else if (line.length() >= 7 && line.substring(0,7).equals("BLOCK: ")) {
                    tempBlockList.add(userWithName(line.substring(7)));
                } else if (line.length() >= 6 && line.substring(0,6).equals("CHAT: ")) {
                    tempChatList.add(Integer.parseInt(line.substring(6)));
                } else if (line.length() >= 4 && line.substring(0, 4).equals("BIO:")) {

                    //The bio will be on the following lines
                    parsingBio = true;

                } else if (parsingBio && !line.equals("---userseparator---")) {

                    tempBio += line + "\n";

                } else if (line.length() >= 19 && line.substring(0,19).equals("---userseparator---")) {
                    //The ---userseparator--- is the end marker, so update the User.
                    userWithName(tempUsername).setFriendList(tempFriendList);
                    userWithName(tempUsername).setBlockList(tempBlockList);
                    userWithName(tempUsername).setChatList(tempChatList);
                    userWithName(tempUsername).setBio(tempBio);

                    //Reset the list variables for the next User
                    tempFriendList = new ArrayList<>();
                    tempBlockList = new ArrayList<>();
                    tempChatList = new ArrayList<>();
                    tempBio = "";

                    parsingBio = false;
                }

            }

            //Close the reader
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
    Returns the User which has the specified username.
    */
    public static synchronized User userWithName(String name) {
        for (User user : users) {
            if (user.getUsername().equals(name)) {
                return user;
            }
        }
        return null;
    }

    /*
    A method which returns an ArrayList containing every User whose username contains the search term.
     */
    public static synchronized ArrayList<User> userSearch(String search) {

        ArrayList<User> result = new ArrayList<>();

        for (User u : users) {
            if (u.getUsername().contains(search)) {
                result.add(u);
            }
        }

        return result;
    }

    /*
    Updates the user database with the current Users
     */
    public static synchronized void writeToUserDatabase() {

        try {

            //Defining the writer
            PrintWriter writer = new PrintWriter(new FileWriter(new File("userDatabase.txt"),true));

            //Iterates through every existing user
            for (User user : users) {

                writer.write("USERNAME: " + user.getUsername() + "\n");
                writer.write("PASSWORD: " + user.getPassword() + "\n");
                //Iterate through the friend list
                for (InterfaceUser friend : user.getFriendList()) {
                    writer.write("FRIEND: " + friend.getUsername() + "\n");
                }
                //Iterate through the block list
                for (User block : user.getBlockList()) {
                    writer.write("BLOCK: " + block.getUsername() + "\n");
                }
                //Iterate through the chat list
                for (int chat : user.getChatList()) {
                    writer.write("CHAT: " + chat + "\n");
                }

                if (user.getBio() != null) {
                    writer.write("BIO:\n" + user.getBio());
                } else {
                    writer.write("BIO:\n");
                }


                writer.write("---userseparator---\n");


            }

            //Closing the writer
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
    Gives String in form:
    Username: [username]
    Password: [password]
    Friend list:
    - friend
    - friend
    - friend
    Block list:
    - block
    - block
    - block
    Chat list:
    - chat
    - chat
    - chat
    Bio:
    bio
     */
    public String toString() {
        String result = "";

        result += "Username: " + username + "\n";
        result += "Password: " + password + "\n";
        result += "Friend list:\n";
        for (User u : friendList) {
            result += "- " + u.getUsername() + "\n";
        }
        result += "Block list:\n";
        for (User u : blockList) {
            result += "- " + u.getUsername() + "\n";
        }
        result += "Chat list:\n";
        for (int i : chatList) {
            result += "- " + i + "\n";
        }
        result += "Bio:\n" + bio + "\n";

        return result;
    }

    public boolean getFriendsOnlyRestriction() {
        return friendsOnlyRestriction;
    }

    public void setFriendsOnlyRestriction(boolean friendsOnlyRestriction) {
        this.friendsOnlyRestriction = friendsOnlyRestriction;
    }

    //hello
}
