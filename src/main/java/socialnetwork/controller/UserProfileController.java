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
import socialnetwork.service.UserService;
import socialnetwork.utils.ChangeProfilePhoto;
import socialnetwork.utils.ChangeProfilePhotoRectangle;
import socialnetwork.utils.ChangeProfilePhotoRound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class UserProfileController {
    private ProfilePhotoUserService profilePhotoUserService;
    private UserService userService;
    private ImageViewUserProfileController imageViewUserProfileController = new ImageViewUserProfileController();
    private User user;
    private Stage userProfileStage;

    @FXML
    ImageView imageViewUserProfile;
    @FXML
    ImageView imageViewStUser;
    @FXML
    ImageView imageViewNdUser;
    @FXML
    ImageView imageViewRdUser;
    @FXML
    ImageView imageView4thUser;
    @FXML
    Label labelUserName;

    @FXML
    void initialize() {
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
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

    public ImageView getImageViewUserProfile() {
        return imageViewUserProfile;
    }

    public void initializeUserProfile() {
        if (user != null) {
            ChangeProfilePhotoRound changeProfilePhotoRound = new ChangeProfilePhotoRound();
            changeProfilePhotoRound.changeProfilePhoto(profilePhotoUserService, imageViewUserProfile, user);
            labelUserName.setText(user.getFirstName() + " " + user.getLastName());
            imageViewUserProfileController.setImageViewUserProfile(imageViewUserProfile);
            List<User> friendsUser = user.getFriends();
            Map<Long, User> friendsUserUnique = new HashMap<>();
            for (User currentUser : friendsUser) {
                if (!friendsUserUnique.containsKey(currentUser.getId())) {
                    friendsUserUnique.put(currentUser.getId(), currentUser);
                }
            }
            List<Long> idsFriends = new ArrayList<>();
            friendsUserUnique.keySet().forEach(idUser -> idsFriends.add(idUser));
            Collections.shuffle(idsFriends);
               User stUserFriend = null, ndUserFriend = null, rdUserFriend = null, fourthUserFriend = null;
            if (idsFriends.size() >= 1)
                stUserFriend = userService.getUser(idsFriends.get(0));
            if (idsFriends.size() >= 2)
                ndUserFriend = userService.getUser(idsFriends.get(1));
            if (idsFriends.size() >= 3)
                rdUserFriend = userService.getUser(idsFriends.get(2));
            if (idsFriends.size() >= 4)
                fourthUserFriend = userService.getUser(idsFriends.get(3));
            ChangeProfilePhoto changeProfilePhoto = new ChangeProfilePhotoRectangle();
            if (stUserFriend != null) {
                changeProfilePhoto.changeProfilePhoto(profilePhotoUserService, imageViewStUser, stUserFriend);
            } else {
                imageViewStUser.setVisible(false);
            }
            if (ndUserFriend != null) {
                changeProfilePhoto.changeProfilePhoto(profilePhotoUserService, imageViewNdUser, ndUserFriend);
            } else {
                imageViewNdUser.setVisible(false);
            }
            if (rdUserFriend != null) {
                changeProfilePhoto.changeProfilePhoto(profilePhotoUserService, imageViewRdUser, rdUserFriend);
            } else {
                imageViewRdUser.setVisible(false);
            }
            if (fourthUserFriend != null) {
                changeProfilePhoto.changeProfilePhoto(profilePhotoUserService, imageView4thUser, fourthUserFriend);
            } else {
                imageView4thUser.setVisible(false);
            }
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
