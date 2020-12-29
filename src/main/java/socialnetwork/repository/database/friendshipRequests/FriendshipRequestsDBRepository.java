package socialnetwork.repository.database.friendshipRequests;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.ContentPage;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;
import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.repository.Repository;
import socialnetwork.utils.Constants;

import javax.lang.model.element.TypeParameterElement;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendshipRequestsDBRepository implements Repository<Long, FriendshipRequest> {
    private String url;
    private String username;
    private String password;
    private Repository<Long, User> userRepository;

    /**
     * Constructor that creates a new FriendshipRequestsDBRepository
     * @param url String, representing the URL of the Data Base
     * @param username String, representing the Username of the user connecting to the DB
     * @param password Password, representing the Password of the user connecting to the DB
     */
    public FriendshipRequestsDBRepository(String url, String username, String password, Repository<Long, User> userRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userRepository = userRepository;
    }

    /**
     * Method that gets one specific Friendship Request
     * @param aLong, representing the ID of the Friendship Request
     * @return null, if the Friendship Request doesn't exist
     *      non-null Friendship Request, otherwise
     */
    @Override
    public FriendshipRequest findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM \"friendshipRequests\" WHERE id = " + aLong;
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getFriendshipRequest(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that gets the list of all Friendship Requests
     * @return Iterable<FriendshipRequest>, representing the list of all Friendship Requests
     */
    @Override
    public Iterable<FriendshipRequest> findAll() {
        List<FriendshipRequest> friendshipRequestList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM \"friendshipRequests\"");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                friendshipRequestList.add(getFriendshipRequest(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return friendshipRequestList;
    }

    /**
     * Method that gets the list of all pending Friendship Requests of an User on a specific Page
     * @param idUser Long, representing the ID of the User the Friendship Requests are sent to
     * @param currentPage ContentPage, representing the Page containing the Friendship Requests
     * @param typeFriendshipRequest TypeFriendshipRequest, representing the Type of the Friendship Request - SENT or RECEIVED
     * @return Iterable<FriendshipRequest>, representing the list of pending Friendship Requests of the User on that Page
     */
    public Iterable<FriendshipRequest> findAll(Long idUser, ContentPage currentPage, TypeFriendshipRequest typeFriendshipRequest) {
        List<FriendshipRequest> friendshipRequestList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String type = "";
            if (typeFriendshipRequest == TypeFriendshipRequest.RECEIVED) {
                type = "\"idUserTo\"";
            } else if (typeFriendshipRequest == TypeFriendshipRequest.SENT) {
                type = "\"idUserFrom\"";
            }
            String command = "SELECT * FROM \"friendshipRequests\"" +
                    " WHERE " + type + " = " + idUser + " AND status = 'pending'" +
                    " LIMIT " + currentPage.getSizePage() +
                    " OFFSET " + (currentPage.getNumberPage() - 1) * currentPage.getSizePage();
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                friendshipRequestList.add(getFriendshipRequest(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return friendshipRequestList;
    }

    /**
     * Method that adds a new Friendship Request to the Data Base
     * @param entity FriendshipRequest, representing the Friendship Request to be added
     *         entity must be not null
     * @return null, if the Friendship Request was added successfully
     *      non-null Friendship Request, otherwise
     */
    @Override
    public FriendshipRequest save(FriendshipRequest entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "INSERT INTO \"friendshipRequests\" (\"idUserFrom\", \"idUserTo\", message, status, date) VALUES " +
                    "(" + entity.getFrom().getId() + ", " + entity.getTo().get(0).getId() + ", '" +
                    entity.getMessage() + "', '" + entity.getStatusRequest() + "', '" +
                    entity.getDate().format(Constants.DATE_TIME_FORMATTER) + "') " +
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
     * Method that deletes a Friendship Request from the Data Base
     * @param aLong Long, representing the ID of the Friendship Request to be deleted
     * @return null, if the Friendship Request doesn't exist
     *      non-null Friendship Request, if the Friendship Request was deleted successfully
     */
    @Override
    public FriendshipRequest delete(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "DELETE FROM \"friendshipRequests\" WHERE id = " + aLong + " " +
                    "RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getFriendshipRequest(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that updates a Friendship Request in the Data Base
     * @param entity FriendshipRequest, representing the new FriendshipRequest
     *          entity must not be null
     * @return null, if the Friendship Request was updated successfully
     *      non-null Friendship Request, otherwise
     */
    @Override
    public FriendshipRequest update(FriendshipRequest entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "UPDATE \"friendshipRequests\" SET " +
                    "\"idUserFrom\" = " + entity.getFrom().getId() + ", " +
                    "\"idUserTo\" = " + entity.getTo().get(0).getId() + ", " +
                    "message = '" + entity.getMessage() + "', " +
                    "status = '" + entity.getStatusRequest() + "', " +
                    "date = '" + entity.getDate().format(Constants.DATE_TIME_FORMATTER) + "' " +
                    "WHERE id = " + entity.getId() + " " +
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
     * Method that gets a Friendship Request from the current position of the Result Set
     * @param resultSet ResultSet, representing the Result Set
     * @return FriendshipRequest, representing the  Friendship Request built from the current position of the Result Set
     * @throws SQLException
     */
    private FriendshipRequest getFriendshipRequest(ResultSet resultSet) throws SQLException {
        User userFrom = userRepository.findOne(resultSet.getLong("idUserFrom"));
        User userTo = userRepository.findOne(resultSet.getLong("idUserTo"));
        String message = resultSet.getString("message");
        LocalDateTime date = LocalDateTime.parse(resultSet.getString("date"), Constants.DATE_TIME_FORMATTER);
        String status = resultSet.getString("status");
        FriendshipRequest friendshipRequest = new FriendshipRequest(userFrom, Arrays.asList(userTo), message, date, status);
        friendshipRequest.setId(resultSet.getLong("id"));
        return friendshipRequest;
    }
}