package socialnetwork.service;

import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipDBRepository;
import socialnetwork.repository.database.UserDBRepository;
import socialnetwork.service.validators.ValidatorService;
import socialnetwork.service.validators.ValidatorUserService;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.UserChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class UserService implements Observable<UserChangeEvent> {
    private final UserDBRepository repositoryUser;
    private final FriendshipDBRepository friendshipRepository;
    private final ValidatorService<User> validatorUserService = new ValidatorUserService();
    private List<Observer<UserChangeEvent>> observers = new ArrayList<>();

    /**
     * Constructor that creates a new UserService
     * @param repositoryUser UserDBRepository, representing the Repository that handles the User data
     * @param friendshipRepository FriendshipDBRepository, representing the Repository
     *                             that handles the Friendship data
     */
    public UserService(UserDBRepository repositoryUser, FriendshipDBRepository friendshipRepository) {
        this.repositoryUser = repositoryUser;
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * Method that adds a new User
     * @param userParam User, representing the User to be added
     * @return null, if the given User is saved
     *         non-null User, otherwise (User with the same id already exists)
     * @throws ValidationException if
     *      the user do be added already exists
     */
    public User addUser(User userParam) throws ValidationException {
        User user = repositoryUser.save(userParam);
        notifyAll(new UserChangeEvent(ChangeEventType.ADD));
        return user;
    }

    /**
     * Method that deletes an User
     * @param userIDParam Long, representing the id of the User to be deleted
     * @return null, if the User to be deleted doesn't exist
     *         non-null User, representing the User that was deleted, otherwise
     * @throws ValidationException if
     *      the user to be deleted doesn't exist
     */
    public User deleteUser(Long userIDParam) throws ValidationException {
        User userToGetDeleted = repositoryUser.findOne(userIDParam);
        if (userToGetDeleted != null) {
            Iterable<User> listUsers = userToGetDeleted.getFriends();
            listUsers.forEach(userDummy -> {
                friendshipRepository.delete(new Tuple<>(userIDParam, userDummy.getId()));
                friendshipRepository.delete(new Tuple<>(userDummy.getId(), userIDParam));
                userDummy.getFriends().removeIf(userToBeDeleted -> userToBeDeleted.getId().equals(userIDParam));
            });
        }
        User user = repositoryUser.delete(userIDParam);
        validatorUserService.validateDelete(user);
        if (user != null) {
            notifyAll(new UserChangeEvent(ChangeEventType.DELETE, userToGetDeleted));
        }
        return user;
    }

    /**
     * Method that gets all the existing Users
     * @return Iterable<User>, representing all the existing Users
     */
    public Iterable<User> getAll(){
        return repositoryUser.findAll();
    }

    /**
     * Method that gets all the existing Users on a specific Page
     * @param page ContentPage, representing the Page containing the Users
     * @return Iterable<User>, representing all the existing Users on that Page
     */
    public Iterable<User> getAll(ContentPage page) {
        return repositoryUser.findAll(page);
    }

    /**
     * Method that gets all the existing Users (in DTO format)
     * @return List<UserDTO>, representing all the existing Users (in DTO format)
     */
    public List<UserDTO> getAllUserDTO() {
        List<UserDTO> userDTOsList = new ArrayList<>();
        getAll().forEach(user -> {
            UserDTO userDTO = new UserDTO(user.getFirstName(), user.getLastName());
            userDTO.setId(user.getId());
            userDTOsList.add(userDTO);
        });
        return userDTOsList;
    }

    /**
     * Method that gets the friends of a User
     * @param idUser Long, representing the ID of the User
     * @return Iterable<User>, representing the friends of the User
     */
    public Iterable<User> getAllFriends(Long idUser) {
        List<User> friendsUser = new ArrayList<>();
        List<Long> idsFriendsUser = new ArrayList<>();
        friendshipRepository.findAll().forEach(ids -> {
            if (ids.getId().getLeft().equals(idUser))
                idsFriendsUser.add(ids.getId().getRight());
        });
        idsFriendsUser.forEach(id -> {
            friendsUser.add(getUser(id));
        });
        return friendsUser;
    }

    /**
     * Method that gets the list of friends of a User on a specific Page
     * @param idUser Long, representing the ID of the User
     * @param page ContentPage, representing the Page containing the Users
     * @return Iterable<User>, representing the friends of the User on that Page
     */
    public Iterable<User> getAllFriends(Long idUser, ContentPage page) {
        List<User> friendsUser = new ArrayList<>();
        friendshipRepository.findAll(idUser, page).forEach(ids -> {
            friendsUser.add(getUser(ids.getId().getRight()));
        });
        return friendsUser;
    }


    /**
     * Method that gets one specific User
     * @param userID Long, representing the ID of the User to be selected
     * @return non-null User, representing the selected User (if the ID of the user exists)
     *         null, otherwise
     */
    public User getUser(Long userID) {
        return repositoryUser.findOne(userID);
    }

    /**
     * Method that gets one specific User (in DTO format)
     * @param userID Long, representing the ID of the User to be selected
     * @return non-null UserDTO, representing the selected User (in DTO format, if the ID of the user exists)
     *         null, otherwise
     */
    public UserDTO getUserDTO(Long userID) {
        User user = getUser(userID);
        if (user == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO(user.getFirstName(), user.getLastName());
        userDTO.setId(userID);
        return userDTO;
    }

    public Long getMaximumUserID() {
        AtomicReference<Long> maximumUserID = new AtomicReference(-1L);
        getAll().forEach(user -> {
            if (user.getId() > maximumUserID.get()) {
                maximumUserID.set(user.getId());
            }
        });
        return maximumUserID.get();
    }

    @Override
    public void addObserver(Observer<UserChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UserChangeEvent> e) {
    }

    @Override
    public void notifyAll(UserChangeEvent userChangeEvent) {
        observers.forEach(observer -> observer.update(userChangeEvent));
    }
}
