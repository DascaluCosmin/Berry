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
import socialnetwork.domain.ProfilePhotoUser;
import socialnetwork.domain.User;
import socialnetwork.service.ProfilePhotoUserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class UserProfileController {
    ProfilePhotoUserService profilePhotoUserService;
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
    }

    public void setProfilePhotoUserService(ProfilePhotoUserService profilePhotoUserService) {
        this.profilePhotoUserService = profilePhotoUserService;
    }

    public void setUserProfileStage(Stage userProfileStage) {
        this.userProfileStage = userProfileStage;
    }

    public void initializeImageViewUserProfile() {
        if (user != null) {
            changeProfilePhoto();
            labelUserName.setText(user.getFirstName() + " " + user.getLastName());
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
            changeProfilePhoto();
        }
    }

    private void changeProfilePhoto() {
        String pathProfilePhoto = profilePhotoUserService.findOne(user.getId()).getPathProfilePhoto();
        try {
            FileInputStream fileInputStream = new FileInputStream(pathProfilePhoto);
            Image image = new Image(
                    fileInputStream, 400, 400, false, false
            );
            imageViewUserProfile.setImage(image);
            Circle clip = new Circle(120, 120, 100);
            imageViewUserProfile.setClip(clip);
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            WritableImage writableImage = imageViewUserProfile.snapshot(parameters, null);
            imageViewUserProfile.setClip(null);
            imageViewUserProfile.setImage(writableImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
