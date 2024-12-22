/**
* InterfaceChatService.java
* 
* The interface for ChatService.java. 
* 
*@author lab section 007, team 1
* 
*@version April 1, 2024
*
*/
public interface InterfaceChatService {
    void addUser(InterfaceUser user);
    String blockUser(String requesterUsername, String userToBlockUsername);
    void loginUser(InterfaceUser user);
    String sendMessage(String senderUsername, String receiverUsername, String message);
}
