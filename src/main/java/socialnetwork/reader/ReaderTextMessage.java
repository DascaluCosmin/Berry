package socialnetwork.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReaderTextMessage implements Reader<String> {

    /**
     * Method that reads a text message from the System.in
     * @return String, representing the read text message
     * @throws IOException
     */
    @Override
    public String read() throws IOException {
        System.out.print("Introduce the text of the message: ");
        return bufferedReader.readLine();
    }
}
