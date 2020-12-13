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

    public UserCredentialsDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

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

    private UserCredentials getUserCredentials(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("idUser");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        UserCredentials userCredentials = new UserCredentials(username, password);
        userCredentials.setId(id);
        return userCredentials;
    }
}
