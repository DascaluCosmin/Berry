package socialnetwork.repository.database.messages;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.ContentPage;
import socialnetwork.domain.User;
import socialnetwork.domain.messages.ReplyMessage;
import socialnetwork.repository.Repository;
import socialnetwork.service.UserService;
import socialnetwork.utils.Constants;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReplyMessageDBRepository implements Repository<Long, ReplyMessage> {
    private String url;
    private String username;
    private String password;
    private Repository<Long, User> userDBRepository;

    /**
     * Constructor that creates a new ReplyMessageDBRepository
     * @param url String, representing the URL of the Data Base
     * @param username String, representing the Username of the user connecting to the DB
     * @param password Password, representing the Password of the user connecting to the DB
     */
    public ReplyMessageDBRepository(String url, String username, String password, Repository<Long, User> userDBRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userDBRepository = userDBRepository;
    }

    /**
     * Method that gets one specific ReplyMessage
     * @param aLong, representing the ID of the ReplyMessage
     * @return null, if the ReplyMessage doesn't exist
     *      non-null ReplyMessage, otherwise
     */
    @Override
    public ReplyMessage findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM conversations WHERE id = " + aLong;
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getReplyMessage(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that gets the list of all ReplyMessage
     * @return Iterable<ReplyMessage>, representing the list of all ReplyMessage
     */
    @Override
    public Iterable<ReplyMessage> findAll() {
        List<ReplyMessage> listReplyMessages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM conversations";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                listReplyMessages.add(getReplyMessage(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listReplyMessages;
    }

    /**
     * Method that gets the list of all ReplyMessage between two Users
     * @param idUserFrom, Long, representing the ID of the First User
     * @param idUserTo Long, representing the ID of the Second User
     * @param page non-null ContentPage, representing the page containing the Reply Messages
     *             null, meaning the command will get all the Reply Messages
     * @param orderByMostRecent Boolean, representing whether the Reply Messages should be ordered by the most recent
     * @return Iterable<ReplyMessage>, representing the list of all ReplyMessage between the two Users
     */
    public Iterable<ReplyMessage> findAll(Long idUserFrom, Long idUserTo, ContentPage page, Boolean orderByMostRecent) {
        List<ReplyMessage> listReplyMessages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(pageToCommand(idUserFrom, idUserTo, page, orderByMostRecent));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                listReplyMessages.add(getReplyMessage(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listReplyMessages;
    }

    /**
     * Method that adds a new ReplyMessage to the Data Base
     * @param entity ReplyMessage, representing the entity to be added
     *         entity must be not null
     * @return null, if the ReplyMessage was added successfully
     *      non-null ReplyMessage, otherwise
     */
    @Override
    public ReplyMessage save(ReplyMessage entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "INSERT INTO conversations (\"idUserFrom\", \"idUserTo\", message, date) " +
                    "VALUES (" + entity.getFrom().getId() + ", " + entity.getTo().get(0).getId() +
                    ", '" + entity.getMessage().replace("'", "`") + "', '" + entity.getDate().format(Constants.DATE_TIME_FORMATTER) + "') " +
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
     * Method that deletes a ReplyMessage from the Data Base
     * @param aLong Long, representing the ID of the ReplyMessage to be deleted
     * @return null, if the ReplyMessage doesn't exist
     *      non-null ReplyMessage, if the ReplyMessage was deleted successfully
     */
    @Override
    public ReplyMessage delete(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "DELETE FROM conversations WHERE id = " + aLong + " " +
                    "RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getReplyMessage(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that updates a ReplyMessage in the Data Base
     * @param entity ReplyMessage, representing the new ReplyMessage
     *          entity must not be null
     * @return null, if the ReplyMessage was updated successfully
     *      non-null ReplyMessage, otherwise
     */
    @Override
    public ReplyMessage update(ReplyMessage entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "UPDATE conversations SET " +
                    "\"idUserFrom\" = " + entity.getFrom().getId() + ", " +
                    "\"idUserTo\" = " + entity.getTo().get(0).getId() + ", " +
                    "message = '" + entity.getMessage() + "', " +
                    "date = '" + entity.getDate().format(Constants.DATE_TIME_FORMATTER)+ "' WHERE id = " + entity.getId() + " " +
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
     * Method that gets a ReplyMessage from the current position of the Result Set
     * @param resultSet ResultSet, representing the Result Set
     * @return ReplyMessage, representing the ReplyMessage built from the current position of the Result Set
     * @throws SQLException, if a field from the Data Base doesn't exist
     */
    private ReplyMessage getReplyMessage(ResultSet resultSet) throws SQLException {
        Long idReplyMessage = resultSet.getLong("id");
        Long idUserFrom = resultSet.getLong("idUserFrom");
        Long idUserTo = resultSet.getLong("idUserTo");
        String message = resultSet.getString("message");
        String dateStringFormat = resultSet.getString("date");
        User userFrom = userDBRepository.findOne(idUserFrom);
        User userTo = userDBRepository.findOne(idUserTo);
        LocalDateTime date = LocalDateTime.parse(dateStringFormat, Constants.DATE_TIME_FORMATTER);
        ReplyMessage replyMessage = new ReplyMessage(userFrom, Arrays.asList(userTo), message, date, null);
        replyMessage.setId(idReplyMessage);
        return replyMessage;
    }

    /**
     * Method that gets the specific SQL Command to be run, based on the ContentPage
     * @param idUserFrom Long, representing the ID of the First User
     * @param idUserTo Long, representing the ID of the Second User
     * @param page non-null ContentPage, representing the page containing the Reply Messages
     *             null, meaning the command will get all the Reply Messages
     * @param orderByMostRecent Boolean, representing whether the Reply Messages should be ordered by the most recent
     * @return String, representing SQL Command to be run
     */
    private String pageToCommand(Long idUserFrom, Long idUserTo, ContentPage page, Boolean orderByMostRecent) {
        String command = "SELECT * FROM conversations WHERE " +
                "(\"idUserFrom\" = " + idUserFrom + " AND " + "\"idUserTo\" = " + idUserTo +")" + " OR " +
                "(\"idUserFrom\" = " + idUserTo + " AND " + "\"idUserTo\" = " + idUserFrom + ")";
        if (orderByMostRecent) {
            command += " ORDER BY id DESC "; // Assume the Reply Messages are never deleted and the ID is auto increment
            // It's faster than ordering by date, which is a varchar
        }
        if (page != null) {
            command += " LIMIT " + page.getSizePage() + " OFFSET " + (page.getNumberPage() - 1);
        }
        return command;
    }
}
