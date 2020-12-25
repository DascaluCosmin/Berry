package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import socialnetwork.domain.Page;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AccountUserControllerV2 {
    private Page userPage;
    private Stage accountUserStage;
    private Stage loginStage;
    private Pane currentPane;

    @FXML
    Label labelRealName;
    @FXML
    Label labelUsername;
    @FXML
    Pane statisticsPane;
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
    public void initialize() {
        currentPane = feedPane;
//        setImageView(stImageView, "C:\\Users\\dasco\\OneDrive\\Pictures\\ProfilePhotos\\larisuuuca.png");
        setImageView(stImageView, "C:\\Users\\dasco\\OneDrive\\Pictures\\ProfilePhotos\\tiberiu.png");
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
}
