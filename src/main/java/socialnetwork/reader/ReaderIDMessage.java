package socialnetwork.reader;

import java.io.IOException;

public class ReaderIDMessage implements Reader<Long> {
    @Override
    public Long read() throws IOException {
        System.out.print("Which message do you want to respond to? ");
        long messageID = 0;
        String messageIDString;
        while (true) {
            System.out.print("Introduce the ID of the message: ");
            messageIDString = bufferedReader.readLine();
            try {
                messageID = Long.parseLong(messageIDString);
                break;
            } catch (NumberFormatException e) {
                System.err.println("Introduce a valid id - a number!");
            }
        }
        return messageID;
    }
}
