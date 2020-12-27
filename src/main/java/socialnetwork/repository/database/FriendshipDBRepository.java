package socialnetwork.repository.database;

import jdk.vm.ci.meta.Local;
import org.postgresql.util.PSQLException;
import socialnetwork.domain.ContentPage;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.repository.Repository;
import socialnetwork.utils.Constants;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDBRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private String url;
    private String username;
    private String password;

    public FriendshipDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Friendship findOne(Tuple<Long, Long> longLongTuple) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM friendships " +
                    "WHERE \"idLeft\" = " + longLongTuple.getLeft() + " AND \"idRight\" = " + longLongTuple.getRight();
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getFriendship(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private Friendship getFriendship(ResultSet resultSet) throws SQLException {
        Long idLeft = resultSet.getLong("idLeft");
        Long idRight = resultSet.getLong("idRight");
        String dateString = resultSet.getString("date");
        LocalDate date = LocalDate.parse(dateString);
        Friendship friendship = new Friendship(date);
        friendship.setId(new Tuple<>(idLeft, idRight));
        return friendship;
    }

    @Override
    public Iterable<Friendship> findAll() {
        List<Friendship> listFriendships = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM friendships");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                listFriendships.add(getFriendship(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listFriendships;
    }

    /**
     * Method that gets the list of all Friendships of a User on a specific Page
     * @param idUser Long, representing the ID of the User
     * @param currentPage ContentPage, representing the Page containing the Friendships
     * @return Iterable<Friendship>, representing the Friendships of the User on that Page
     */
    public Iterable<Friendship> findAll(Long idUser, ContentPage currentPage) {
        List<Friendship> listFriendships = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM friendships WHERE \"idLeft\" = " + idUser + " LIMIT " + currentPage.getSizePage() +
                    " OFFSET " + (currentPage.getNumberPage() - 1) * currentPage.getSizePage();
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                listFriendships.add(getFriendship(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listFriendships;
    }

    @Override
    public Friendship save(Friendship entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)){
            String command = "INSERT INTO friendships (\"idLeft\", \"idRight\", date) VALUES " +
                    "(" + entity.getId().getLeft() + ", " + entity.getId().getRight() + ", '" +
                    entity.getDate().format(DateTimeFormatter.ISO_DATE)+ "')" +
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
    public Friendship delete(Tuple<Long, Long> longLongTuple) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "DELETE FROM friendships WHERE \"idLeft\" = " + longLongTuple.getLeft() + " AND " +
                    "\"idRight\" = " + longLongTuple.getRight() + " " +
                    "RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getFriendship(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Friendship update(Friendship entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "UPDATE friendships SET " +
                    "date = '" + entity.getDate().format(DateTimeFormatter.ISO_DATE) + "' " +
                    "WHERE \"idLeft\" = " + entity.getId().getLeft() + " AND " +
                    "\"idRight\" = " + entity.getId().getRight() + " " +
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
}
