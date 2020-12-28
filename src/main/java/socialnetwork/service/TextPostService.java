package socialnetwork.service;

import javafx.collections.ObservableList;
import socialnetwork.domain.ContentPage;
import socialnetwork.domain.posts.TextPost;
import socialnetwork.repository.database.TextPostDBRepository;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.TextPostEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class TextPostService implements Observable<TextPostEvent> {
    private final TextPostDBRepository textPostDBRepository;
    private List<Observer<TextPostEvent>> observers = new ArrayList<>();

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
        TextPost textPost = textPostDBRepository.save(textPostToBeAdded);
        if (textPost == null) {
            notifyAll(new TextPostEvent(ChangeEventType.ADD));
        }
        return textPost;
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
     * Method that gets the list of Text Posts
     * @return List<TextPost>, representing the list of Text Posts
     */
    public List<TextPost> getListTextPosts() {
        List<TextPost> listTextPosts = new ArrayList<>();
        textPostDBRepository.findAll().forEach(listTextPosts::add);
        return listTextPosts;
    }

    /**
     * Method that gets the list of Text Posts of a User
     * @param idUser List<TextPost>, representing the list of Text Posts in descending order by Date
     * @return List<TextPost>, representing the list of Text Posts
     */
    public List<TextPost> getListTextPosts(Long idUser) {
        List<TextPost> listTextPosts = new ArrayList<>();
        textPostDBRepository.findAll(idUser).forEach(listTextPosts::add);
        return listTextPosts;
    }

    /**
     * Method that gets the list of Text Posts of a User on a specific Page
     * @param idUser Long, representing the ID of the User
     * @param page ContentPage, representing the Page containing the Text Posts
     * @return List<TextPost>, representing the list of Text Posts on that Page
     */
    public List<TextPost> getListTextPosts(Long idUser, ContentPage page) {
        List<TextPost> listTextPosts = new ArrayList<>();
        textPostDBRepository.findAll(idUser, page).forEach(listTextPosts::add);
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

    /**
     * Method that gets the number of Text Posts an User has
     * @param idUser Long, representing the ID of the User
     * @return Integer, representing the number of Text Posts the User has
     */
    public Integer getNumberTextPosts(Long idUser) {
        return getListTextPosts(idUser).size();
    }

    /**
     * Overridden method that adds a new Observer to the list of Observers
     * @param observer Observer<TextPostEvent>, representing the Observer to be added
     */
    @Override
    public void addObserver(Observer<TextPostEvent> observer) {
        observers.add(observer);
    }

    /**
     * Overridden method that deletes an Observer from the list of Observers
     * @param observer Observer<TextPostEvent>, representing the Observer to be removed
     */
    @Override
    public void removeObserver(Observer<TextPostEvent> observer) {
        observers.remove(observer);
    }

    /**
     * Method that notifies all of the Observer that an event has occurred
     * @param textPostEvent TextPostEvent, representing the event that has occurred
     */
    @Override
    public void notifyAll(TextPostEvent textPostEvent) {
        observers.forEach(observer -> observer.update(textPostEvent));
    }
}
