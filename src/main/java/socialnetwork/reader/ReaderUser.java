package socialnetwork.reader;

import socialnetwork.domain.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReaderUser implements Reader<User> {

    /**
     * Method that reads an User from the System.in
     * @return User, representing the read User
     * @throws IOException
     */
    @Override
    public User read() throws IOException {
        Long idUser = new ReaderID().read();
        String firstNameUser;
        System.out.print("Introduce the first name of the user: ");
        firstNameUser = bufferedReader.readLine();
        String lastNameUser;
        System.out.print("Introduce the last name of the user: ");
        lastNameUser = bufferedReader.readLine();
        User user = new User(firstNameUser, lastNameUser);
        user.setId(idUser);
        return user;
    }
}
