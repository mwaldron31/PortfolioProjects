import java.io.*;
import java.util.ArrayList;
import java.util.List;
/**
 *DataStore.java
 *
 *Writes information to various files for storage of data that is inputted by the user
 *
 *@author lab section 007, team 1
 *
 *@version April 15, 2024
 *
 */

public class DataStore implements InterfaceDataStore{

    public void saveToFile(String filename, String data) {
        try (FileWriter fw = new FileWriter(filename, true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(data);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> readFromFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
