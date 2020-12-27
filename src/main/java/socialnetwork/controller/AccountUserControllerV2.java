package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import socialnetwork.config.Config;
import socialnetwork.domain.ContentPage;
import socialnetwork.domain.Page;
import javafx.scene.image.ImageView;
import socialnetwork.domain.ProfilePhotoUser;
import socialnetwork.domain.posts.PhotoPost;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountUserControllerV2 {
    private List<ImageView> listImageViewProfile;
    private Page userPage;
    private Stage accountUserStage;
    private Stage loginStage;
    private Pane currentPane;
    private ContentPage pagePhotoProfile = new ContentPage(4, 1);

    @FXML
    Label labelRealName;
    @FXML
    Label labelUsername;
    @FXML
    Pane statisticsPane;
    @FXML
    Pane profilePane;
    @FXML
    Pane feedPane;
    @FXML
    ImageView stImageView;
    @FXML
    ImageView ndImageView;
    @FXML
    ImageView rdImageView;
    @FXML
    ImageView fourthImageView;
    @FXML
    ImageView fifthImageView;
    @FXML
    ImageView sixthImageView;
    @FXML
    ImageView imageViewChangeProfilePhoto;
    @FXML
    ImageView stImageViewProfile;
    @FXML
    ImageView ndImageViewProfile;
    @FXML
    ImageView rdImageViewProfile;
    @FXML
    ImageView fourthImageViewProfile;

    @FXML
    public void initialize() {
        currentPane = feedPane;
        listImageViewProfile = new ArrayList<>(Arrays.asList(stImageViewProfile, ndImageViewProfile,
                rdImageViewProfile, fourthImageViewProfile));
        setImageView(stImageView, "C:\\Users\\dasco\\OneDrive\\Pictures\\ProfilePhotos\\larisuuuca.png");
        setImageView(ndImageView, "C:\\Users\\dasco\\OneDrive\\Pictures\\ProfilePhotos\\mariabun.jpg");
        setImageView(rdImageView, "C:\\Users\\dasco\\OneDrive\\Pictures\\ProfilePhotos\\carina.png");
        setImageView(fourthImageView, "C:\\Users\\dasco\\OneDrive\\Pictures\\ProfilePhotos\\patricea.png");
        setImageView(fifthImageView, "C:\\Users\\dasco\\OneDrive\\Pictures\\ProfilePhotos\\sergiu1.png");
        setImageView(sixthImageView, "C:\\Users\\dasco\\OneDrive\\Pictures\\ProfilePhotos\\elenabunbun.png");
    }

    /**
     * @param userPage Page, representing the Page of the logged in User.
     */
    public void setUserPage(Page userPage) {
        this.userPage = userPage;
        labelRealName.setText(userPage.getUser().getFullName());
        labelUsername.setText("@" + userPage.getUserCredentialsService().findOne(userPage.getUser().getId()).getUsername());
    }

    /**
     * @param accountUserStage Stage, representing the AccountUserStage
     */
    public void setAccountUserStage(Stage accountUserStage) {
        this.accountUserStage = accountUserStage;
    }

    /**
     * @param loginStage Stage, representing the LoginStage
     */
    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    /**
     * Method linked to the labelExitApplication's onMouseClicked event.
     * It closes the Application with a 0 status code.
     */
    public void eventExitApplication() {
        System.exit(0);
    }

    /**
     * Method linked to the labelLogout's onMouseClicked event
     * It log outs the user
     */
    public void eventLogout() {
        accountUserStage.close();
        loginStage.show();
    }

    /**
     * Method linked to the labelShowStatistics onMouseClicked event
     * It shows the Statistics Panel
     */
    public void eventShowStatistics() {
        currentPane.setVisible(false);
        currentPane = statisticsPane;
        currentPane.setVisible(true);
    }

    /**
     * Method linked to the labelShowFeed onMouseClicked event
     * It shows the Feed Panel
     */
    public void eventShowFeed() {
        currentPane.setVisible(false);
        currentPane = feedPane;
        currentPane.setVisible(true);
    }

    /**
     * Method linked to the labelShowProfile onMouseClicked event
     * It shows the Profile Panel
     */
    public void eventShowProfile() {
        currentPane.setVisible(false);
        currentPane = profilePane;
        currentPane.setVisible(true);
        pagePhotoProfile.setNumberPage(1); // Resets the Posts of the User to the first Page
        setImageViewProfile(userPage.getPhotoPostService().getListPhotoPosts(userPage.getUser().getId(), pagePhotoProfile));
    }

    /**
     * Method that sets an ImageView, making the Photo corresponding to the path round bordered
     * @param imageView Imageview, representing the ImageView to be set
     * @param pathToPhoto String, representing the path to the photo
     */
    private void setImageView(ImageView imageView, String pathToPhoto) {
        try {
            FileInputStream fileInputStream = new FileInputStream(pathToPhoto);
            Image image = new Image(fileInputStream, imageView.getFitWidth(), imageView.getFitHeight(), false, true);
            imageView.setImage(image);

            Rectangle clip = new Rectangle();
            clip.setWidth(imageView.getFitWidth());
            clip.setHeight(imageView.getFitHeight());

            clip.setArcHeight(45);
            clip.setArcWidth(45);
            clip.setStroke(Color.BLACK);
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

    /**
     * Method linked to the buttonChangeProfilePhoto's onMouseClicked event
     * It allows to the user to change the current Profile Photo
     */
    public void eventChangeProfilePhoto() {
        String selectedPhotoURL = getPhotoURL();
        if (selectedPhotoURL == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No profile photo was selected!");
            alert.show();
        } else {
            ProfilePhotoUser newProfilePhotoUser = new ProfilePhotoUser(selectedPhotoURL);
            newProfilePhotoUser.setId(userPage.getUser().getId());
            userPage.getProfilePhotoUserService().updateProfilePhotoUser(newProfilePhotoUser);
        }
    }

    /**
     * Method linked to the buttonAddPhoto's onMouseClicked event
     * It allows the user to add a new photo
     */
    public void eventAddPhoto() {
        String selectedPhotoURL = getPhotoURL();
        if (selectedPhotoURL == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No photo was selected!");
            alert.show();
        } else {
            PhotoPost photoPost = new PhotoPost(userPage.getUser().getId(), LocalDate.now(), selectedPhotoURL);
            if (userPage.getPhotoPostService().addPhotoPost(photoPost) == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "The photo was added successfully!");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "There was an error adding your photo. Please try again!");
                alert.show();
            }
        }
    }

    /**
     * Method linked to the labelGoNextProfile's onMouseClicked event
     * It shows the Posts of the User on the next Page
     */
    public void eventGoNextProfile() {
        pagePhotoProfile.nextPage();
        setImageViewProfile(userPage.getPhotoPostService().getListPhotoPosts(userPage.getUser().getId(), pagePhotoProfile));
    }

    /**
     * Method linked to the labelGoBackProfile's onMouseClicked event
     * It shows the Posts of the User on the previous Page
     */
    public void eventGoBackProfile() {
        pagePhotoProfile.previousPage();
        setImageViewProfile(userPage.getPhotoPostService().getListPhotoPosts(userPage.getUser().getId(), pagePhotoProfile));
        // TODO: ALERT GO BACK PAGE ALREADY 1
    }

    /**
     * Method that opens up a FileChooser Dialog in order to select a photo (.png, .jpg extensions)
     * @return null, if no photo is chosen
     *      non-null String, representing the URL of the selected photo
     */
    private String getPhotoURL() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\Users\\dasco\\OneDrive\\Pictures\\ProfilePhotos"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                new FileChooser.ExtensionFilter("JPG Files", "*.jpg"));
        fileChooser.setTitle("Choose Photo");
        File file = fileChooser.showOpenDialog(accountUserStage);
        if (file != null) {
            return file.toString();
        }
        return null;
    }

    /**
     * Method that sets the Image Views on the Profile Pane with some Photo Posts
     * @param listPhotoPosts List<PhotoPost>, representing the Photo Posts to be set to the Image Views
     *    its size can't be greater than 4 since a Page contains 4 Image Views
     */
    private void setImageViewProfile(List<PhotoPost> listPhotoPosts) {
        // First, reset the Image Views from the list
        listImageViewProfile.forEach(imageView -> imageView.setImage(null));
        for (int i = 0; i < listPhotoPosts.size(); i++) {
            setImageView(listImageViewProfile.get(i), listPhotoPosts.get(i).getPhotoURL());
        }
    }
}
