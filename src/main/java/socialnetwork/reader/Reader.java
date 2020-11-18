package socialnetwork.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public interface Reader<E> {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Abstract method that reads an Entity from the System.in
     * @return E, representing the read entity
     * @throws IOException
     */
    E read() throws IOException;
}

