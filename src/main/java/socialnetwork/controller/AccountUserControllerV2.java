package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.util.ResourceUtils;
import socialnetwork.domain.*;
import javafx.scene.image.ImageView;
import socialnetwork.domain.messages.Message;
import socialnetwork.domain.posts.PhotoPost;
import socialnetwork.domain.posts.TextPost;
import socialnetwork.utils.ChangeProfilePhotoRound;
import socialnetwork.utils.ValidatorDates;
import socialnetwork.utils.ViewClass;
import socialnetwork.utils.events.TextPostEvent;
import socialnetwork.utils.observer.Observer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class AccountUserControllerV2  implements Observer<TextPostEvent> {
    private List<ImageView> listImageViewProfile;
    private List<Button> listButtonProfile;
    private Page userPage;
    private Stage accountUserStage;
    private Stage loginStage;
    private Pane currentPane;
    private ContentPage pagePhotoPostProfile = new ContentPage(4, 1);
    private ContentPage pageTextPostProfile = new ContentPage(2, 1);

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


    // INITIALIZE
    @FXML
    public void initialize() {
        currentPane = feedPane;
        listImageViewProfile = new ArrayList<>(Arrays.asList(stImageViewProfile, ndImageViewProfile,
                rdImageViewProfile, fourthImageViewProfile));
        listButtonProfile = new ArrayList<>(Arrays.asList(buttonStPostProfile, buttonNdPostProfile));
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
        userPage.getTextPostService().addObserver(this);
        initializeSliderPane();
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

    // SLIDER PANE
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

    /**
     * Method that initializes the Slider Pane.
     * It sets the labelRealName, labelUserName and imageViewProfilePhoto
     */
    private void initializeSliderPane() {
        labelRealName.setText(userPage.getUserDTO().getFullName());
        labelUsername.setText("@" + userPage.getUserCredentialsService().findOne(userPage.getUser().getId()).getUsername());
        ChangeProfilePhotoRound changeProfilePhotoRound = new ChangeProfilePhotoRound();
        changeProfilePhotoRound.changeProfilePhoto(userPage.getProfilePhotoUserService(), imageViewProfilePhoto, userPage.getUser());
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
        setPieChart(pieChartMessages, userPage.getMessageService().getMessagesToUserYear(userPage.getUserDTO().getId(), 2020), 2020, "messages");
        setPieChart(pieChartFriendships, userPage.getFriendshipService().getNewFriendsUserYear(userPage.getUserDTO().getId(), 2020), 2020, "friendships");
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
        pagePhotoPostProfile.setNumberPage(1); // Resets the Posts of the User to the first Page
        setImageViewProfile(userPage.getPhotoPostService().getListPhotoPosts(userPage.getUser().getId(), pagePhotoPostProfile));
        setButtonProfile(userPage.getTextPostService().getListTextPosts(userPage.getUser().getId(), pageTextPostProfile));
    }

    // PROFILE PANE
    @FXML
    ImageView imageViewProfilePhoto;
    @FXML
    ImageView stImageViewProfile;
    @FXML
    ImageView ndImageViewProfile;
    @FXML
    ImageView rdImageViewProfile;
    @FXML
    ImageView fourthImageViewProfile;
    @FXML
    Button buttonStPostProfile;
    @FXML
    Button buttonNdPostProfile;

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
            ChangeProfilePhotoRound changeProfilePhotoRound = new ChangeProfilePhotoRound();
            changeProfilePhotoRound.changeProfilePhoto(userPage.getProfilePhotoUserService(), imageViewProfilePhoto, userPage.getUser());
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
                setImageViewProfile(userPage.getPhotoPostService().getListPhotoPosts(userPage.getUser().getId(), pagePhotoPostProfile));
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "There was an error adding your photo. Please try again!");
                alert.show();
            }
        }
    }

    /**
     * Method linked to the buttonWritePost's onMouseClicked event
     * It opens up a new Stage where the User can Write a Post
     */
    public void eventWritePost() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/writePostView.fxml"));
        Stage writePostStage = new Stage();
        ViewClass viewClass = new ViewClass();
        viewClass.initView(writePostStage, loader);
        WritePostController writePostController = loader.getController();
        writePostController.setUserPage(userPage);
        writePostController.setWritePostStage(writePostStage);
        writePostStage.initModality(Modality.APPLICATION_MODAL);
        writePostStage.show();
    }

    /**
     * Method linked to the labelGoNextProfile's onMouseClicked event
     * It shows the Posts of the User on the next Page
     */
    public void eventGoNextProfile() {
        pagePhotoPostProfile.nextPage();
        pageTextPostProfile.nextPage();
        setImageViewProfile(userPage.getPhotoPostService().getListPhotoPosts(userPage.getUser().getId(), pagePhotoPostProfile));
        setButtonProfile(userPage.getTextPostService().getListTextPosts(userPage.getUser().getId(), pageTextPostProfile));
    }

    /**
     * Method linked to the labelGoBackProfile's onMouseClicked event
     * It shows the Posts of the User on the previous Page
     */
    public void eventGoBackProfile() {
        if (pagePhotoPostProfile.getNumberPage() == 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You're already on the first Page!");
            alert.show();
        } else {
            pagePhotoPostProfile.previousPage();
            pageTextPostProfile.previousPage();
            setImageViewProfile(userPage.getPhotoPostService().getListPhotoPosts(userPage.getUser().getId(), pagePhotoPostProfile));
            setButtonProfile(userPage.getTextPostService().getListTextPosts(userPage.getUser().getId(), pageTextPostProfile));
        }
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

    /**
     * Method that sets the Buttons used for Text Posts on the Profile Pane with some Text
     * @param listTextPosts List<TextPosts>, representing the Text Posts whose Texts are to be set to the Buttons
     *    its size can't be greater than 2 since a Page contains 2 Text Posts
     */
    private void setButtonProfile(List<TextPost> listTextPosts) {
        // First, reset the Buttons
        listButtonProfile.forEach(button -> button.setVisible(false));
        for (int i = 0; i < listTextPosts.size(); i++) {
            listButtonProfile.get(i).setVisible(true);
            listButtonProfile.get(i).setText(listTextPosts.get(i).getText());
        }
    }

    /**
     * Overridden method that updates the Text Posts on the current Page
     * @param textPostEvent TextPostEvent, representing the Event corresponding to the changes in Text Post Data
     */
    @Override
    public void update(TextPostEvent textPostEvent) {
        setButtonProfile(userPage.getTextPostService().getListTextPosts(userPage.getUser().getId(), pageTextPostProfile));
    }

    // STATISTICS PANE
    @FXML
    DatePicker datePickerStartDate;
    @FXML
    DatePicker datePickerEndDate;
    @FXML
    TextField textFieldYear;
    @FXML
    PieChart pieChartMessages;
    @FXML
    PieChart pieChartFriendships;

    /**
     * Method that generates a Report about the User activity in a Date Period
     * @param typeReport TypeReport, representing the Type of the Report - PDF or HTML
     */
    private void generateReport(TypeReport typeReport) {
        LocalDate dateStart = datePickerStartDate.getValue();
        LocalDate dateEnd = datePickerEndDate.getValue();
        if (!ValidatorDates.validateDates(dateStart, dateEnd)) {
            return;
        }
        List<Message> messageList = userPage.getMessageService().getListAllMessagesToUserTimeInterval(
                userPage.getUserDTO().getId(), dateStart, dateEnd
        );
        List<Friendship> friendshipList = userPage.getFriendshipService().getListAllFriendshipsUserTimeInterval(
                userPage.getUserDTO().getId(), dateStart, dateEnd
        );
        if (messageList.size() == 0) { // No messages in that Date Period
            messageList.add(new Message(new User("No messages", "No messages"), null, "No messages", LocalDateTime.now()));
        }
        try {
            File file = ResourceUtils.getFile("classpath:report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(messageList);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("UserReport", userPage.getUserDTO().getFullName() + "'s Report");
            parameters.put("StatisticsFriendsMessages", "You befriended " + friendshipList.size() +
                    " people and received " + messageList.size() + " messages");
            parameters.put("DatePeriodReport", "Date Period: " + dateStart + " - " + dateEnd);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            String pathToGenerateTo = "C:\\Users\\dasco\\" + userPage.getUserDTO().getFullName().replace(' ', '_');
            if (typeReport == TypeReport.PDF) {
                JasperExportManager.exportReportToPdfFile(jasperPrint, pathToGenerateTo + "Report.pdf");
            } else if (typeReport == TypeReport.HTML) {
                JasperExportManager.exportReportToHtmlFile(jasperPrint, pathToGenerateTo + "Report.html");
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The report has been generated successfully!");
            alert.show();
        } catch (FileNotFoundException | JRException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method linked to the buttonGeneratePDFReport's onMouseClicked event
     * It generates a new PDF Report about the User's activity
     */
    public void generatePDFReport() {
        generateReport(TypeReport.PDF);
    }

    /**
     * Method linked to the buttonGenerateHTMLReport's onMouseClicked event
     * It generates a new HTML Report about the User's activity
     */
    public void generateHTMLReport() {
        generateReport(TypeReport.HTML);
    }

    /**
     * Method linked to the textFieldYear's onKeyTyped event
     * It displays the two Pie Charts - received Messages and new Friendships for a typed Year
     */
    public void eventShowGraphs() {
        if (textFieldYear.getText().length() >= 4) {
            try {
                Integer year = Integer.parseInt(textFieldYear.getText());
                setPieChart(pieChartMessages, userPage.getMessageService().getMessagesToUserYear(userPage.getUserDTO().getId(), year), year, "messages");
                setPieChart(pieChartFriendships, userPage.getFriendshipService().getNewFriendsUserYear(userPage.getUserDTO().getId(), year), year, "friendships");
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Introduce a valid year!");
                alert.show();
            }
        } else {
            pieChartMessages.setTitle("");
            pieChartMessages.setData(FXCollections.emptyObservableList());
            pieChartFriendships.setTitle("");
            pieChartFriendships.setData(FXCollections.emptyObservableList());
        }
    }

    /**
     * @param pieChart PieChart, representing the Pie Chart to be set
     * @param mapData Map<String, Integer>, representing a Map mapping a String (Month in String Format) to a Quantity
     * @param year Integer, representing the Year the observations were made
     * @param entitiesString String, representing the type of entities observed
     */
    private void setPieChart(PieChart pieChart, Map<String, Integer> mapData, Integer year, String entitiesString) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        mapData.entrySet().removeIf(entry -> entry.getValue() == 0); // Remove the months with 0 entities
        mapData.keySet().forEach(key -> {
            int number = mapData.get(key);
            String text = key + ": " + number + " " + entitiesString;
            pieChartData.add(new PieChart.Data(text, number));
        });
        if (mapData.keySet().size() == 0) { // Don't show the pie chart if there is no data for that year
            pieChart.setTitle("");
            pieChart.setData(FXCollections.emptyObservableList());
        } else {
            pieChart.setData(pieChartData);
            pieChart.setClockwise(true);
            pieChart.setStartAngle(180);
            pieChart.setLabelLineLength(15);
            pieChart.setTitle(entitiesString.substring(0, 1).toUpperCase() + entitiesString.substring(1) + " in " + year);
        }
    }

}
