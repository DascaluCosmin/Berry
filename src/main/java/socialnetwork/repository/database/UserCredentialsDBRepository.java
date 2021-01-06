package socialnetwork.repository.database;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.Entity;
import socialnetwork.domain.User;
import socialnetwork.domain.UserCredentials;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserCredentialsDBRepository implements Repository<Long, UserCredentials> {
    private String url;
    private String username;
    private String password;

    /**
     * Constructor that creates a new UserCredentialsDBRepository
     * @param url String, representing the URL of the Data Base
     * @param username String, representing the Username of the user connecting to the DB
     * @param password Password, representing the Password of the user connecting to the DB
     */
    public UserCredentialsDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Method that gets one specific User Credentials
     * @param username String, representing the Username of the User
     * @return null, if the User with that Username doesn't exist
     *      non-null UserCredentials, otherwise
     */
    public UserCredentials findOne(String username) {
        try (Connection connection = DriverManager.getConnection(url, this.username, password)) {
            String command = "SELECT * FROM \"userCredentials\" WHERE \"username\" = '" + username + "';";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getUserCredentials(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that the UserCredentials for a specific User
     * @param aLong, representing the ID of the User
     * @return null, if the User doesn't exist
     *      non-null UserCredentials, otherwise
     */
    @Override
    public UserCredentials findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, this.username, password)) {
            String command = "SELECT * FROM \"userCredentials\" WHERE \"idUser\" = " + aLong;
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getUserCredentials(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that gets the list of all UserCredentials
     * @return Iterable<UserCredentials>, representing the list of all UserCredentials
     */
    @Override
    public Iterable<UserCredentials> findAll() {
        List<UserCredentials> userCredentialsList = new ArrayList();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM \"userCredentials\"");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userCredentialsList.add(getUserCredentials(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userCredentialsList;
    }

    /**
     * Method that adds a new UserCredentials to the Data Base
     * @param entity UserCredentials, representing the entity to be added
     *         entity must be not null
     * @return null, if the UserCredentials was added successfully
     *      non-null UserCredentials, otherwise
     */
    @Override
    public UserCredentials save(UserCredentials entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)){
            String command = "INSERT INTO \"userCredentials\" (\"idUser\", username, password) VALUES " +
                    "(" + entity.getId() + ", '" + entity.getUsername() + "', '" + entity.getPassword() + "') " +
                    "RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return null;
                }
            } catch (PSQLException e) {
                return entity;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return entity;
    }

    /**
     * Method that deletes a UserCredentials from the Data Base
     * @param aLong Long, representing the ID of the User whose Credentials are to be deleted
     * @return null, if the User doesn't exist
     *      non-null UserCredentials, if the UserCredentials was deleted successfully
     */
    @Override
    public UserCredentials delete(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "DELETE FROM \"userCredentials\" WHERE \"idUser\" = " + aLong + " " +
                    "RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getUserCredentials(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that updates a UserCredentials in the Data Base
     * @param entity UserCredentials, representing the new UserCredentials
     *          entity must not be null
     * @return null, if the UserCredentials was updated successfully
     *      non-null UserCredentials, otherwise
     */
    @Override
    public UserCredentials update(UserCredentials entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "UPDATE \"userCredentials\" SET " +
                    "username = '" +  entity.getUsername() +"', " +
                    "password = '" + entity.getPassword() + "' WHERE \"idUser\" = " + entity.getId() + " " +
                    "RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return null;
                }
            } catch (PSQLException e) {
                return entity;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return entity;
    }

    /**
     * Method that gets a UserCredentials from the current position of the Result Set
     * @param resultSet ResultSet, representing the Result Set
     * @return UserCredentials, representing the UserCredentials built from the current position of the Result Set
     * @throws SQLException, if a field from the Data Base doesn't exist
     */
    private UserCredentials getUserCredentials(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("idUser");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        UserCredentials userCredentials = new UserCredentials(username, password);
        userCredentials.setId(id);
        return userCredentials;
    }
}
