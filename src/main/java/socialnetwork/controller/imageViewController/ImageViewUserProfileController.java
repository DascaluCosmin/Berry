package socialnetwork.controller.imageViewController;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import socialnetwork.domain.ProfilePhotoUser;
import socialnetwork.domain.User;
import socialnetwork.domain.UserDTO;
import socialnetwork.service.ProfilePhotoUserService;
import socialnetwork.utils.ChangeProfilePhoto;
import socialnetwork.utils.ChangeProfilePhotoRound;
import socialnetwork.utils.events.ProfilePhotoUserChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageViewUserProfileController implements Observer<ProfilePhotoUserChangeEvent> {
    ImageView imageViewUserProfile;
    ProfilePhotoUserService profilePhotoUserService;
    User user;

    public void setImageViewUserProfile(ImageView imageViewUserProfile) {
        this.imageViewUserProfile = imageViewUserProfile;
    }

    public void setProfilePhotoUserService(ProfilePhotoUserService profilePhotoUserService) {
        this.profilePhotoUserService = profilePhotoUserService;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void update(ProfilePhotoUserChangeEvent profilePhotoUserChangeEvent) {
        ChangeProfilePhoto ChangeProfilePhoto = new ChangeProfilePhotoRound();
        ChangeProfilePhoto.changeProfilePhoto(profilePhotoUserService, imageViewUserProfile, user);
    }
}
