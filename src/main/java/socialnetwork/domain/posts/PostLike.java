package socialnetwork.domain.posts;

import socialnetwork.domain.Entity;
import socialnetwork.domain.Tuple;

public class PostLike extends Entity<Tuple<Long, Long>> {
    /**
     * A PostLike extends Entity<Tuple<Long, Long>>. It has 2 IDs: (PostID, UserID)
     */

    /**
     * Constructor that creates a new PostLike
     * @param idPost Long, representing the ID of the Post
     * @param idUser Long, representing the ID of the User
     */
    public PostLike(Long idPost, Long idUser) {
        setId(new Tuple<>(idPost, idUser));
    }

    /**
     * Constructor that creates a new PostLike
     */
    public PostLike() {
    }

    /**
     * @return String, representing the serialization of a PostLike
     */
    @Override
    public String toString() {
        return "The User with ID = " + getId().getRight() + " likes the Post with ID = " + getId().getLeft();
    }
}
