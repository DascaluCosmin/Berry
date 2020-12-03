package socialnetwork.repository.database;

import org.postgresql.util.PSQLException;
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
    private Validator<User> validator;

    public UserDBRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

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
                Long idUser = resultSet.getLong("id");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                User user = new User(firstName, lastName);
                user.setId(idUser);
                return user;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<User> findAll() {
        List<User> userList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long idUser = resultSet.getLong("id");
                String firstNameUser = resultSet.getString("firstName");
                String lastNameUser = resultSet.getString("lastName");
                User user = new User(firstNameUser, lastNameUser);
                user.setId(idUser);
                userList.add(user);
            }
            return userList;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userList;
    }

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
    public User delete(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "DELETE FROM users WHERE id = " + aLong + " RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                User user = new User(firstName, lastName);
                user.setId(aLong);
                return user;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

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
            return entity;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return entity;
    }
}
