package socialnetwork.repository.file;

import socialnetwork.domain.Entity;
import socialnetwork.domain.ProfilePhotoUser;
import socialnetwork.domain.validators.Validator;

import java.util.List;

public class ProfilePhotoUserFileRepository extends AbstractFileRepository<Long, ProfilePhotoUser> {

    public ProfilePhotoUserFileRepository(String fileName, Validator<ProfilePhotoUser> validator) {
        super(fileName, validator);
    }

    @Override
    public ProfilePhotoUser extractEntity(List<String> attributes) {
        String pathProfilePhoto = attributes.get(1);
        ProfilePhotoUser profilePhotoUser = new ProfilePhotoUser(pathProfilePhoto);
        profilePhotoUser.setId(Long.parseLong(attributes.get(0)));
        return profilePhotoUser;
    }

    @Override
    protected String createEntityAsString(ProfilePhotoUser entity) {
        return entity.getId() + ";" + entity.getPathProfilePhoto();
    }
}
