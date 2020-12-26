package socialnetwork.service;

import socialnetwork.domain.posts.TextPost;
import socialnetwork.repository.database.TextPostDBRepository;

import java.util.ArrayList;
import java.util.List;

public class TextPostService {
    private final TextPostDBRepository textPostDBRepository;

    /**
     * Constructor that creates a new TextPostService
     * @param textPostDBRepository, representing the Repository that handles the Text Posts data
     */
    public TextPostService(TextPostDBRepository textPostDBRepository) {
        this.textPostDBRepository = textPostDBRepository;
    }

    /**
     * Method that adds a new Text Post
     * @param textPostToBeAdded TextPost, representing the Text Post to be added
     * @return null, if the Text Post was added successfully,
     *      non-null TextPost, otherwise
     */
    public TextPost addTextPost(TextPost textPostToBeAdded) {
        return textPostDBRepository.save(textPostToBeAdded);
    }

    /**
     * Method that deletes a Text Post
     * @param idTextPost Long, representing the Text Post to be deleted
     * @return null, if the Text Post doesn't exist
     *      non-null TextPost, if the Text Post was deleted successfully
     */
    public TextPost deleteTextPost(Long idTextPost) {
        return textPostDBRepository.delete(idTextPost);
    }

    /**
     * Method that gets the list of text posts
     * @return List<TextPost>, representing the list of text posts
     */
    public List<TextPost> getListTextPosts() {
        List<TextPost> listTextPosts = new ArrayList<>();
        textPostDBRepository.findAll().forEach(listTextPosts::add);
        return listTextPosts;
    }

    /**
     * Method that gets the list of text posts of a User
     * @param idUser List<TextPost>, representing the list of text posts in descending order by Date
     * @return
     */
    public List<TextPost> getListTextPosts(Long idUser) {
        List<TextPost> listTextPosts = new ArrayList<>();
        textPostDBRepository.findAll(idUser).forEach(listTextPosts::add);
        return listTextPosts;
    }

    /**
     * Method that gets a specific Text Post
     * @param idTextPost Long, representing the ID of the Text Post to be selected
     * @return TextPost, representing the Text Post to be selected
     */
    public TextPost getTextPost(Long idTextPost) {
        return textPostDBRepository.findOne(idTextPost);
    }
}
