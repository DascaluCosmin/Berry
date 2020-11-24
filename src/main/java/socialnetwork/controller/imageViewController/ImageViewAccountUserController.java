package socialnetwork.controller.imageViewController;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import socialnetwork.domain.ProfilePhotoUser;
import socialnetwork.domain.User;
import socialnetwork.service.ProfilePhotoUserService;
import socialnetwork.utils.ChangeProfilePhoto;
import socialnetwork.utils.ChangeProfilePhotoRectangle;
import socialnetwork.utils.events.ProfilePhotoUserChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageViewAccountUserController implements Observer<ProfilePhotoUserChangeEvent> {
    ImageView profilePhotoImageView;
    ProfilePhotoUserService profilePhotoUserService;
    User user;

    @Override
    public void update(ProfilePhotoUserChangeEvent profilePhotoUserChangeEvent) {
        ChangeProfilePhoto changeProfilePhoto = new ChangeProfilePhotoRectangle();
        changeProfilePhoto.changeProfilePhoto(profilePhotoUserService, profilePhotoImageView, user);
    }

    public void setProfilePhotoImageView(ImageView profilePhotoImageView) {
        this.profilePhotoImageView = profilePhotoImageView;
    }

    public void setProfilePhotoUserService(ProfilePhotoUserService profilePhotoUserService) {
        this.profilePhotoUserService = profilePhotoUserService;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void changeProfilePhoto() {
        ProfilePhotoUser profilePhotoUser = profilePhotoUserService.findOne(user.getId());
        String pathProfilePhoto = "C:\\Users\\dasco\\IdeaProjects\\proiect-lab-schelet\\src\\main\\resources\\images\\noProfilePhoto.png";
        if (profilePhotoUser != null) {
            pathProfilePhoto = profilePhotoUser.getPathProfilePhoto();
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(pathProfilePhoto);
            Image newImage = new Image(fileInputStream, profilePhotoImageView.getFitWidth(),
                    profilePhotoImageView.getFitHeight(), false, true);
            profilePhotoImageView.setImage(newImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
