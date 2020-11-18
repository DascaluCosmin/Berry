package socialnetwork.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReaderMonth implements Reader<String> {

    /**
     * Method that reads a month from the System.in
     * @return String, representing the read month in String format
     * @throws IOException
     */
    @Override
    public String read() throws IOException {
        String month = "";
        while(true) {
            System.out.print("Introduce the month: ");
            month = bufferedReader.readLine();
            int monthToInt = Integer.parseInt(month);
            if (monthToInt >= 1 && monthToInt <= 12)
                break;
        }
        return month;
    }
}
