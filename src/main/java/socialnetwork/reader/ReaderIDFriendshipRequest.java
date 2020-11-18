package socialnetwork.reader;

import java.io.IOException;

public class ReaderIDFriendshipRequest implements Reader<Long> {

    /**
     * Method that reads an ID of friendship request from the System.in
     * @return Long, representing the read ID of the friendship request
     * @throws IOException
     */
    @Override
    public Long read() throws IOException {
        System.out.print("Which friendship request do you want to respond to? ");
        long friendshipRequestID = 0;
        String friendshipRequestIDString;
        while (true) {
            System.out.print("Introduce the ID of the friendship request: ");
            friendshipRequestIDString = bufferedReader.readLine();
            try {
                friendshipRequestID = Long.parseLong(friendshipRequestIDString);
                break;
            } catch (NumberFormatException e) {
                System.err.println("Introduce a valid id - a number!");
            }
        }
        return friendshipRequestID;
    }
}
