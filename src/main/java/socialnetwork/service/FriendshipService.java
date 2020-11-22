package socialnetwork.service;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.service.validators.ValidatorFriendshipService;
import socialnetwork.service.validators.ValidatorService;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class FriendshipService implements Observable<FriendshipChangeEvent> {
    private final Repository<Tuple<Long, Long>, Friendship> repositoryFriendship;
    private final Repository<Long, User> repositoryUser;
    private final ValidatorService<Friendship> validatorFriendshipService = new ValidatorFriendshipService();
    private List<Observer<FriendshipChangeEvent>> observers = new ArrayList<>();

    /**
     * Constructor that creates a new FriendshipService
     * @param repositoryFriendship Repository<Tuple<Long, Long>, Friendship>, representing the Repository
     *                             that handles the Friendship data
     * @param repositoryUser Repository<Long, User>, representing the Repository that handles the User data
     */
    public FriendshipService(Repository<Tuple<Long, Long>, Friendship> repositoryFriendship, Repository<Long, User> repositoryUser) {
        this.repositoryFriendship = repositoryFriendship;
        this.repositoryUser = repositoryUser;
    }

    /**
     * Method that adds a new Friendship
     * @param friendshipParam Friendship, representing the Friendship to be added
     * @return null, if the Friendship was added successfully
     *         non-null Friendship, otherwise (the Friendship already exists)
     * @throws ValidationException, if the Friendship already exists
     */
    public Friendship addFriendship(Friendship friendshipParam) throws ValidationException {
        Friendship friendship = repositoryFriendship.save(friendshipParam);
        validatorFriendshipService.validateAdd(friendship);
        User userLeft = repositoryUser.findOne(friendshipParam.getId().getLeft());
        User userRight = repositoryUser.findOne(friendshipParam.getId().getRight());
        userLeft.getFriends().add(userRight);
        userRight.getFriends().add(userLeft);
        return friendship;
    }

    /**
     * Method that deletes a Friendship
     * @param ids Tuple<Long, Long>, representing the IDs of the Friendship to be deleted
     * @return null, if the Friendship to be deleted doesn't exist
     *         non-null Friendship, representing the deleted Friendship, otherwise
     * @throws ValidationException, if the Friendship doesn't exist
     */
    public Friendship deleteFriendship(Tuple<Long, Long> ids) throws ValidationException {
        Friendship friendship = repositoryFriendship.delete(ids);
        validatorFriendshipService.validateDelete(friendship);
        if (friendship != null) {
            notifyObserver(new FriendshipChangeEvent(ChangeEventType.DELETE, friendship));
        }
        return friendship;
    }

    /**
     * Method that gets all the existing Friendships
     * @return Iterable<Friendship>, representing all the existing Friendships
     */
    public Iterable<Friendship> getAll() {
        return repositoryFriendship.findAll();
    }

    /**
     * Method that gets all the existing Friendships of a specific User
     * @param userID Long, representing the ID of the User
     * @return Iterable<Friendship>, representing all the existing Friendships of an User
     */
    public Iterable<Friendship> getAllFriendshipsUser(Long userID) {
        Iterable<Friendship> allFriendships = this.getAll();
        List<Friendship> listFriendshipsUser = new ArrayList<>();
        allFriendships.forEach(friendship -> {
            if (friendship.getId().getLeft().equals(userID))
                listFriendshipsUser.add(friendship);
        });
        return listFriendshipsUser;
    }

    public Iterable<Friendship> getAllNonFriendshipsUser(Long userID) {
        Iterable<Friendship> allFriendships = this.getAll();
        List<Friendship> listNonFriendshipsUser = new ArrayList<>();
        allFriendships.forEach(friendship ->  {
            if (!friendship.getId().getLeft().equals(userID))
                listNonFriendshipsUser.add(friendship);
        });
        return listNonFriendshipsUser;
    }

    /**
     * Method that gets one specific Friendship
     * @param idLeft Long, representing the ID of the left User forming the Friendship pair
     * @param idRight Long, representing the ID of the right User forming the Friendship pair
     * @return non-null Friendship, representing the selected Friendship (if the pair of ids of the Friendship exists)
     *         null, otherwise
     */
    public Friendship findOne(Long idLeft, Long idRight) {
        return repositoryFriendship.findOne(new Tuple<>(idLeft, idRight));
    }

    @Override
    public void addObserver(Observer<FriendshipChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendshipChangeEvent> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObserver(FriendshipChangeEvent friendshipChangeEvent) {
        observers.stream().forEach(observer -> observer.update(friendshipChangeEvent));
    }
}