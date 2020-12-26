package socialnetwork.repository.database;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.posts.Post;
import socialnetwork.domain.posts.TextPost;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TextPostDBRepository implements Repository<Long, TextPost> {
    private String url;
    private String username;
    private String password;

    /**
     * Constructor that creates a new TextPostDBRepository
     * @param url String, representing the URL of the Data Base
     * @param username String, representing the Username of the user connecting to the DB
     * @param password Password, representing the Password of the user connecting to the DB
     */
    public TextPostDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Overridden method that gets one specific Text Post
     * @param aLong Long, representing the ID of the Text Post
     * @return null, if the Test Post doesn't exist
     *      non-null Test Post, otherwise
     */
    @Override
    public TextPost findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM \"textPosts\" WHERE id = " + aLong;
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getTextPost(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that gets the list of all Text Posts
     * @return Iterable<TextPost>, representing the list of all Text Posts
     */
    @Override
    public Iterable<TextPost> findAll() {
        List<TextPost> textPostsList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM \"textPosts\"";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                textPostsList.add(getTextPost(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return textPostsList;
    }

    /**
     * Method that adds a new Text Post to the Data Base
     * @param entity TextPost, representing the Text Post to be added
     *         entity must be not null
     * @return null, if the Text Post was added successfully
     *      non-null Text Post, otherwise
     */
    @Override
    public TextPost save(TextPost entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "INSERT INTO \"textPosts\" (\"UserID\", \"Date\", \"Text\") VALUES " +
                    "(" + entity.getUserID() + ", " +
                    "'" + entity.getPostDate() + "', " +
                    "'" + entity.getText() + "') " +
                    "RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return null;
                }
            } catch(PSQLException e) {
                return entity;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return entity;
    }

    /**
     * Method that deletes a Text Post from the Data Base
     * @param aLong Long, representing the ID of the Text Post to be deleted
     * @return null, if the Text Post doesn't exist
     *      non-null Text Post, if the Text Post was deleted successfully
     */
    @Override
    public TextPost delete(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "DELETE FROM \"textPosts\" WHERE ID = " + aLong + " RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getTextPost(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that updates a Text Post in the Data Base
     * @param entity TextPost, representing the new TextPost
     *          entity must not be null
     * @return null, if the Text Post was updated successfully
     *      non-null Text Post, otherwise
     */
    @Override
    public TextPost update(TextPost entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "UPDATE \"textPosts\" SET " +
                    "\"UserID\" = " + entity.getUserID() + ", " +
                    "\"Date\" = '" + entity.getPostDate() + "', " +
                    "\"Text\" = '" + entity.getText() + "' " +
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
     * Method that gets a Text Post from the current position of the Result Set
     * @param resultSet ResultSet, representing the Result Set
     * @return TextPost, representing the Text Post built from the current position of the Result Set
     * @throws SQLException
     */
    private TextPost getTextPost(ResultSet resultSet) throws SQLException {
        Long postID = resultSet.getLong("ID");
        Long userID = resultSet.getLong("UserID");
        Date date = resultSet.getDate("Date");
        String text = resultSet.getString("Text");
        TextPost textPost = new TextPost(userID, date.toLocalDate(), text);
        textPost.setId(postID);
        return textPost;
    }
}
