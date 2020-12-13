package socialnetwork.repository.database;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.User;
import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.repository.Repository;
import socialnetwork.utils.Constants;

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

    public FriendshipRequestsDBRepository(String url, String username, String password, Repository<Long, User> userRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userRepository = userRepository;
    }

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
