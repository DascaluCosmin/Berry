package socialnetwork.service;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.service.validators.ValidatorService;
import socialnetwork.service.validators.ValidatorUserService;

public class UserService {
    private final Repository<Long, User> repositoryUser;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private final ValidatorService<User> validatorUserService = new ValidatorUserService();

    /**
     * Constructor that creates a new UserService
     * @param repositoryUser Repository<Long, User>, representing the Repository that handles the User data
     * @param friendshipRepository Repository<Tuple<Long, Long>, Friendship>, representing the Repository
     *                             that handles the Friendship data
     */
    public UserService(Repository<Long, User> repositoryUser, Repository<Tuple<Long, Long>, Friendship> friendshipRepository) {
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
        validatorUserService.validateAdd(user);
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
     * Method that gets one specific User
     * @param userID Long, representing the id of the User to be selected
     * @return non-null User, representing the selected User (if the ID of the user exists)
     *         null, otherwise
     */
    public User getUser(Long userID) {
        return repositoryUser.findOne(userID);
    }
}
