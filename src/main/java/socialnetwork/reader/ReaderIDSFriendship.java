package socialnetwork.reader;

import socialnetwork.domain.Tuple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReaderIDSFriendship implements Reader<Tuple<Long, Long>> {

    /**
     * Method that reads the IDs of a Friendship from the System.in
     * @return Tuple<Long, Long>, representing a Tuple of the 2 IDs of a Friendship
     * @throws IOException
     */
    @Override
    public Tuple<Long, Long> read() throws IOException {
        Reader readerID = new ReaderID();
        return new Tuple(readerID.read(), readerID.read());
    }
}
