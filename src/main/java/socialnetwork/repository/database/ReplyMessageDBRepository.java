package socialnetwork.repository.database;

import org.postgresql.util.PSQLException;
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

    public ReplyMessageDBRepository(String url, String username, String password, Repository<Long, User> userDBRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userDBRepository = userDBRepository;
    }

    @Override
    public ReplyMessage findOne(Long aLong) {
        return null;
    }

    @Override
    public Iterable<ReplyMessage> findAll() {
        List<ReplyMessage> listReplyMessages = new ArrayList();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM conversations";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
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
                listReplyMessages.add(replyMessage);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listReplyMessages;
    }

    @Override
    public ReplyMessage save(ReplyMessage entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "INSERT INTO conversations (\"idUserFrom\", \"idUserTo\", message, date) " +
                    "VALUES (" + entity.getFrom().getId() + ", " + entity.getTo().get(0).getId() +
                    ", '" + entity.getMessage() + "', '" + entity.getDate().format(Constants.DATE_TIME_FORMATTER) + "') " +
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
    public ReplyMessage delete(Long aLong) {
        return null;
    }

    @Override
    public ReplyMessage update(ReplyMessage entity) {
        return null;
    }
}
