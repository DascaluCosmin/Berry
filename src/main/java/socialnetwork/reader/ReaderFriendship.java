package socialnetwork.reader;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReaderFriendship implements Reader<Friendship> {

    /**
     * Method that reads a Friendship from the System.in
     * @return Friendship, representing the read Friendship
     * @throws IOException
     */
    @Override
    public Friendship read() throws IOException {
        Reader readerID = new ReaderID();
        Friendship friendship = new Friendship(new Tuple(readerID.read(), readerID.read()));
        return friendship;
    }
}
