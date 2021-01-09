package socialnetwork.service.postsServices;

import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.posts.Post;
import socialnetwork.domain.posts.PostLike;
import socialnetwork.repository.database.PostLikesDBRepository;

import java.util.ArrayList;
import java.util.List;

public class PostLikesService {
    private final PostLikesDBRepository postLikesRepository;

    /**
     * Constructor that creates a new PostLikesService
     * @param postLikesRepository postLikesRepository, representing the Repository
     *                                 handling the Post Likes data
     */
    public PostLikesService(PostLikesDBRepository postLikesRepository) {
        this.postLikesRepository = postLikesRepository;
    }

    /**
     * Method that adds a new Like to a Post
     * @param post Post, representing the Post to be liked
     *        post must be non-null
     * @param user User, representing the User liking the Post
     *        user must be non-null
     * @return null, if the Like was added successfully
     *      non-null Post, otherwise
     */
    public PostLike likePost(Post post, User user) {
        return postLikesRepository.save(new PostLike(post.getId(), user.getId()));
    }

    /**
     * Method that deletes a Like from a Post
     * @param post Post, representing the Post to delete the Like from
     *                  post must be non-null
     * @param user User, representing the User unliking the Post
     *             user must be non-null
     * @return non-null PostLike, if the Like was deleted successfully
     *      null, otherwise
     */
    public PostLike unlikePhotoPost(Post post, User user) {
        return postLikesRepository.delete(new Tuple<>(post.getId(), user.getId()));
    }

    /**
     * Method that gets the Like of a User to a Post
     * @param post Post, representing the liked Post
     *                  post must be non-null
     * @param user User, representing the User liking the Post
     *             post must be non-null
     * @return non-null PostLike, if the User likes the Post
     *      null, otherwise
     */
    public PostLike getLikePhotoPost(Post post, User user) {
        return postLikesRepository.findOne(new Tuple<>(post.getId(), user.getId()));
    }

    /**
     * Method that gets the list of all Post Likes of a Post
     * @param post Post, representing the liked Post
     *                  post must be non-null
     * @return List<PostLike>, representing the likes of the Post
     */
    public List<PostLike> getListLikes(Post post) {
        List<PostLike> postLikes = new ArrayList<>();
        postLikesRepository.findAll(post.getId()).forEach(postLikes::add);
        return postLikes;
    }

    /**
     * Method that gets the number of Likes of a Post
     * @param post Post, representing the liked Post
     * @return int, representing the number of Likes
     */
    public int getNumberOfLikes(Post post) {
        return getListLikes(post).size();
    }
}
