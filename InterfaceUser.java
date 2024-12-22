import java.util.ArrayList;
/**
*InterfaceUser.java
*
*The interface for User.java.
*
*@author lab section 007, team 1
*
*@version April 1, 2024
*
*/
public interface InterfaceUser {
    String getUsername();
    void addBlock(InterfaceUser user);
    void unblockUser(InterfaceUser user);
    //void unblockUser(String requesterUsername, String userToUnblockUsername);
    boolean hasBlocked(InterfaceUser user);

    ArrayList<InterfaceUser> getFriendList();
}
