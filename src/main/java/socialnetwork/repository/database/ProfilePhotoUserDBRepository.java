package socialnetwork.repository.database;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.ProfilePhotoUser;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfilePhotoUserDBRepository implements Repository<Long, ProfilePhotoUser> {
    private String url;
    private String username;
    private String password;

    public ProfilePhotoUserDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public ProfilePhotoUser findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)){
            String command = "SELECT * FROM \"profilePhotoUser\" WHERE id = " + aLong;
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getProfilePhotoUser(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<ProfilePhotoUser> findAll() {
        List<ProfilePhotoUser> profilePhotoUserList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM \"profilePhotoUser\"");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                profilePhotoUserList.add(getProfilePhotoUser(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return profilePhotoUserList;
    }

    @Override
    public ProfilePhotoUser save(ProfilePhotoUser entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "INSERT INTO \"profilePhotoUser\" (id, \"profilePhotoPath\") VALUES " +
                    "(" + entity.getId() + ", '" + entity.getPathProfilePhoto() + "')" +
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
    public ProfilePhotoUser delete(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)){
            String command = "DELETE FROM \"profilePhotoUser\" WHERE id = " + aLong + " " +
                    "RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getProfilePhotoUser(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public ProfilePhotoUser update(ProfilePhotoUser entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "UPDATE \"profilePhotoUser\" SET " +
                    "\"profilePhotoPath\" = '" + entity.getPathProfilePhoto() + "' " +
                    "WHERE id = " + entity.getId() + " " +
                    "RETURNING *";
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

    private ProfilePhotoUser getProfilePhotoUser(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String path = resultSet.getString("profilePhotoPath");
        ProfilePhotoUser profilePhotoUser = new ProfilePhotoUser(path);
        profilePhotoUser.setId(id);
        return profilePhotoUser;
    }
}
