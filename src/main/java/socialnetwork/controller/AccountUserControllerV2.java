package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
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
    private Integer numberFriends;
    private Integer numberPosts;
    private Integer numberPhotos;
    private List<Rectangle> listRectanglesProfile;
    private List<Rectangle> listRectanglesProfileFriend;
    private List<Button> listButtonProfile;
    private Page userPage;
    private Stage accountUserStage;
    private Stage loginStage;
    private Pane currentPane;
    private ContentPage pagePhotoPostProfile = new ContentPage(6, 1);
    private ContentPage pagePhotoPostProfileFriend = new ContentPage(6, 1);
    private ContentPage pageTextPostProfile = new ContentPage(6, 1);

    // INITIALIZE
    @FXML
    public void initialize() {
        buttonRemoveFriend.setVisible(false); // TODO: at production, set visibility to false in Scene Builder
        labelFriendRealName.setVisible(false);
        currentPane = feedPane;
        listButtonProfile = new ArrayList<>(Arrays.asList(
                buttonStPostProfile, buttonNdPostProfile, buttonRdPostProfile,
                button4thPostProfile, button5thPostProfile, button6thPostProfile));
        listRectanglesProfile = new ArrayList<>(Arrays.asList(
                rectangleStPhoto, rectangleNdPhoto, rectangleRdPhoto,
                rectangle4thPhoto, rectangle5thPhoto, rectangle6thPhoto)
        );
        listRectanglesProfileFriend = new ArrayList<>(Arrays.asList(
                rectangleStPhotoFriendsFeed, rectangleNdPhotoFriendsFeed, rectangleRdPhotoFriendsFeed,
                rectangle4thPhotoFriendsFeed, rectangle5thPhotoFriendsFeed, rectangle6thPhotoFriendsFeed
        ));
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

    // FEED PANE
    @FXML
    TextField textFieldSearchFriend;
    @FXML
    Label labelFriendRealName;
    @FXML
    Button buttonRemoveFriend;
    @FXML
    Pane paneFriendsProfile;
    @FXML
    Pane paneFriendsFeed;
    @FXML
    Rectangle rectangleStPhotoFriendsFeed;
    @FXML
    Rectangle rectangleNdPhotoFriendsFeed;
    @FXML
    Rectangle rectangleRdPhotoFriendsFeed;
    @FXML
    Rectangle rectangle4thPhotoFriendsFeed;
    @FXML
    Rectangle rectangle5thPhotoFriendsFeed;
    @FXML
    Rectangle rectangle6thPhotoFriendsFeed;

    /**
     * Method linked to the textFieldSearchFriend's onKeyTyped event
     * It allows the User to search a Friend's Profile
     */
    public void eventSearchFriend() {
        String usernameUser = textFieldSearchFriend.getText();
        if (usernameUser.length() >= 4) {
            UserCredentials userCredentials = userPage.getUserCredentialsService().findOne(usernameUser);
            if (userCredentials != null) { // The User exists
                User searchedUser = userPage.getUserService().getUser(userCredentials.getId());
                if (userPage.getFriendshipService().findOne(userPage.getUser().getId(), searchedUser.getId()) != null) {
                    // The two Users are friends
                    paneFriendsProfile.setVisible(true);
                    paneFriendsFeed.setVisible(false);
                    buttonRemoveFriend.setVisible(true);
                    labelFriendRealName.setVisible(true);
                    labelFriendRealName.setText(searchedUser.getFullName());
                    setRectanglesPhoto(userPage.getPhotoPostService().getListPhotoPosts(searchedUser.getId(), pagePhotoPostProfileFriend), listRectanglesProfileFriend);
                }
            }
        } else if (usernameUser.length() == 0) {
            buttonRemoveFriend.setVisible(false);
            labelFriendRealName.setVisible(false);
            paneFriendsProfile.setVisible(false);
            paneFriendsFeed.setVisible(true);
        }
    }

    // SLIDER PANE
    @FXML
    Label labelRealName;
    @FXML
    Label labelUsername;
    @FXML
    Label labelNumberFriends;
    @FXML
    Label labelNumberPosts;
    @FXML
    Label labelNumberPhotos;
    @FXML
    Pane statisticsPane;
    @FXML
    Pane profilePane;
    @FXML
    Pane feedPane;
    @FXML
    Circle circleProfilePhoto;

    /**
     * Method that initializes the Slider Pane.
     * It sets the labelRealName, labelUserName and imageViewProfilePhoto
     */
    private void initializeSliderPane() {
        numberFriends = userPage.getFriendshipService().getNumberFriends(userPage.getUser().getId());
        numberPosts = userPage.getTextPostService().getNumberTextPosts(userPage.getUser().getId());
        numberPhotos = userPage.getPhotoPostService().getNumberPhotoPosts(userPage.getUser().getId());

        labelRealName.setText(userPage.getUserDTO().getFullName());
        labelUsername.setText("@" + userPage.getUserCredentialsService().findOne(userPage.getUser().getId()).getUsername());
        labelNumberFriends.setText(numberFriends + " Friends");
        labelNumberPosts.setText(numberPosts + " Posts");
        labelNumberPhotos.setText(numberPhotos + " Photos");
        setImage(circleProfilePhoto, userPage.getProfilePhotoUserService().findOne(userPage.getUser().getId()).getPathProfilePhoto());
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
        pageTextPostProfile.setToFirstPage();
        pagePhotoPostProfile.setToFirstPage();
        setRectanglesPhoto(userPage.getPhotoPostService().getListPhotoPosts(userPage.getUser().getId(), pagePhotoPostProfile), listRectanglesProfile);
        setButtonProfile(userPage.getTextPostService().getListTextPosts(userPage.getUser().getId(), pageTextPostProfile));
    }

    // PROFILE PANE
    @FXML
    Button buttonStPostProfile;
    @FXML
    Button buttonNdPostProfile;
    @FXML
    Button buttonRdPostProfile;
    @FXML
    Button button4thPostProfile;
    @FXML
    Button button5thPostProfile;
    @FXML
    Button button6thPostProfile;
    @FXML
    Rectangle rectangleStPhoto;
    @FXML
    Rectangle rectangleNdPhoto;
    @FXML
    Rectangle rectangleRdPhoto;
    @FXML
    Rectangle rectangle4thPhoto;
    @FXML
    Rectangle rectangle5thPhoto;
    @FXML
    Rectangle rectangle6thPhoto;
    @FXML
    Pane panePhotoPostsProfile;
    @FXML
    Pane paneTextPostsProfile;

    /**
     * Method that sets a Shape, filling it with an Image
     * @param shape Shape, representing the Shape to be set
     * @param pathToPhoto String, representing the path to the Image
     */
    private void setImage(Shape shape, String pathToPhoto) {
        try {
            FileInputStream fileInputStream = new FileInputStream(pathToPhoto);
            Image image = new Image(fileInputStream, 1000, 1000, false, true);
            shape.setFill(new ImagePattern(image));
            if (shape instanceof Rectangle) {
                ((Rectangle) shape).setArcHeight(45);
                ((Rectangle) shape).setArcWidth(45);
            }
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
            setImage(circleProfilePhoto, userPage.getProfilePhotoUserService().findOne(userPage.getUser().getId()).getPathProfilePhoto());
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
                setRectanglesPhoto(userPage.getPhotoPostService().getListPhotoPosts(userPage.getUser().getId(), pagePhotoPostProfile), listRectanglesProfile);
                numberPhotos++;
                labelNumberPhotos.setText(numberPhotos + " Photos");
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
        writePostController.setNumberPosts(numberPosts);
        writePostController.setLabelNumberPosts(labelNumberPosts);
        writePostStage.initModality(Modality.APPLICATION_MODAL);
        writePostStage.setOnHiding(event -> {
            // When closing (hiding) the Write Post Stage, we want the number of Posts to be updated
            numberPosts = userPage.getTextPostService().getNumberTextPosts(userPage.getUser().getId());
        });
        writePostStage.show();
    }

    /**
     * Method linked to the labelGoNextProfile's onMouseClicked event
     * It shows the Posts of the User on the next Page
     */
    public void eventGoNextProfile() {
        pagePhotoPostProfile.nextPage();
        pageTextPostProfile.nextPage();
        setRectanglesPhoto(userPage.getPhotoPostService().getListPhotoPosts(userPage.getUser().getId(), pagePhotoPostProfile), listRectanglesProfile);
        setButtonProfile(userPage.getTextPostService().getListTextPosts(userPage.getUser().getId(), pageTextPostProfile));
    }

    /**
     * Method linked to the labelGoBackProfile's onMouseClicked event
     * It shows the Posts of the User on the previous Page
     */
    public void eventGoBackProfile() {
        if (pagePhotoPostProfile.getNumberPage() == 1 || pageTextPostProfile.getNumberPage() == 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You're already on the first Page!");
            alert.show();
        } else {
            pagePhotoPostProfile.previousPage();
            pageTextPostProfile.previousPage();
            setRectanglesPhoto(userPage.getPhotoPostService().getListPhotoPosts(userPage.getUser().getId(), pagePhotoPostProfile), listRectanglesProfile);
            setButtonProfile(userPage.getTextPostService().getListTextPosts(userPage.getUser().getId(), pageTextPostProfile));
        }
    }

    /**
     * Method linked to the buttonsShowProfilePhoto's onMouseClicked event
     * It shows the Photo Posts of the User
     */
    public void eventShowPhotoPaneProfile() {
        pagePhotoPostProfile.setToFirstPage();
        setRectanglesPhoto(userPage.getPhotoPostService().getListPhotoPosts(userPage.getUser().getId(), pagePhotoPostProfile), listRectanglesProfile);
        panePhotoPostsProfile.setVisible(true);
        paneTextPostsProfile.setVisible(false);
    }

    /**
     * Method linked to the buttonsShowTextPhoto's onMouseClicked event
     * It shows the Text Posts of the User
     */
    public void eventShowTextPaneProfile() {
        pageTextPostProfile.setToFirstPage();
        setButtonProfile(userPage.getTextPostService().getListTextPosts(userPage.getUser().getId(), pageTextPostProfile));
        paneTextPostsProfile.setVisible(true);
        panePhotoPostsProfile.setVisible(false);
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
     * Method that sets some Rectangles corresponding to the Photo Posts, filling them with the Photo Posts
     * @param listPhotoPosts List<PhotoPost>, representing the Photo Posts to be filled to the Rectangles
     *    its size can't be greater than 6 since a Page contains only 6 Photos
     * @param listRectangles List<Rectangles>, representing the Rectangles to be filled
     */
    private void setRectanglesPhoto(List<PhotoPost> listPhotoPosts, List<Rectangle> listRectangles) {
        listRectangles.forEach(shape -> shape.setFill(null));
        for (int i = 0; i < listPhotoPosts.size(); i++) {
            setImage(listRectangles.get(i), listPhotoPosts.get(i).getPhotoURL());
        }
    }

    /**
     * Method that sets the Buttons used for Text Posts on the Profile Pane with some Text
     * @param listTextPosts List<TextPosts>, representing the Text Posts whose Texts are to be set to the Buttons
     *    its size can't be greater than 6 since a Page contains only 6 Text Posts
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
