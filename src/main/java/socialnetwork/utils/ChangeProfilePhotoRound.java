package socialnetwork.utils;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import socialnetwork.domain.User;
import socialnetwork.service.ProfilePhotoUserService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ChangeProfilePhotoRound implements ChangeProfilePhoto {
    @Override
    public void changeProfilePhoto(ProfilePhotoUserService profilePhotoUserService, ImageView imageView, User user) {
        String pathProfilePhoto = profilePhotoUserService.findOne(user.getId()).getPathProfilePhoto();
        try {
            FileInputStream fileInputStream = new FileInputStream(pathProfilePhoto);
            Image image = new Image(
                    fileInputStream, 400, 400, false, false
            );
            imageView.setImage(image);
            Circle clip = new Circle(120, 120, 100);
            imageView.setClip(clip);
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            WritableImage writableImage = imageView.snapshot(parameters, null);
            imageView.setClip(null);
            imageView.setImage(writableImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
