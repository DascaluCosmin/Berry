package socialnetwork.domain.posts;

import java.time.LocalDate;

public class TextPost extends Post{
    private String text;

    /**
     * Constructor that creates a new Text Post
     * @param userID   Long, representing the ID of the User writing the post
     * @param postDate LocalDate, representing the Date the post was made on
     * @param text String, representing the text of the Post
     */
    public TextPost(Long userID, LocalDate postDate, String text) {
        super(userID, postDate);
        this.text = text;
    }

    /**
     * @return String, representing the text of the Post
     */
    public String getText() {
        return text;
    }

    /**
     * @param text String, representing the text of the Post
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Method that serializes a Text Post
     * @return String, representing the serialization of the Text Post
     */
    @Override
    public String toString() {
        return super.toString() + " " +
                "Text = " + text;
    }
}
