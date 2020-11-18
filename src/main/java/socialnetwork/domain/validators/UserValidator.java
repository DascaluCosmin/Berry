package socialnetwork.domain.validators;

import socialnetwork.domain.User;

public class UserValidator implements Validator<User> {

    /**
     * Method that validates an User entity
     * @param entity User, representing the User to be validated
     * @throws ValidationException if
     *      the id of the user is a negative number,
     *      the first name or the last name contains digits,
     *      the first name or last name contains only blank spaces
     */
    @Override
    public void validate(User entity) throws ValidationException {
        String errors = "";
        if (entity.getId() < 0) {
            errors += "The id of the user can't be a negative number !\n";
        }
        if (entity.getFirstName().matches(".*\\d.*")) {
            errors += "The first name can't contain digits!\n";
        }
        if (entity.getLastName().matches(".*\\d.*")) {
            errors += "The last name can't contain digits!\n";
        }
        if (entity.getFirstName().matches("[ ]*")) {
            errors += "The first name can't contain only blank spaces!\n";
        }
        if (entity.getLastName().matches("[ ]*")) {
            errors += "The last name can't contain only blank spaces!\n";
        }
        if (errors.length() > 0) {
            throw new ValidationException(errors);
        }
    }
}
