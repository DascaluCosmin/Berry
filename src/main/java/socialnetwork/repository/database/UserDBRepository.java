package socialnetwork.repository.database;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.ContentPage;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDBRepository implements Repository<Long, User> {
    private String url;
    private String username;
    private String password;

    /**
     * Constructor that creates a new UserDBRepository
     * @param url String, representing the URL of the Data Base
     * @param username String, representing the Username of the user connecting to the DB
     * @param password Password, representing the Password of the user connecting to the DB
     */
    public UserDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Overridden method that gets one specific User
     * @param aLong Long, representing the ID of the User
     * @return null, if the User doesn't exist
     *      non-null User, otherwise
     */
    @Override
    public User findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command =
                    "SELECT * " +
                    "FROM users " +
                    "WHERE id = " + aLong;
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
               return getUser(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that gets the list of all Users
     * @return Iterable<User>, representing the list of all Users
     */
    @Override
    public Iterable<User> findAll() {
        List<User> userList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userList.add(getUser(resultSet));
            }
            return userList;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userList;
    }

    /**
     * Method that gets the list of all Users on a specific Page
     * @param currentPage ContentPage, representing the Page containing the Users
     * @return Iterable<User>, representing the list of Users on that Page
     */
    public Iterable<User> findAll(ContentPage currentPage) {
        List<User> userList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM users LIMIT " + currentPage.getSizePage() +
                    " OFFSET " + (currentPage.getNumberPage() - 1) * currentPage.getSizePage();
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                userList.add(getUser(resultSet));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userList;
    }

    /**
     * Method that gets the list of all Users that are not befriended with a User, on a specific Page
     * @param currentPage ContentPage, representing the Page containing the Users
     * @return Iterable<User>, representing the list of Users that are not befriended, on that Page
     */
    public Iterable<User> findAll(Long idUser, ContentPage currentPage) {
        List<User> userList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String commandSubQuery = "SELECT \"idRight\" FROM \"friendships\" WHERE \"idLeft\" = " + idUser + " " +
                    "UNION SELECT \"idUserTo\" FROM \"friendshipRequests\" WHERE \"idUserFrom\" = " + idUser + " AND status = 'pending' " +
                    "UNION SELECT \"idUserFrom\" FROM \"friendshipRequests\" WHERE \"idUserTo\" = " + idUser + " AND status = 'pending'";
            String command = "SELECT * FROM users WHERE id NOT IN (" + commandSubQuery + ") AND id != " + idUser + " " +
                    "LIMIT " + currentPage.getSizePage() + " OFFSET " + (currentPage.getNumberPage() - 1) * currentPage.getSizePage();
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                userList.add(getUser(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userList;
    }

    /**
     * Method that adds a new User to the Data Base
     * @param entity User, representing the User to be added
     *         entity must be not null
     * @return null, if the User was added successfully
     *      non-null User, otherwise
     */
    @Override
    public User save(User entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "";
            if (entity.getId() == null) {
                command = "INSERT INTO users (\"firstName\", \"lastName\") VALUES " +
                          "('" + entity.getFirstName() + "', '" + entity.getLastName() + "') " +
                          "RETURNING *";
            } else {
                command = "INSERT INTO users (id, \"firstName\", \"lastName\") VALUES " +
                          "(" + entity.getId() + ", '" + entity.getFirstName() + "', '" + entity.getLastName() + "') " +
                          "RETURNING *";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                   return getUser(resultSet);
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
     * Method that deletes a User from the Data Base
     * @param aLong Long, representing the ID of theUser to be deleted
     * @return null, if the User doesn't exist
     *      non-null User, if the User was deleted successfully
     */
    @Override
    public User delete(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "DELETE FROM users WHERE id = " + aLong + " RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getUser(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that updates a User in the Data Base
     * @param entity User, representing the new User
     *          entity must not be null
     * @return null, if the User was updated successfully
     *      non-null User, otherwise
     */
    @Override
    public User update(User entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "UPDATE users SET " +
                             "\"firstName\" = '" + entity.getFirstName() + "', " +
                             "\"lastName\" = '" + entity.getLastName() + "' " +
                             "WHERE id = " + entity.getId() + " RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return null;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return entity;
    }

    /**
     * Method that gets an User from the current position of the Result Set
     * @param resultSet ResultSet, representing the Result Set
     * @return User, representing the User built from the current position of the Result Set
     * @throws SQLException if a field from the Data Base doesn't exist
     */
    private User getUser(ResultSet resultSet) throws SQLException {
        Long userID = resultSet.getLong("id");
        String firstName = resultSet.getString("firstName");
        String lastName = resultSet.getString("lastName");
        User user = new User(firstName, lastName);
        user.setId(userID);
        return user;
    }
}
