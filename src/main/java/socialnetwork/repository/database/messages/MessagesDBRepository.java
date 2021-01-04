package socialnetwork.repository.database.messages;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.ContentPage;
import socialnetwork.domain.User;
import socialnetwork.domain.messages.Message;
import socialnetwork.repository.Repository;
import socialnetwork.utils.Constants;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MessagesDBRepository implements Repository<Long, Message> {
    private String url;
    private String username;
    private String password;
    private Repository<Long, User> userRepository;

    /**
     * Constructor that creates a new MessagesDBRepository
     * @param url String, representing the URL of the Data Base
     * @param username String, representing the Username of the user connecting to the DB
     * @param password Password, representing the Password of the user connecting to the DB
     */
    public MessagesDBRepository(String url, String username, String password, Repository<Long, User> userRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userRepository = userRepository;
    }

    /**
     * Method that gets one specific Message
     * @param aLong, representing the ID of the Message
     * @return null, if the Message doesn't exist
     *      non-null Message, otherwise
     */
    @Override
    public Message findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * " +
                    "FROM messages m " +
                    "INNER JOIN \"messages_To\" mT " +
                    "ON m.id = mT.\"idMessage\" " +
                    "WHERE m.id = " + aLong;
            // Add these 2 attributes to make sure the ResultSet can go backward as well
            PreparedStatement preparedStatement = connection.prepareStatement(command, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                resultSet.previous(); // We have verified the message exists, go back to avoid missing the first user recipient
                return getMessages(resultSet).get(0);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that gets the list of all Messages
     * @return Iterable<Message>, representing the list of Messages
     */
    @Override
    public Iterable<Message> findAll() {
        return findAllMessages(null);
    }

    /**
     * Method that gets the list of Messages of a specific User
     * @param idUserTo Long, representing the ID of the User (null to get all the messages)
     * @return Iterable<Message>, representing the list of messages
     */
    public Iterable<Message> findAll(Long idUserTo) {
        return findAllMessages(idUserTo);
    }

    /**
     * Method that gets the list of all Messages sent to a User, on a specific Page
     * @param page ContentPage, representing the page containing the Messages
     * @return Iterable<Message>, representing the list of all Message sent to the User, on that Page,
     *      ordered descending by Date
     */
    public Iterable<Message> findAll(Long idUser, ContentPage page) {
        List<Message> listMessages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            /**
             *  Assume the Messages are never deleted and the id of the Message is AUTO INCREMENT
             *  Thus, ordering by id is equivalent to ordering by date, but faster, since date is a varchar
             */
            String command = "SELECT * FROM messages WHERE id IN " +
                    "(" +
                    "SELECT \"idMessage\" " +
                    "FROM \"messages_To\" " +
                    "WHERE \"idUserTo\" = " + idUser +
                    ")" +
            " ORDER BY id DESC " + " LIMIT " + page.getSizePage() +
                    " OFFSET " + (page.getNumberPage() - 1) * page.getSizePage();
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long idMessage = resultSet.getLong("id");
                Long idUserFrom = resultSet.getLong("idUserFrom");
                String textMessage = resultSet.getString("message");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date"), Constants.DATE_TIME_FORMATTER);
                User userFrom = userRepository.findOne(idUserFrom);
                User userTo = userRepository.findOne(idUser);
                Message message = new Message(userFrom, Collections.singletonList(userTo), textMessage, date);
                message.setId(idMessage);
                listMessages.add(message);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listMessages;
    }

    /**
     * Method that gets the list of Messages sent to a specific User
     * @param idUserTo Long, representing the ID of the User (null to get all the messages)
     * @return Iterable<Message>, representing the list of messages
     */
    private Iterable<Message> findAllMessages(Long idUserTo) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * " +
                    "FROM messages m " +
                    "INNER JOIN \"messages_To\" mT " +
                    "ON m.id = mT.\"idMessage\"";
            if (idUserTo != null) { // Want the messages of a specific user
                command += " WHERE \"idUserTo\" = " + idUserTo;
            }
            PreparedStatement preparedStatement = connection.prepareStatement(command, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = preparedStatement.executeQuery();
            return getMessages(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Method that adds a new Message to the Data Base
     * @param entity Message, representing the entity to be added
     *         entity must be not null
     * @return null, if the Message was added successfully
     *      non-null Message, otherwise
     */
    @Override
    public Message save(Message entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "INSERT INTO messages (\"idUserFrom\", message, date) VALUES " +
                    "(" + entity.getFrom().getId() + ", '"  + entity.getMessage() + "', '" + entity.getDate().format(Constants.DATE_TIME_FORMATTER) + "') " +
                    "RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) { // The message was added successfully, now add the the user recipients
                    AtomicReference<Boolean> fkException = new AtomicReference<>(false);
                    Long idMessage = resultSet.getLong("id");
                    List<User> listUsersTo = entity.getTo(); // Add each recipient user
                    listUsersTo.forEach(user -> {
                        String commandMessagesTo = "INSERT INTO \"messages_To\" (\"idMessage\", \"idUserTo\") VALUES " +
                                "(" + idMessage + ", " + user.getId() + ")";
                        try {
                            PreparedStatement preparedStatementMessagesTo = connection.prepareStatement(commandMessagesTo);
                            try {
                                preparedStatementMessagesTo.execute();
                            } catch (PSQLException e) { // The recipient user is invalid. FK exception
                               fkException.set(true);
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    });
                    if (fkException.get()) {
                        delete(idMessage);
                        return entity;
                    }
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
     * Method that deletes a Message from the Data Base
     * @param aLong Long, representing the ID of the Message to be deleted
     * @return null, if the Message doesn't exist
     *      non-null Message, if the ReplyMessage was deleted successfully
     */
    @Override
    public Message delete(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Message messageToBeDeleted = findOne(aLong);
            if (messageToBeDeleted != null) {
                String command = "DELETE FROM messages WHERE id = " + aLong;
                PreparedStatement preparedStatement = connection.prepareStatement(command, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                preparedStatement.execute();
                return messageToBeDeleted;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that updates a Message in the Data Base
     * @param entity Message, representing the new Message
     *          entity must not be null
     * @return null, if the Message was updated successfully
     *      non-null Message, otherwise
     */
    @Override
    public Message update(Message entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "UPDATE messages SET " +
                    "\"idUserFrom\" = " + entity.getFrom().getId() + ", " +
                    "message = '" + entity.getMessage() + "', " +
                    "date = '" + entity.getDate().format(Constants.DATE_TIME_FORMATTER)+ "' " +
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
     * Method that builds the list of messages based on a given Result Set
     * @param resultSet ResultSet, representing the Result Set of the SQL Query
     * @return List<Message>, representing the list of Messages from the Database
     * @throws SQLException
     */
    private List<Message> getMessages(ResultSet resultSet) throws SQLException {
        List<Message> messageList = new ArrayList<>();
        while (resultSet.next()) {
            Long idMessage = resultSet.getLong("id");
            User userFrom = userRepository.findOne(resultSet.getLong("idUserFrom"));
            String textMessage = resultSet.getString("message");
            LocalDateTime date = LocalDateTime.parse(resultSet.getString("date"), Constants.DATE_TIME_FORMATTER);
            List<User> listUsersTo = new ArrayList<>();
            resultSet.previous(); // Otherwise, the loop would not find the first recipient user
            while (resultSet.next()) { // Find the user recipients
                if (idMessage != resultSet.getLong("id")) { // The cursor is positioned to a different Message
                    resultSet.previous();
                    break;
                }
                User userTo = userRepository.findOne(resultSet.getLong("idUserTo"));
                listUsersTo.add(userTo);
            }
            Message message = new Message(userFrom, listUsersTo, textMessage, date);
            message.setId(idMessage);
            messageList.add(message);
        }
        return messageList;
    }
}
