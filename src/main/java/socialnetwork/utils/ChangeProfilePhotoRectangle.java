package socialnetwork.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import socialnetwork.domain.ProfilePhotoUser;
import socialnetwork.domain.User;
import socialnetwork.service.ProfilePhotoUserService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ChangeProfilePhotoRectangle implements ChangeProfilePhoto {
    @Override
    public void changeProfilePhoto(ProfilePhotoUserService profilePhotoUserService, ImageView imageView, User user) {
        ProfilePhotoUser profilePhotoUser = profilePhotoUserService.findOne(user.getId());
        String pathProfilePhoto = "C:\\Users\\dasco\\IdeaProjects\\proiect-lab-schelet\\src\\main\\resources\\images\\noProfilePhoto.png";
        if (profilePhotoUser != null) {
            pathProfilePhoto = profilePhotoUser.getPathProfilePhoto();
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(pathProfilePhoto);
            Image newImage = new Image(fileInputStream, imageView.getFitWidth(),
                    imageView.getFitHeight(), false, true);
            imageView.setImage(newImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
