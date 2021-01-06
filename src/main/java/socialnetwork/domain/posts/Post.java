package socialnetwork.domain.posts;

import socialnetwork.domain.Entity;

import java.time.LocalDate;

public class Post extends Entity<Long> {
    private Long userID;
    private LocalDate postDate;

    /**
     * Constructor that creates a new Post
     * @param userID Long, representing the ID of the User writing the post
     * @param postDate LocalDate, representing the Date the post was made on
     */
    public Post(Long userID, LocalDate postDate) {
        this.userID = userID;
        this.postDate = postDate;
    }

    /**
     * @return Long, representing the ID of the User writing the post
     */
    public Long getUserID() {
        return userID;
    }

    /**
     * @param userID Long, representing the ID of the User writing the post
     */
    public void setUserID(Long userID) {
        this.userID = userID;
    }

    /**
     * @return LocalDate, representing the Date the post was made on
     */
    public LocalDate getPostDate() {
        return postDate;
    }

    /**
     * @param postDate LocalDate, representing the Date the post was made on
     */
    public void setPostDate(LocalDate postDate) {
        this.postDate = postDate;
    }

    /**
     * Method that serializes a Post
     * @return String, representing the serialization of the Post
     */
    @Override
    public String toString() {
        return "ID = " + getId() + " " +
            "UserID = " + userID + " " +
            "Date = " + postDate;
    }
}
