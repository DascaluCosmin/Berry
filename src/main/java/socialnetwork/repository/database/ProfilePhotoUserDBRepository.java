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

    /**
     * Constructor that creates a new ProfilePhotoUserDBRepository
     * @param url String, representing the URL of the Data Base
     * @param username String, representing the Username of the user connecting to the DB
     * @param password Password, representing the Password of the user connecting to the DB
     */
    public ProfilePhotoUserDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Method that gets one specific ProfilePhotoUser
     * @param aLong, representing the ID of the ProfilePhotoUser
     * @return null, if the ProfilePhotoUser doesn't exist
     *      non-null ProfilePhotoUser, otherwise
     */
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

    /**
     * Method that gets the list of all ProfilePhotoUser
     * @return Iterable<ProfilePhotoUser>, representing the list of all ProfilePhotoUser
     */
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

    /**
     * Method that adds a new ProfilePhotoUser to the Data Base
     * @param entity ProfilePhotoUser, representing the entity to be added
     *         entity must be not null
     * @return null, if the ProfilePhotoUser was added successfully
     *      non-null ProfilePhotoUser, otherwise
     */
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

    /**
     * Method that deletes a ProfilePhotoUser from the Data Base
     * @param aLong Long, representing the ID of the ProfilePhotoUser to be deleted
     * @return null, if the ProfilePhotoUser doesn't exist
     *      non-null ProfilePhotoUser, if the ReplyMessage was deleted successfully
     */
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

    /**
     * Method that updates a ProfilePhotoUser in the Data Base
     * @param entity ProfilePhotoUser, representing the new ProfilePhotoUser
     *          entity must not be null
     * @return null, if the ProfilePhotoUser was updated successfully
     *      non-null ProfilePhotoUser, otherwise
     */
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

    /**
     * Method that gets a ProfilePhotoUser from the current position of the Result Set
     * @param resultSet ResultSet, representing the Result Set
     * @return ProfilePhotoUser, representing the ProfilePhotoUser built from the current position of the Result Set
     * @throws SQLException, if a field from the Data Base doesn't exist
     */
    private ProfilePhotoUser getProfilePhotoUser(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String path = resultSet.getString("profilePhotoPath");
        ProfilePhotoUser profilePhotoUser = new ProfilePhotoUser(path);
        profilePhotoUser.setId(id);
        return profilePhotoUser;
    }
}
