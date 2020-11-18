package socialnetwork.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReaderID implements Reader<Long> {

    /**
     * Method that reads an ID from the System.in
     * @return Long, representing the read ID
     * @throws IOException
     */
    @Override
    public Long read() throws IOException {
        long userIDToDelete = 0;
        String userIDString;
        while (true) {
            System.out.print("Introduce the ID of the user: ");
            userIDString = bufferedReader.readLine();
            try {
                userIDToDelete = Long.parseLong(userIDString);
                break;
            } catch (NumberFormatException e) {
                System.err.println("Introduce a valid id - a number!");
            }
        }
        return userIDToDelete;
    }
}
