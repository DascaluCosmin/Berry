package socialnetwork.repository.database;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.Entity;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.posts.PhotoPost;
import socialnetwork.domain.posts.PostLike;
import socialnetwork.domain.posts.PostType;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostLikesDBRepository implements Repository<Tuple<Long, Long>, PostLike> {
    private final String url;
    private final String username;
    private final String password;
    private final PostType postType;

    /**
     * Constructor that creates a new PostLikesDBRepository
     * @param url String, representing the URL of the Data Base
     * @param username String, representing the Username of the user connecting to the DB
     * @param password Password, representing the Password of the user connecting to the DB
     * @param postType PostType, representing the Type of the Posts being Liked - PHOTO or TEXT
     */
    public PostLikesDBRepository(String url, String username, String password, PostType postType) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.postType = postType;
    }

    /**
     * Method that gets one specific PostLike
     * @param idTuple Tuple<Long, Long>, representing the IDs of the Post and User
     * @return null, if the PostLike doesn't exist
     *      non-null PostLike, otherwise
     */
    @Override
    public PostLike findOne(Tuple<Long, Long> idTuple) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "";
            if (postType == PostType.PHOTO) {
                command = "SELECT * FROM \"photoPosts_Likes\" WHERE \"PhotoPostID\" = " + idTuple.getLeft() +
                        " AND \"UserID\" = " + idTuple.getRight();
            } else if (postType == PostType.TEXT) {
                command = "SELECT * FROM \"textPosts_Likes\" WHERE \"TextPostID\" = " + idTuple.getLeft() +
                        " AND \"UserID\" = " + idTuple.getRight();
            }
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())  {
                return getPostLike(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that gets the list of all PostLikes
     * @return Iterable<PostLike>, representing the list of all PostLikes
     */
    @Override
    public Iterable<PostLike> findAll() {
        List<PostLike> listPostLikes = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)){
            String command = "";
            if (postType == PostType.PHOTO) {
                command = "SELECT * FROM \"photoPosts_Likes\"";
            } else if (postType == PostType.TEXT) {
                command = "SELECT * FROM \"textPosts_Likes\"";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                listPostLikes.add(getPostLike(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listPostLikes;
    }

    /**
     * Method that gets the list of all PostLikes corresponding to a Post
     * @param idPost Long, representing the ID of the Post
     * @return Iterable<PostLike>, representing the list of all PostLikes corresponding to the Post
     */
    public Iterable<PostLike> findAll(Long idPost) {
        List<PostLike> listPostLikes = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "";
            if (postType == PostType.PHOTO) {
                command = "SELECT * FROM \"photoPosts_Likes\" WHERE \"PhotoPostID\" = " + idPost;
            } else if (postType == PostType.TEXT) {
                command = "SELECT * FROM \"textPosts_Likes\" WHERE \"TextPostID\" = " + idPost;
            }
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                listPostLikes.add(getPostLike(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listPostLikes;
    }

    /**
     * Method that adds a new PostLike to the Data Base
     * @param entity PostLike, representing the entity to be added
     *         entity must be not null
     * @return null, if the PostLike was added successfully
     *      non-null PostLike, otherwise
     */
    @Override
    public PostLike save(PostLike entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "";
            if (postType == PostType.PHOTO) {
                command = "INSERT INTO \"photoPosts_Likes\" (\"PhotoPostID\", \"UserID\") VALUES " +
                        "(" + entity.getId().getLeft() + ", " + entity.getId().getRight() + ") " +
                        "RETURNING *";
            } else if (postType == PostType.TEXT) {
                command = "INSERT INTO \"textPosts_Likes\" (\"TextPostID\", \"UserID\") VALUES " +
                        "(" + entity.getId().getLeft() + ", " + entity.getId().getRight() + ") " +
                        "RETURNING *";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return null;
                }
            } catch (PSQLException ignored) {
                return entity;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return entity;
    }

    /**
     * Method that deletes a PostLike from the Data Base
     * @param tuple Tuple<Long, Long>, representing the IDs of the Post and User, representing the PostLike
     *                      to be deleted
     * @return null, if the PostLike doesn't exist
     *      non-null PostLike, if the PostLike was deleted successfully
     */
    @Override
    public PostLike delete(Tuple<Long, Long> tuple) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "";
            if (postType == PostType.PHOTO) {
                command = "DELETE FROM \"photoPosts_Likes\" WHERE \"PhotoPostID\" = " + tuple.getLeft() +
                        " AND \"UserID\" = " + tuple.getRight() + " " +
                        "RETURNING *";
            } else if (postType == PostType.TEXT) {
                command = "DELETE FROM \"textPosts_Likes\" WHERE \"TextPostID\" = " + tuple.getLeft() +
                        " AND \"UserID\" = " + tuple.getRight() + " " +
                        "RETURNING *";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return getPostLike(resultSet);
                }
            } catch (PSQLException e) {
                return null;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Note: a PostLike can't be updated - either add, either delete it.
     * @param entity PostLike, representing the new PostLike
     * @return null - a PostLike can't be updated
     */
    @Override
    public PostLike update(PostLike entity) {
        return null;
    }

    /**
     * Method that gets a PostLike from the current position of the Result Set
     * @param resultSet ResultSet, representing the Result Set
     * @return PostLike, representing the PostLike built from the current position of the Result Set
     * @throws SQLException, if a field from the Data Base doesn't exist
     */
    private PostLike getPostLike(ResultSet resultSet) throws SQLException {
        Long idPost = 0L;
        if (postType == PostType.PHOTO) {
            idPost = resultSet.getLong("PhotoPostID");
        } else {
            idPost = resultSet.getLong("TextPostID");
        }
        Long idUser = resultSet.getLong("UserID");
        PostLike postLike = new PostLike();
        postLike.setId(new Tuple<>(idPost, idUser));
        return postLike;
    }
}
