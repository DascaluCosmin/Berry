package socialnetwork.domain.validators;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;
import socialnetwork.repository.Repository;

import java.util.concurrent.atomic.AtomicBoolean;

public class FriendshipValidator implements Validator<Friendship> {
    private Repository<Long, User> userRepository;

    /**
     * Constructor that creates a new FriendshipValidator, based on the UserRepository
     * @param userRepository Repository<Long, User>, representing the UserRepository to be validated
     */
    public FriendshipValidator(Repository<Long, User> userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Method that validates a Friendship entity
     * @param entity Friendship, representing the Friendship to be validated
     * @throws ValidationException if
     *      the ids are negative numbers,
     *      the ids are the same,
     *      the ids do not exist
     */
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String errors = "";
        if (entity.getId().getLeft() < 0) {
            errors += "The left id of the friendship can't be a negative number !\n";
        }
        if (entity.getId().getRight() < 0) {
            errors += "The id of the user can't be a negative number !\n";
        }
        if (entity.getId().getLeft() == entity.getId().getRight()) {
            errors += "The ids are the same!\n";
        }
        Iterable<User> listUsers = this.userRepository.findAll();
        AtomicBoolean leftIdExists = new AtomicBoolean(false);
        AtomicBoolean rightIdExists = new AtomicBoolean(false);
        listUsers.forEach(user -> {
            if (user.getId().equals(entity.getId().getLeft()))
                leftIdExists.set(true);
            if (user.getId().equals(entity.getId().getRight()))
                rightIdExists.set(true);
        });
        if (!leftIdExists.get()) {
            errors += "The left id is not valid - doesn't exist!\n";
        }
        if (!rightIdExists.get()) {
            errors += "The right id is not valid - doesn't exist!\n";
        }
        if (errors.length() > 0)
            throw new ValidationException(errors);
    }
}
