package socialnetwork.repository.database.userPosts;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.ContentPage;
import socialnetwork.domain.posts.PhotoPost;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhotoPostDBRepository implements Repository<Long, PhotoPost> {
    private String url;
    private String username;
    private String password;

    /**
     * Constructor that creates a new PhotoPostDBRepository
     * @param url String, representing the URL of the Data Base
     * @param username String, representing the Username of the user connecting to the DB
     * @param password Password, representing the Password of the user connecting to the DB
     */
    public PhotoPostDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Method that gets one specific Photo Post
     * @param aLong Long, representing the ID of the Photo Post
     * @return null, if the Photo Post doesn't exist
     *      non-null Photo Post, otherwise
     */
    @Override
    public PhotoPost findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM \"photoPosts\" WHERE id = " + aLong;
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getPhotoPost(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that gets the list of all Photo Posts
     * @return Iterable<PhotoPost>, representing the list of all Photo Posts
     */
    @Override
    public Iterable<PhotoPost> findAll() {
        List<PhotoPost> photoPostList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM \"photoPosts\"";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                photoPostList.add(getPhotoPost(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return photoPostList;
    }

    /**
     * Method that gets the list of all Photo Posts of a User
     * @param idUser Long, representing the ID of the User
     * @return Iterable<PhotoPost>, representing the list of all Photo Posts
     */
    public Iterable<PhotoPost> findAll(Long idUser) {
        List<PhotoPost> photoPostList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM \"photoPosts\" WHERE \"UserID\" = " + idUser + " ORDER BY \"Date\" DESC, id ASC";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                photoPostList.add(getPhotoPost(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return photoPostList;
    }

    /**
     * Method that gets the list of all Photo Posts of a User on a specific Page
     * @param idUser Long, representing the ID of the User
     * @param currentPage ContentPage, representing the Page containing the Photo Posts
     * @param postUserType PostUserType, representing the Type of the User posting
     *                USER - meaning the USER posted
     *                FRIEND - meaning the FRIEND of the User posted
     * @param isLeaping Boolean, representing the way the Photo Posts are determined
     *                  (pageNumber - 1) * pageSize or pageNumber - 1
     * @return Iterable<PhotoPost>, representing the list of Photo Posts of the User on that Page
     *      in descending order by Date
     */
    public Iterable<PhotoPost> findAll(Long idUser, ContentPage currentPage, PostUserType postUserType, Boolean isLeaping) {
        List<PhotoPost> photoPostList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(postUserTypeToCommand(idUser, currentPage, postUserType, isLeaping));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                photoPostList.add(getPhotoPost(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return photoPostList;
    }

    /**
     * Method that adds a new Photo Post to the Data Base
     * @param entity PhotoPost, representing the entity to be added
     *         entity must be not null
     * @return null, if the Photo Post was added successfully
     *      non-null Photo Post, otherwise
     */
    @Override
    public PhotoPost save(PhotoPost entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "INSERT INTO \"photoPosts\" (\"UserID\", \"Date\", url) VALUES " +
                    "(" + entity.getUserID() + ", " +
                    "'" + entity.getPostDate() + "', " +
                    "'" + entity.getPhotoURL()+ "') "  +
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
     * Method that deletes a Photo Post from the Data Base
     * @param aLong Long, representing the ID of the Photo Post to be deleted
     * @return null, if the Photo Post doesn't exist
     *      non-null Photo Post, if the Photo Post was deleted successfully
     */
    @Override
    public PhotoPost delete(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "DELETE FROM \"photoPosts\" WHERE id = " + aLong + " RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getPhotoPost(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that updates a Photo Post in the Data Base
     * @param entity PhotoPost, representing the new PhotoPost
     *          entity must not be null
     * @return null, if the PhotoPost was updated successfully
     *      non-null PhotoPost, otherwise
     */
    @Override
    public PhotoPost update(PhotoPost entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "UPDATE \"photoPosts\" SET " +
                    "\"UserID\" = " + entity.getUserID() + ", " +
                    "\"Date\" = '" + entity.getPostDate() + "', " +
                    "url = '" +  entity.getPhotoURL() + "' " +
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
     * Method that gets a Photo Post from the current position of the Result Set
     * @param resultSet ResultSet, representing the Result Set
     * @return PhotoPost, representing the Photo Post built from the current position of the Result Set
     * @throws SQLException, if a field from the Data Base doesn't exist
     */
    private PhotoPost getPhotoPost(ResultSet resultSet) throws SQLException {
        Long postID = resultSet.getLong("ID");
        Long userID = resultSet.getLong("UserID");
        Date date = resultSet.getDate("Date");
        String URL = resultSet.getString("URL");
        PhotoPost photoPost = new PhotoPost(userID, date.toLocalDate(), URL);
        photoPost.setId(postID);
        return photoPost;
    }

    /**
     * Method that gets the specific SQL Command to be run, given the Type of User who did the Post
     * @param idUser Long, representing the ID of the User
     * @param currentPage ContentPage, representing the Page containing the Photo Posts
     * @param postUserType PostUserType, representing the Type of the User posting
     *                USER - meaning the USER posted
     *                FRIEND - meaning the FRIEND of the User posted
     * @param isLeaping Boolean, representing the way the Photo Posts are determined
     *                  (pageNumber - 1) * pageSize or pageNumber - 1
     * @return String, representing the SQL Command to be run
     */
    private String postUserTypeToCommand(Long idUser, ContentPage currentPage, PostUserType postUserType, Boolean isLeaping) {
        String subQuery = "(SELECT \"idRight\" FROM friendships WHERE \"idLeft\" = " + idUser + ")";
        String limitQuery =  "";
        if (isLeaping) {
            limitQuery = " LIMIT " + currentPage.getSizePage() +
                    " OFFSET " + (currentPage.getNumberPage() - 1) * currentPage.getSizePage();
        } else {
            limitQuery = " LIMIT " + currentPage.getSizePage() +
                    " OFFSET " + (currentPage.getNumberPage() - 1);
        }
        if (postUserType == PostUserType.USER) {
            return "SELECT * FROM \"photoPosts\" WHERE \"UserID\" = " + idUser + " ORDER BY \"Date\" DESC" + limitQuery;
        }
        // PostUserType.FRIEND
        return "SELECT * FROM \"photoPosts\" WHERE \"UserID\" IN " + subQuery + " ORDER BY \"Date\" DESC" + limitQuery;
    }
}
