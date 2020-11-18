package socialnetwork.reader;

import java.io.IOException;

public class ReaderResponseFriendshipRequest implements Reader<String> {

    /**
     * Method that reads a response about a friendship request from the System.in
     * @return String, representing the read response
     * @throws IOException
     */
    @Override
    public String read() throws IOException {
        System.out.println("Do you want to accept or decline it (\"exit\" to exit the menu)?");
        String response;
        while(true) {
            System.out.print("Your response = ");
            response = bufferedReader.readLine();
            if (response.equals("accept") || response.equals("decline") || response.equals("exit")) {
                break;
            }
            else {
                System.out.println("Introduce either \"accept\" or \"decline\"!");
            }
        }
        return response;
    }
}
