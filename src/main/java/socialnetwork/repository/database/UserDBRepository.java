package socialnetwork.repository.database;

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
        return null;
    }

    @Override
    public Iterable<User> findAll() {
        List<User> userList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM domain.users");
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
        return null;
    }

    @Override
    public User delete(Long aLong) {
        return null;
    }

    @Override
    public User update(User entity) {
        return null;
    }
}
