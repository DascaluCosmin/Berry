package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import socialnetwork.controller.imageViewController.ImageViewUserProfileController;
import socialnetwork.domain.ProfilePhotoUser;
import socialnetwork.domain.User;
import socialnetwork.service.ProfilePhotoUserService;
import socialnetwork.utils.ChangeProfilePhotoRound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class UserProfileController {
    ProfilePhotoUserService profilePhotoUserService;
    ImageViewUserProfileController imageViewUserProfileController = new ImageViewUserProfileController();
    User user;
    Stage userProfileStage;

    @FXML
    ImageView imageViewUserProfile;
    @FXML
    Label labelUserName;

    @FXML
    void initialize() {
    }

    void setUser(User user) {
        this.user = user;
        imageViewUserProfileController.setUser(this.user);
    }

    public void setProfilePhotoUserService(ProfilePhotoUserService profilePhotoUserService) {
        this.profilePhotoUserService = profilePhotoUserService;
        this.profilePhotoUserService.addObserver(imageViewUserProfileController);
        imageViewUserProfileController.setProfilePhotoUserService(this.profilePhotoUserService);
    }

    public void setUserProfileStage(Stage userProfileStage) {
        this.userProfileStage = userProfileStage;
    }

    public void initializeImageViewUserProfile() {
        if (user != null) {
            ChangeProfilePhotoRound changeProfilePhotoRound = new ChangeProfilePhotoRound();
            changeProfilePhotoRound.changeProfilePhoto(profilePhotoUserService, imageViewUserProfile, user);
            labelUserName.setText(user.getFirstName() + " " + user.getLastName());
            imageViewUserProfileController.setImageViewUserProfile(imageViewUserProfile);
        }
    }

    public void changeProfilePhotoEvent() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\Users\\dasco\\OneDrive\\Pictures\\ProfilePhotos"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                new FileChooser.ExtensionFilter("JPG Files", "*.jpg"));
        File file = fileChooser.showOpenDialog(userProfileStage);
        if (file != null) {
            String pathProfilePhoto = file.toString();
            ProfilePhotoUser newProfilePhotoUser = new ProfilePhotoUser(pathProfilePhoto);
            newProfilePhotoUser.setId(user.getId());
            profilePhotoUserService.updateProfilePhotoUser(newProfilePhotoUser);
            ChangeProfilePhotoRound changeProfilePhotoRound = new ChangeProfilePhotoRound();
            changeProfilePhotoRound.changeProfilePhoto(profilePhotoUserService, imageViewUserProfile, user);
        }
    }
}
