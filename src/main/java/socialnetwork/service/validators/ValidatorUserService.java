package socialnetwork.service.validators;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;

public class ValidatorUserService implements ValidatorService<User> {

    /**
     * Method that validates an User upon adding
     * @param entity User, representing the User to be validated
     * @throws ValidationException if
     *      the User is not null, i.e. the User was not added successfully because it already exists
     */
    @Override
    public void validateAdd(User entity) throws ValidationException {
        if (entity != null) {
            throw new ValidationException("The user to be added already exists!");
        }
    }

    /**
     * Method that validates an User upon deletion
     * @param entity User, representing the User to be validated
     * @throws ValidationException if
     *      the User is null, i.e. the User to be deleted does not exist
     */
    @Override
    public void validateDelete(User entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("The user to be deleted doesn't exist!");
        }
    }
}
