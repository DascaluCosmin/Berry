package socialnetwork.domain.validators;

import socialnetwork.domain.ProfilePhotoUser;

public class ValidatorProfilePhotoUser implements Validator<ProfilePhotoUser> {
    @Override
    public void validate(ProfilePhotoUser entity) throws ValidationException {
        String errors = "";
        if (entity.getPathProfilePhoto().matches("[ ]*")) {
            errors += "The path to profile photo can't contain only blank spaces!\n";
        }
        if (errors.length() > 0) {
            throw new ValidationException(errors);
        }
    }
}
