package socialnetwork.service.validators;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.validators.ValidationException;

public class ValidatorFriendshipService implements ValidatorService<Friendship> {

    /**
     * Method that validates a Friendship upon adding
     * @param entity Friendship, representing the Friendship to be validated
     * @throws ValidationException if
     *      the Friendship is not null, i.e. the Friendship was not added successfully because it already exists
     */
    @Override
    public void validateAdd(Friendship entity) throws ValidationException {
        if (entity != null) {
            throw new ValidationException("The friendship to be added already exists!");
        }
    }

    /**
     * Method that validates a Friendship upon deletion
     * @param entity Friendship, representing the Friendship to be validated
     * @throws ValidationException if
     *      the Friendship is null, i.e. the Friendship to be deleted does not exist
     */
    @Override
    public void validateDelete(Friendship entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("The friendship to be deleted doesn't exist!");
        }
    }
}