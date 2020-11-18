package socialnetwork.reader;

import java.io.IOException;

public class ReaderResponse implements Reader<String> {

    /**
     * Method that reads a response from the System.in
     * @return String, representing the read response
     * @throws IOException
     */
    @Override
    public String read() throws IOException {
        String response;
        while(true) {
            System.out.print("The response = ");
            response = bufferedReader.readLine();
            if (response.matches("[yYnN]"))
                break;
            System.out.println("The response is either y/Y or n/N");
        }
        return response;
    }
}
