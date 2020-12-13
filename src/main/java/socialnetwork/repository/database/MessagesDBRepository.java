package socialnetwork.repository.database;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.User;
import socialnetwork.domain.messages.Message;
import socialnetwork.repository.Repository;
import socialnetwork.utils.Constants;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MessagesDBRepository implements Repository<Long, Message> {
    private String url;
    private String username;
    private String password;
    private Repository<Long, User> userRepository;

    public MessagesDBRepository(String url, String username, String password, Repository<Long, User> userRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userRepository = userRepository;
    }

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

    public Iterable<Message> findAll(Long idUserTo) {
        return findAllMessages(idUserTo);
    }

    @Override
    public Iterable<Message> findAll() {
        return findAllMessages(null);
    }

    /**
     * Method that gets the list of messages of a specific User
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
