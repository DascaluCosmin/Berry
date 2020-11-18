package socialnetwork.reader;

import socialnetwork.domain.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReaderIDSRecipients implements Reader<List<Long>> {

    /**
     * Method that reads the ids of the recipients of the message from the System.in
     * @return List<Long>, representing the list of the ids of the User recipients
     * @throws IOException
     */
    @Override
    public List<Long> read() throws IOException {
        System.out.println("Introduce the ids of the user you want to send data to. Introduce 0 to terminate.");
        List<Long> listIDSRecipients = new ArrayList<>();
        while(true) {
            Long idUserTo = new ReaderID().read();
            if (idUserTo == 0)
                break;
            listIDSRecipients.add(idUserTo);
        }
        return listIDSRecipients;
    }
}
