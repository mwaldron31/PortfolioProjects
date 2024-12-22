import java.util.List;
/**
 *InterfaceDataStore.java
 *
 *The interface for DataStore.java.
 *
 *@author lab section 007, team 1
 *
 *@version April 1, 2024
 *
 */
public interface InterfaceDataStore {
    void saveToFile(String filename, String data);
    List<String> readFromFile(String filename);
}
