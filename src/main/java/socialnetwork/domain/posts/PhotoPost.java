package socialnetwork.domain.posts;

import java.time.LocalDate;

public class PhotoPost extends Post{
    private String photoURL;

    /**
     * Constructor that creates a new Photo Post
     *  @param userID   Long, representing the ID of the User writing the post
     * @param postDate LocalDate, representing the Date the post was made on
     * @param photoURL String, representing the URL of the Photo
     */
    public PhotoPost(Long userID, LocalDate postDate, String photoURL) {
        super(userID, postDate);
        this.photoURL = photoURL;
    }

    /**
     * @return String, representing the URL of the Photo
     */
    public String getPhotoURL() {
        return photoURL;
    }

    /**
     * @param photoURL String, representing the URL of the Photo
     */
    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    /**
     * Method that serializes a Photo Post
     * @return String, representing the serialization of the Photo Post
     */
    @Override
    public String toString() {
        return super.toString() + " " +
                "URL = " + photoURL;
    }
}
