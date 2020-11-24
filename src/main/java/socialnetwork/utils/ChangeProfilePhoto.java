package socialnetwork.utils;

import javafx.scene.image.ImageView;
import socialnetwork.domain.User;
import socialnetwork.service.ProfilePhotoUserService;

public interface ChangeProfilePhoto {
    void changeProfilePhoto(ProfilePhotoUserService profilePhotoUserService, ImageView imageView, User user);
}
