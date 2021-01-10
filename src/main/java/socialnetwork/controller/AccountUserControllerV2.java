package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.util.ResourceUtils;
import socialnetwork.domain.*;
import socialnetwork.domain.events.Event;
import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.domain.messages.Message;
import socialnetwork.domain.messages.ReplyMessage;
import socialnetwork.domain.posts.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.database.friendshipRequests.TypeFriendshipRequest;
import socialnetwork.repository.database.messages.SenderType;
import socialnetwork.utils.Constants;
import socialnetwork.utils.DateConverter;
import socialnetwork.utils.ValidatorDates;
import socialnetwork.utils.ViewClass;
import socialnetwork.utils.events.TextPostEvent;
import socialnetwork.utils.observer.Observer;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class AccountUserControllerV2  implements Observer<TextPostEvent> {
    private Integer numberFriends;
    private Integer numberPosts;
    private Integer numberPhotos;
    private List<Rectangle> listRectanglesProfile;
    private List<Rectangle> listRectanglesProfileFriend;
    private List<Rectangle> listRectanglesFeedFriends;
    private List<Button> listButtonsProfile;
    private List<Button> listButtonsProfileFriend;
    private List<Button> listButtonsFeedFriends;
    private List<Button> listButtonsBerryFriend;
    private List<Button> listButtonsBerryFriendProfile;
    private List<Label> listLabelsFeedFriends;
    private List<Group> listGroupsNbBerriesPostFriend;
    private List<Group> listGroupsBerryPostFriend;
    private List<Group> listGroupsNbBerriesPostFriendProfile;
    private List<Group> listGroupsBerryPostFriendProfile;
    private Page userPage;
    private Stage accountUserStage;
    private Stage loginStage;
    private Pane currentPane;
    private Pane currentDirectPane;
    private Label currentSliderLabel;
    private User friendUser;
    private final ContentPage pagePhotoPostProfile = new ContentPage(6, 1);
    private final ContentPage pagePhotoPostProfileFriend = new ContentPage(3, 1);
    private final ContentPage pageTextPostProfile = new ContentPage(6, 1);
    private final ContentPage pageTextPostProfileFriend = new ContentPage(3, 1);
    private final ContentPage pageExploreUsers = new ContentPage(5, 1);

    // INITIALIZE
    /**
     * Method that initializes some of the components of the View
     */
    @FXML
    public void initialize() {
        currentPane = feedPane;
        currentSliderLabel = labelShowFeedDummy;
        currentSliderLabel.setVisible(true);
        listButtonsProfile = new ArrayList<>(Arrays.asList(
                buttonStPostProfile, buttonNdPostProfile, buttonRdPostProfile,
                button4thPostProfile, button5thPostProfile, button6thPostProfile));
        listButtonsProfileFriend = new ArrayList<>(Arrays.asList(
                buttonStPostProfileFriend, buttonNdPostProfileFriend, buttonRdPostProfileFriend
        ));
        listButtonsFeedFriends = new ArrayList<>(Arrays.asList(
                buttonStTextPostFriends, buttonNdTextPostFriends, buttonRdTextPostFriends,
                button4thTextPostFriends, button5thTextPostFriends, button6thTextPostFriends
        ));
        listButtonsBerryFriend = new ArrayList<>(Arrays.asList(
                buttonBerryStPostFriend, buttonBerryNdPostFriend, buttonBerryRdPostFriend,
                buttonBerry4thPostFriend, buttonBerry5thPostFriend, buttonBerry6thPostFriend
        ));
        listButtonsBerryFriendProfile = new ArrayList<>(Arrays.asList(
                buttonBerryStPostFriendProfile, buttonBerryNdPostFriendProfile, buttonBerryRdPostFriendProfile
        ));
        listGroupsNbBerriesPostFriend = new ArrayList<>(Arrays.asList(
                groupNbBerriesStPostFriend, groupNbBerriesNdPostFriend, groupNbBerriesRdPostFriend,
                groupNbBerries4thPostFriend, groupNbBerries5thPostFriend, groupNbBerries6thPostFriend
        ));
        listGroupsBerryPostFriend = new ArrayList<>(Arrays.asList(
                groupBerryStPostFriend, groupBerryNdPostFriend, groupBerryRdPostFriend,
                groupBerry4thPostFriend, groupBerry5thPostFriend, groupBerry6thPostFriend
        ));
        listGroupsNbBerriesPostFriendProfile = new ArrayList<>(Arrays.asList(
                groupNbBerriesStPostFriendProfile, groupNbBerriesNdPostFriendProfile, groupNbBerriesRdPostFriendProfile
        ));
        listGroupsBerryPostFriendProfile = new ArrayList<>(Arrays.asList(
                groupBerryStPostFriendProfile, groupBerryNdPostFriendProfile, groupBerryRdPostFriendProfile
        ));
        listRectanglesProfile = new ArrayList<>(Arrays.asList(
                rectangleStPhoto, rectangleNdPhoto, rectangleRdPhoto,
                rectangle4thPhoto, rectangle5thPhoto, rectangle6thPhoto)
        );
        listRectanglesProfileFriend = new ArrayList<>(Arrays.asList(
                rectangleStPhotoFriendFeed, rectangleNdPhotoFriendFeed, rectangleRdPhotoFriendFeed
        ));
        listRectanglesFeedFriends = new ArrayList<>(Arrays.asList(
                rectangleStPhotoPostFriends, rectangleNdPhotoPostFriends, rectangleRdPhotoPostFriends,
                rectangle4thPhotoPostFriends, rectangle5thPhotoPostFriends, rectangle6thPhotoPostFriends
        ));
        listCirclesPhotoFriends = new ArrayList<>(Arrays.asList(
                circleStPhotoFriendChat, circleNdPhotoFriendChat, circleRdPhotoFriendChat,
                circle4thPhotoFriendChat, circle5thPhotoFriendChat, circle6thPhotoFriendChat,
                circle7thPhotoFriendChat, circle8thPhotoFriendChat
        ));
        listGroupRequestsReceived = new ArrayList<>(Arrays.asList(
                groupRequestReceivedSt, groupRequestReceivedNd, groupRequestReceivedRd,
                groupRequestReceived4th, groupRequestReceived5th
        ));
        listLabelsFeedFriends = new ArrayList<>(Arrays.asList(
                labelUserNameStPost, labelUserNameNdPost, labelUserNameRdPost,
                labelUserName4thPost, labelUserName5thPost, labelUserName6thPost
        ));
        listGroupRequestsSent = new ArrayList<>(Arrays.asList(
                groupRequestSentSt, groupRequestSentNd, groupRequestSentRd,
                groupRequestSent4th, groupRequestSent5th
        ));
        listGroupsNonFriends = new ArrayList<>(Arrays.asList(
                groupNonFriendSt, groupNonFriendNd, groupNonFriendRd,
                groupNonFriend4th, groupNonFriend5th
        ));
        listGroupMessagesUser = new ArrayList<>(Arrays.asList(
                groupStMessageUser, groupNdMessageUser, groupRdMessageUser,
                group4thMessageUser, group5thMessageUser, group6thMessageUser,
                groupHiddenMessageUser
        ));
        listGroupMessagesFriend = new ArrayList<>(Arrays.asList(
                groupStMessageFriend, groupNdMessageFriend, groupRdMessageFriend,
                group4thMessageFriend, group5thMessageFriend, group6thMessageFriend,
                groupHiddenMessageFriend
        ));
        listGroupMessagesInbox = new ArrayList<>(Arrays.asList(
                groupStMessageInbox, groupNdMessageInbox, groupRdMessageInbox, group4thMessageInbox,
                group5thMessageInbox, group6thMessageInbox, group7thMessageInbox, group8thMessageInbox,
                group9thMessageInbox, group10thMessageInbox, group11thMessageInbox
        ));
        initializeEventHandlers();
    }

    /**
     * Method that initializes some of the Event Handlers - for textFieldChat onKeyTyped & paneChat onScrollEvent
     */
    private void initializeEventHandlers() {
        textFieldChat.setOnKeyTyped(event -> {
            if (textFieldChat.getText().length() > MAX_CHARACTERS_MESSAGE * 2) {
                textFieldChat.setText(textFieldChat.getText(0, MAX_CHARACTERS_MESSAGE * 2));
                textFieldChat.positionCaret(MAX_CHARACTERS_MESSAGE * 2);
            }
        });
        textFieldChat.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String textMessage = textFieldChat.getText();
                if (!textMessage.matches("[ ]*")) {
                    ReplyMessage chatMessage = new ReplyMessage(
                            userPage.getUser(), Collections.singletonList(currentFriendChat),
                            textFieldChat.getText().trim(), LocalDateTime.now(), null);
                    userPage.getReplyMessageService().addMessage(chatMessage);
                    pageFriendConversation.setToFirstPage();
                    initializeConversation();
                    textFieldChat.clear();
                }
            }
        });
        paneChat.setOnScroll((ScrollEvent e) -> {
            if (e.getDeltaY() < 0) {
                if (pageFriendConversation.getNumberPage() > 1) {
                    pageFriendConversation.previousPage();
                    initializeConversation();
                }
            } else {
                pageFriendConversation.nextPage();
                initializeConversation();
            }
        });
        for(int i = 0; i < listGroupMessagesInbox.size(); i++) {
            Group currentGroup = listGroupMessagesInbox.get(i);
            int finalI = i;
            currentGroup.setOnMouseClicked(event -> eventInboxMessageClickedOn(listCurrentMessagesInbox.get(finalI)));
        }
        for (int i = 0; i < listButtonsBerryFriend.size(); i++) {
            int finalI = i;
            Button currentButton = listButtonsBerryFriend.get(i);
            Group currentGroup = listGroupsNbBerriesPostFriend.get(i);
            currentButton.setOnMouseClicked(event -> likeUnlikeSelectedPost(
                            listCurrentPostsFeedFriends.get(finalI), currentButton, currentGroup));
        }
        for (int i = 0; i < listGroupsBerryPostFriendProfile.size(); i++) {
            int finalI = i;
            Button currentButton = listButtonsBerryFriendProfile.get(i);
            Group currentGroup = listGroupsNbBerriesPostFriendProfile.get(i);
            currentButton.setOnMouseClicked(event -> likeUnlikeSelectedPost(
                    listCurrentPostsProfileFriend.get(finalI), currentButton, currentGroup
            ));
        }
    }

    /**
     * @param userPage Page, representing the Page of the logged in User
     */
    public void setUserPage(Page userPage) {
        this.userPage = userPage;
        userPage.getTextPostService().addObserver(this);
        initializeFeedPane();
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
    private final Integer DAY_GAP = 250;

    @FXML
    Label labelNbNotifications;
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
    Label labelLogout;
    @FXML
    Label labelShowProfileDummy;
    @FXML
    Label labelShowFeedDummy;
    @FXML
    Label labelShowEventsDummy;
    @FXML
    Label labelShowExploreDummy;
    @FXML
    Label labelShowDirectDummy;
    @FXML
    Label labelShowStatisticsDummy;
    @FXML
    Pane statisticsPane;
    @FXML
    Pane profilePane;
    @FXML
    Pane feedPane;
    @FXML
    Pane explorePane;
    @FXML
    Pane eventsPane;
    @FXML
    Pane notificationsPane;
    @FXML
    Circle circleProfilePhoto;

    /**
     * Method that initializes the Slider Pane
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
        initializeNumberNotifications();
        setImage(circleProfilePhoto, userPage.getProfilePhotoUserService().findOne(userPage.getUser().getId()).getPathProfilePhoto());
    }

    /**
     * Method that initializes the Feed Pane
     * It clears the textFieldSearchFriend, shows paneFriendsFeed and hides paneFriendsProfile
     */
    private void initializeFeedPane() {
        pagePhotoPostsFeedFriends.setToFirstPage();
        textFieldSearchFriend.clear();
        panePhotoPostsFriends.setVisible(true);
        paneTextPostsFriends.setVisible(false);
        paneFriendsFeed.setVisible(true);
        paneFriendsProfile.setVisible(false);
        labelGoBackPhotoPostsFriends.setVisible(true);
        labelGoNextPhotoPostsFriends.setVisible(true);
        labelGoBackTextPostsFriends.setVisible(false);
        labelGoNextTextPostsFriends.setVisible(false);
        initializePhotoPostsFriends();
    }

    /**
     * Method that initializes the Explore Pane
     * It shows paneExploreReceivedRequests and hides paneExploreSentRequests
     */
    private void initializeExplorePane() {
        paneExploreReceivedRequests.setVisible(true);
        paneExploreSentRequests.setVisible(false);
        labelFriendshipRequests.setText("Received Requests");
    }

    /**
     * Method that initializes the Explore Pane
     * It shows panePhotoPostsProfile and hides paneTextPostsProfile
     */
    private void initializeProfilePane() {
        panePhotoPostsProfile.setVisible(true);
        paneTextPostsProfile.setVisible(false);
    }

    /**
     * Method that initializes the Events Pane
     * It shows the components corresponding to the Discover Pane, hiding the components corresponding to the Your Events Pane
     */
    private void initializeEventsPane() {
        buttonParticipate.setVisible(true);
        buttonSubscribe.setVisible(false);
        labelNoEvents.setVisible(false);
        labelGoBackEventDiscover.setVisible(true);
        labelGoNextEventDiscover.setVisible(true);
        labelGoBackEventParticipate.setVisible(false);
        labelGoNextEventParticipate.setVisible(false);
    }

    /**
     * Method that initializes the number of Notifications
     */
    private void initializeNumberNotifications() {
        Long numberOfNotifications = userPage.getEventsService().getNumberOfNotificationsEvents(
                userPage.getUser().getId(), DAY_GAP);
        if (numberOfNotifications == 0) {
            labelNbNotifications.setVisible(false);
        } else {
            labelNbNotifications.setVisible(true);
            labelNbNotifications.setText(numberOfNotifications.toString());
        }
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
        currentSliderLabel.setVisible(false);
        currentSliderLabel = labelShowStatisticsDummy;
        currentSliderLabel.setVisible(true);
        int CurrentYear = LocalDate.now().getYear();
        setPieChart(pieChartMessages, userPage.getMessageService().getMessagesToUserYear(userPage.getUserDTO().getId(), CurrentYear), CurrentYear, "messages");
        setPieChart(pieChartFriendships, userPage.getFriendshipService().getNewFriendsUserYear(userPage.getUserDTO().getId(), CurrentYear), CurrentYear, "friendships");
    }

    /**
     * Method linked to the labelShowFeed onMouseClicked event
     * It shows the Feed Panel
     */
    public void eventShowFeed() {
        currentPane.setVisible(false);
        currentPane = feedPane;
        currentPane.setVisible(true);
        currentSliderLabel.setVisible(false);
        currentSliderLabel = labelShowFeedDummy;
        currentSliderLabel.setVisible(true);
        labelGoBackPhotoPostsFriends.setVisible(true);
        labelGoNextPhotoPostsFriends.setVisible(true);
        labelGoBackTextPostsFriends.setVisible(false);
        labelGoNextTextPostsFriends.setVisible(false);
        buttonRemoveFriend.setVisible(false);
        pagePhotoPostsFeedFriends.setToFirstPage();
        pageTextPostsFeedFriends.setToFirstPage();
        initializeFeedPane();
    }

    /**
     * Method linked to the labelShowProfile onMouseClicked event
     * It shows the Profile Panel
     */
    public void eventShowProfile() {
        initializeProfilePane();
        currentPane.setVisible(false);
        currentPane = profilePane;
        currentPane.setVisible(true);
        currentSliderLabel.setVisible(false);
        currentSliderLabel = labelShowProfileDummy;
        currentSliderLabel.setVisible(true);
        pageTextPostProfile.setToFirstPage();
        pagePhotoPostProfile.setToFirstPage();
        List<PhotoPost> listPhotoPostsProfile = userPage.getPhotoPostService().getListPhotoPosts(userPage.getUser().getId(), pagePhotoPostProfile);
        labelGoBackProfile.setVisible(pagePhotoPostProfile.getNumberPage() > 1);
        labelGoNextProfile.setVisible(pagePhotoPostProfile.getSizePage() == listPhotoPostsProfile.size());
        setRectanglesPhoto(listPhotoPostsProfile, listRectanglesProfile);
    }

    /**
     * Method linked to the labelShowExplore onMouseClicked event
     * It shows the Explore Panel
     */
    public void eventShowExplore() {
        initializeExplorePane();
        currentPane.setVisible(false);
        currentPane = explorePane;
        currentPane.setVisible(true);
        currentSliderLabel.setVisible(false);
        currentSliderLabel = labelShowExploreDummy;
        currentSliderLabel.setVisible(true);
        pageExploreUsers.setToFirstPage();
        pageFriendRequestsReceived.setToFirstPage();
        pageFriendRequestsSent.setToFirstPage();
        initializeReceivedRequests();
        initializeNonFriends();
    }

    /**
     * Method linked to the labelShowEvents onMouseClicked event
     * It shows the Events Panel
     */
    public void eventShowEvents() {
        initializeEventsPane();
        currentPane.setVisible(false);
        currentPane = eventsPane;
        currentPane.setVisible(true);
        if (currentEventPane != null) {
            currentEventPane.setVisible(false);
        }
        currentEventPane = paneEventsParticipate;
        currentEventPane.setVisible(true);
        currentSliderLabel.setVisible(false);
        currentSliderLabel = labelShowEventsDummy;
        currentSliderLabel.setVisible(true);
        pageEventsDiscover.setToFirstPage();
        pageEventsParticipate.setToFirstPage();
        initializeEventToBeDiscovered();
    }

    /**
     * Method linked to the labelShowDirect onMouseClicked event
     * It shows the Direct Pane
     */
    public void eventShowDirect() {
        currentPane.setVisible(false);
        currentPane = directPane;
        currentPane.setVisible(true);
        currentSliderLabel.setVisible(false);
        currentSliderLabel = labelShowDirectDummy;
        currentSliderLabel.setVisible(true);
        initializePaneChat();
    }

    /**
     * Method linked to the labelNotifications' onMouseClicked
     * It shows the Notifications Pane
     */
    public void eventShowNotifications() {
        if (userPage.getEventsService().getNumberOfNotificationsEvents(userPage.getUser().getId(), DAY_GAP) > 0) {
            currentPane.setVisible(false);
            currentPane = notificationsPane;
            currentPane.setVisible(true);
            currentSliderLabel.setVisible(false);
            pageEventsNotifications.setToFirstPage();
            labelHeaderEventNotifications.setText(HEADER_EVENT_NOTIFICATIONS_ST_PART + DAY_GAP + HEADER_EVENT_NOTIFICATIONS_ND_PART);
            initializeEventNotification();
        }
    }

    // NOTIFICATIONS PANE
    private final ContentPage pageEventsNotifications = new ContentPage(1, 1);
    private final String HEADER_EVENT_NOTIFICATIONS_ST_PART = "Events taking place in less than ";
    private final String HEADER_EVENT_NOTIFICATIONS_ND_PART = " Days";
    @FXML
    Label labelHeaderEventNotifications;
    @FXML
    Label labelNoEventsNotifications;
    @FXML
    Label labelGoBackEventNotifications;
    @FXML
    Label labelGoNextEventNotifications;
    @FXML
    Button buttonDontParticipateNotifications;
    @FXML
    Button buttonParticipateNotifications;
    @FXML
    Group groupEventDetailsNotifications;
    @FXML
    Rectangle rectanglePhotoEventNotifications;
    @FXML
    Pane paneEventsNotifications;

    /**
     * Method that initializes the Notification Event
     */
    private void initializeEventNotification() {
        List<Event> listEventsNotificationsOnPage = userPage.getEventsService().getListEventsToNotify(
                userPage.getUser().getId(), pageEventsNotifications, DAY_GAP
        );
        labelGoBackEventNotifications.setVisible(pageEventsNotifications.getNumberPage() > 1);
        labelGoNextEventNotifications.setVisible(pageEventsNotifications.getSizePage() == listEventsNotificationsOnPage.size());
        if (!listEventsNotificationsOnPage.isEmpty()) {
            paneEventsNotifications.setVisible(true);
            labelNoEventsNotifications.setVisible(false);
            initializeEvent(listEventsNotificationsOnPage.get(0), groupEventDetailsNotifications, rectanglePhotoEventNotifications);
        } else {
            labelNoEventsNotifications.setVisible(true);
            paneEventsNotifications.setVisible(false);
        }
    }

    /**
     * Method linked to the labelGoNextEventNotifications' onMouseClicked
     * It shows the Notification Event on the next Page
     */
    public void eventGoNextEventNotifications() {
        pageEventsNotifications.nextPage();
        initializeEventNotification();
    }

    /**
     * Method linked to the labelGoBackEventNotifications' onMouseClicked
     * It shows the Notification Event on the previous Page
     */
    public void eventGoBackEventNotifications() {
        if (pageEventsNotifications.getNumberPage() == 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You're already on the first Page!");
            alert.show();
        } else {
            pageEventsNotifications.previousPage();
            initializeEventNotification();
        }
    }

    /**
     * Method linked to the buttonDontParticipateNotifications' onMouseClicked
     * It removes the participation from the Event shown in the Notifications Pane
     */
    public void eventDontParticipateEventNotifications() {
        if (currentEvent != null) {
            if (userPage.getEventsService().deleteParticipant(currentEvent, userPage.getUser()) != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "You participate no longer to the Event!");
                alert.show();
                initializeNumberNotifications();
                buttonParticipateNotifications.setVisible(true);
                buttonDontParticipateNotifications.setVisible(false);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error at removing the participation from the Event!");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error at removing the participation from the Event!");
            alert.show();
        }
    }

    /**
     * Method linked to the buttonParticipateNotifications' onMouseClicked
     * It makes the User a Participant to the Event shown in the Notifications Pane
     */
    public void eventParticipateEventNotifications() {
        if (currentEvent != null) {
            if (userPage.getEventsService().addParticipant(currentEvent, userPage.getUser()) != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Now you are participating to the Event!");
                alert.show();
                initializeNumberNotifications();
                buttonParticipateNotifications.setVisible(false);
                buttonDontParticipateNotifications.setVisible(true);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error at adding the participation from the Event!");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error at adding the participation from the Event!");
            alert.show();
        }
    }

    // FEED PANE
    private List<? extends Post> listCurrentPostsFeedFriends = new ArrayList<>();
    private List<? extends Post> listCurrentPostsProfileFriend = new ArrayList<>();
    private final ContentPage pagePhotoPostsFeedFriends = new ContentPage(6, 1);
    private final ContentPage pageTextPostsFeedFriends = new ContentPage(6, 1);

    @FXML
    TextField textFieldSearchFriend;
    @FXML
    Label labelFriendRealName;
    @FXML
    Label labelNumberFriendsFriend;
    @FXML
    Label labelNumberPhotosFriend;
    @FXML
    Label labelNumberPostsFriend;
    @FXML
    Label labelGoBackPhotoPostsFriends;
    @FXML
    Label labelGoNextPhotoPostsFriends;
    @FXML
    Label labelGoBackTextPostsFriends;
    @FXML
    Label labelGoNextTextPostsFriends;
    @FXML
    Label labelGoBackProfileFriend;
    @FXML
    Label labelGoNextProfileFriend;
    @FXML
    Label labelUsernameFriendFeed;
    @FXML
    Label labelUserNameStPost;
    @FXML
    Label labelUserNameNdPost;
    @FXML
    Label labelUserNameRdPost;
    @FXML
    Label labelUserName4thPost;
    @FXML
    Label labelUserName5thPost;
    @FXML
    Label labelUserName6thPost;
    @FXML
    Label labelNbBerriesStPostFriend;
    @FXML
    Label labelNbBerriesNdPostFriend;
    @FXML
    Label labelNbBerriesRdPostFriend;
    @FXML
    Label labelNbBerries4thPostFriend;
    @FXML
    Label labelNbBerries5thPostFriend;
    @FXML
    Label labelNbBerries6thPostFriend;
    @FXML
    Button buttonRemoveFriend;
    @FXML
    Button buttonStTextPostFriends;
    @FXML
    Button buttonNdTextPostFriends;
    @FXML
    Button buttonRdTextPostFriends;
    @FXML
    Button button4thTextPostFriends;
    @FXML
    Button button5thTextPostFriends;
    @FXML
    Button button6thTextPostFriends;
    @FXML
    Group groupNbBerriesStPostFriend;
    @FXML
    Group groupNbBerriesNdPostFriend;
    @FXML
    Group groupNbBerriesRdPostFriend;
    @FXML
    Group groupNbBerries4thPostFriend;
    @FXML
    Group groupNbBerries5thPostFriend;
    @FXML
    Group groupNbBerries6thPostFriend;
    @FXML
    Group groupBerryStPostFriend;
    @FXML
    Group groupBerryNdPostFriend;
    @FXML
    Group groupBerryRdPostFriend;
    @FXML
    Group groupBerry4thPostFriend;
    @FXML
    Group groupBerry5thPostFriend;
    @FXML
    Group groupBerry6thPostFriend;
    @FXML
    Group groupNbBerriesStPostFriendProfile;
    @FXML
    Group groupNbBerriesNdPostFriendProfile;
    @FXML
    Group groupNbBerriesRdPostFriendProfile;
    @FXML
    Group groupBerryStPostFriendProfile;
    @FXML
    Group groupBerryNdPostFriendProfile;
    @FXML
    Group groupBerryRdPostFriendProfile;
    @FXML
    Pane paneFriendsProfile;
    @FXML
    Pane paneFriendsFeed;
    @FXML
    Pane paneFriendsProfilePosts;
    @FXML
    Pane paneFriendsProfilePhotos;
    @FXML
    Pane panePhotoPostsFriends;
    @FXML
    Pane panePhotoPostsFriends1;
    @FXML
    Pane paneTextPostsFriends;
    @FXML
    Rectangle rectangleStPhotoPostFriends;
    @FXML
    Rectangle rectangleNdPhotoPostFriends;
    @FXML
    Rectangle rectangleRdPhotoPostFriends;
    @FXML
    Rectangle rectangle4thPhotoPostFriends;
    @FXML
    Rectangle rectangle5thPhotoPostFriends;
    @FXML
    Rectangle rectangle6thPhotoPostFriends;
    @FXML
    Rectangle rectangleStPhotoFriendFeed;
    @FXML
    Rectangle rectangleNdPhotoFriendFeed;
    @FXML
    Rectangle rectangleRdPhotoFriendFeed;
    @FXML
    Button buttonStPostProfileFriend;
    @FXML
    Button buttonNdPostProfileFriend;
    @FXML
    Button buttonRdPostProfileFriend;
    @FXML
    Button buttonBerryStPostFriend;
    @FXML
    Button buttonBerryNdPostFriend;
    @FXML
    Button buttonBerryRdPostFriend;
    @FXML
    Button buttonBerry4thPostFriend;
    @FXML
    Button buttonBerry5thPostFriend;
    @FXML
    Button buttonBerry6thPostFriend;
    @FXML
    Button buttonBerryStPostFriendProfile;
    @FXML
    Button buttonBerryNdPostFriendProfile;
    @FXML
    Button buttonBerryRdPostFriendProfile;
    @FXML
    Circle circleProfilePhotoFriend;

    /**
     * Method that initializes the Photo Posts of the Users' Friends
     * It sets the Photo Posts and the corresponding labels
     */
    private void initializePhotoPostsFriends() {
        List<PhotoPost> listPhotoPostsFriends = userPage.getPhotoPostService().getListPhotoPostsFriends(userPage.getUser().getId(), pagePhotoPostsFeedFriends);
        listCurrentPostsFeedFriends = listPhotoPostsFriends;
        labelGoBackPhotoPostsFriends.setVisible(pagePhotoPostsFeedFriends.getNumberPage() > 1);
        labelGoNextPhotoPostsFriends.setVisible(pagePhotoPostsFeedFriends.getSizePage() == listPhotoPostsFriends.size());
        setRectanglesPhoto(listPhotoPostsFriends, listRectanglesFeedFriends);
        setLabelsUserNames(listPhotoPostsFriends, listLabelsFeedFriends);
        setLabelPostLikes(listPhotoPostsFriends, listGroupsNbBerriesPostFriend);
        setButtonsBerry(listPhotoPostsFriends, listGroupsBerryPostFriend);
    }

    /**
     * Method that initializes the Text Posts of the Users' Friends
     * It sets the Text Posts and the corresponding labels
     */
    private void initializeTextPostsFriends() {
        List<TextPost> listTextPostsFriends = userPage.getTextPostService().getListTextPostsFriends(userPage.getUser().getId(), pageTextPostsFeedFriends);
        listCurrentPostsFeedFriends = listTextPostsFriends;
        labelGoBackTextPostsFriends.setVisible(pageTextPostsFeedFriends.getNumberPage() > 1);
        labelGoNextTextPostsFriends.setVisible(pageTextPostsFeedFriends.getSizePage() == listTextPostsFriends.size());
        setButtonsTextPosts(listTextPostsFriends, listButtonsFeedFriends);
        setLabelsUserNames(listTextPostsFriends, listLabelsFeedFriends);
        setLabelPostLikes(listTextPostsFriends, listGroupsNbBerriesPostFriend);
        setButtonsBerry(listTextPostsFriends, listGroupsBerryPostFriend);
    }

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
                    friendUser = searchedUser;
                    pagePhotoPostProfileFriend.setToFirstPage();
                    pageTextPostProfileFriend.setToFirstPage();
                    paneFriendsProfile.setVisible(true);
                    paneFriendsProfilePhotos.setVisible(true);
                    paneFriendsProfilePosts.setVisible(false);
                    paneFriendsFeed.setVisible(false);
                    labelFriendRealName.setVisible(true);
                    labelNumberFriendsFriend.setVisible(true);
                    labelNumberPostsFriend.setVisible(true);
                    labelNumberPhotosFriend.setVisible(true);
                    labelFriendRealName.setText(friendUser.getFullName());
                    labelNumberFriendsFriend.setText(userPage.getFriendshipService().getNumberFriends(friendUser.getId()) + " Friends");
                    labelNumberPhotosFriend.setText(userPage.getPhotoPostService().getNumberPhotoPosts(friendUser.getId()) + " Photos");
                    labelNumberPostsFriend.setText(userPage.getTextPostService().getNumberTextPosts(friendUser.getId()) + " Posts");
                    setImage(circleProfilePhotoFriend, userPage.getProfilePhotoUserService().findOne(friendUser.getId()).getPathProfilePhoto());
                    List<PhotoPost> listPhotoPostsFriend = userPage.getPhotoPostService().getListPhotoPosts(friendUser.getId(), pagePhotoPostProfileFriend);
                    listCurrentPostsProfileFriend = listPhotoPostsFriend;
                    labelGoBackProfileFriend.setVisible(pagePhotoPostProfileFriend.getNumberPage() > 1);
                    labelGoNextProfileFriend.setVisible(pagePhotoPostProfileFriend.getSizePage() == listPhotoPostsFriend.size());
                    setRectanglesPhoto(listPhotoPostsFriend, listRectanglesProfileFriend);
                    setLabelPostLikes(listPhotoPostsFriend, listGroupsNbBerriesPostFriendProfile);
                    setButtonsBerry(listPhotoPostsFriend, listGroupsBerryPostFriendProfile);
                }
            }
        } else if (usernameUser.length() == 0) {
            labelFriendRealName.setVisible(false);
            labelNumberFriendsFriend.setVisible(false);
            labelNumberPostsFriend.setVisible(false);
            labelNumberPhotosFriend.setVisible(false);
            paneFriendsProfile.setVisible(false);
            paneFriendsFeed.setVisible(true);
            initializeFeedPane();
            friendUser = null;
        }
    }

    /**
     * Method linked to the labelGoNextProfileFriend's onMouseClicked event
     * It shows the Posts of the Friend User on the next Page
     */
    public void eventGoNextFriend() {

        if (paneFriendsProfilePhotos.isVisible()) {
            pagePhotoPostProfileFriend.nextPage();
            List<PhotoPost> listPhotoPostsFriend = userPage.getPhotoPostService().getListPhotoPosts(friendUser.getId(), pagePhotoPostProfileFriend);
            listCurrentPostsProfileFriend = listPhotoPostsFriend;
            labelGoBackProfileFriend.setVisible(pagePhotoPostProfileFriend.getNumberPage() > 1);
            labelGoNextProfileFriend.setVisible(pagePhotoPostProfileFriend.getSizePage() == listPhotoPostsFriend.size());
            setRectanglesPhoto(userPage.getPhotoPostService().getListPhotoPosts(friendUser.getId(), pagePhotoPostProfileFriend), listRectanglesProfileFriend);
            setLabelPostLikes(listPhotoPostsFriend, listGroupsNbBerriesPostFriendProfile);
            setButtonsBerry(listPhotoPostsFriend, listGroupsBerryPostFriendProfile);
        } else if (paneFriendsProfilePosts.isVisible()){
            pageTextPostProfileFriend.nextPage();
            List<TextPost> listTextPostsFriends = userPage.getTextPostService().getListTextPosts(friendUser.getId(), pageTextPostProfileFriend);
            listCurrentPostsProfileFriend = listTextPostsFriends;
            labelGoBackProfileFriend.setVisible(pageTextPostProfileFriend.getNumberPage() > 1);
            labelGoNextProfileFriend.setVisible(pageTextPostProfileFriend.getSizePage() == listTextPostsFriends.size());
            setButtonsTextPosts(userPage.getTextPostService().getListTextPosts(friendUser.getId(), pageTextPostProfileFriend), listButtonsProfileFriend);
            setLabelPostLikes(listTextPostsFriends, listGroupsNbBerriesPostFriendProfile);
            setButtonsBerry(listTextPostsFriends, listGroupsBerryPostFriendProfile);
        }
    }

    /**
     * Method linked to the labelGoBackProfileFriend's onMouseClicked event
     * It shows the Posts of the Friend User on the next Page
     */
    public void eventGoBackFriend() {
        if (paneFriendsProfilePhotos.isVisible()) {
            if (pagePhotoPostProfileFriend.getNumberPage() == 1) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "You're already on the first Page!");
                alert.show();
            } else {
                pagePhotoPostProfileFriend.previousPage();
                List<PhotoPost> listPhotoPostsFriend = userPage.getPhotoPostService().getListPhotoPosts(friendUser.getId(), pagePhotoPostProfileFriend);
                listCurrentPostsProfileFriend = listPhotoPostsFriend;
                labelGoBackProfileFriend.setVisible(pagePhotoPostProfileFriend.getNumberPage() > 1);
                labelGoNextProfileFriend.setVisible(pagePhotoPostProfileFriend.getSizePage() == listPhotoPostsFriend.size());
                setRectanglesPhoto(listPhotoPostsFriend, listRectanglesProfileFriend);
                setLabelPostLikes(listPhotoPostsFriend, listGroupsNbBerriesPostFriendProfile);
                setButtonsBerry(listPhotoPostsFriend, listGroupsBerryPostFriendProfile);
            }
        } else if (paneFriendsProfilePosts.isVisible()) {
            if (pageTextPostProfileFriend.getNumberPage() == 1) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "You're already on the first Page!");
                alert.show();
            } else {
                pageTextPostProfileFriend.previousPage();
                List<TextPost> listTextPostsFriends = userPage.getTextPostService().getListTextPosts(friendUser.getId(), pageTextPostProfileFriend);
                listCurrentPostsProfileFriend = listTextPostsFriends;
                labelGoBackProfileFriend.setVisible(pageTextPostProfileFriend.getNumberPage() > 1);
                labelGoNextProfileFriend.setVisible(pageTextPostProfileFriend.getSizePage() == listTextPostsFriends.size());
                setButtonsTextPosts(listTextPostsFriends, listButtonsProfileFriend);
                setLabelPostLikes(listTextPostsFriends, listGroupsNbBerriesPostFriendProfile);
                setButtonsBerry(listTextPostsFriends, listGroupsBerryPostFriendProfile);
            }
        }
    }

    /**
     * Method linked to the labelGoNextPhotoPostsFriends' onMouseClicked event
     * It shows the Photo Posts of the User's Friends on the next Page
     */
    public void eventGoNextPhotoPostsFriends() {
        pagePhotoPostsFeedFriends.nextPage();
        initializePhotoPostsFriends();
    }

    /**
     * Method linked to the labelGoBackPhotoPostsFriends' onMouseClicked event
     * It shows the Photo Posts of the User's Friends on the previous Page
     */
    public void eventGoBackPhotoPostsFriends() {
        if (pagePhotoPostsFeedFriends.getNumberPage() == 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You're already on the first Page!");
            alert.show();
        } else {
            pagePhotoPostsFeedFriends.previousPage();
            initializePhotoPostsFriends();
        }
    }

    /**
     * Method linked to the labelGoNextTextPostsFriends' onMouseClicked event
     * It shows the Text Posts of the User's Friends on the next Page
     */
    public void eventGoNextTextPostsFriends() {
        pageTextPostsFeedFriends.nextPage();
        initializeTextPostsFriends();
    }

    /**
     * Method linked to the labelGoBackTextPostsFriends' onMouseClicked event
     * It shows the Text Posts of the User's Friends on the previous Page
     */
    public void eventGoBackTextPostsFriends() {
        if (pageTextPostsFeedFriends.getNumberPage() == 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You're already on the first Page!");
            alert.show();
        } else {
            pageTextPostsFeedFriends.previousPage();
            initializeTextPostsFriends();
        }
    }

    /**
     * Method linked to the buttonShowPhotoPostsFriends' onMouseClicked event
     * It shows the Photo Posts of the User's Friends or the Photo Posts of the Friend User
     */
    public void eventShowPhotoPostsFriends() {
        if (paneFriendsProfile.isVisible()) {
            showPhotoPaneProfileFriend();
        } else if (paneFriendsFeed.isVisible()) {
            showPhotoPaneFeedFriends();
        }
    }

    /**
     * Method linked to the buttonShowTextPostsFriends' onMouseClicked event
     * It shows the Text Posts of the User's Friends or the Text Posts of the Friend User
     */
    public void eventShowTextPostsFriends() {
        if (paneFriendsProfile.isVisible()) {
            showTextPaneProfileFriend();
        } else if (paneFriendsFeed.isVisible()) {
            showTextPaneFeedFriends();
        }
    }

    /**
     * Method that shows the Photo Posts of the User's Friends on Feed
     */
    private void showPhotoPaneFeedFriends() {
        pagePhotoPostsFeedFriends.setToFirstPage();
        panePhotoPostsFriends.setVisible(true);
        paneTextPostsFriends.setVisible(false);
        labelGoBackTextPostsFriends.setVisible(false);
        labelGoNextTextPostsFriends.setVisible(false);
        initializePhotoPostsFriends();
    }

    /**
     * Method that shows the Photo Posts of the Friend User
     */
    private void showPhotoPaneProfileFriend() {
        pagePhotoPostProfileFriend.setToFirstPage();
        List<PhotoPost> listPhotoPostsFriend = userPage.getPhotoPostService().getListPhotoPosts(friendUser.getId(), pagePhotoPostProfileFriend);
        listCurrentPostsProfileFriend = listPhotoPostsFriend;
        labelGoBackProfileFriend.setVisible(pagePhotoPostProfileFriend.getNumberPage() > 1);
        labelGoNextProfileFriend.setVisible(pagePhotoPostProfileFriend.getSizePage() == listPhotoPostsFriend.size());
        setRectanglesPhoto(listPhotoPostsFriend, listRectanglesProfileFriend);
        setLabelPostLikes(listPhotoPostsFriend, listGroupsNbBerriesPostFriendProfile);
        setButtonsBerry(listPhotoPostsFriend, listGroupsBerryPostFriendProfile);
        paneFriendsProfilePhotos.setVisible(true);
        paneFriendsProfilePosts.setVisible(false);
    }

    /**
     * Method that shows the Text Posts of the User's Friends on Feed
     */
    private void showTextPaneFeedFriends() {
        pageTextPostsFeedFriends.setToFirstPage();
        paneTextPostsFriends.setVisible(true);
        panePhotoPostsFriends.setVisible(false);
        labelGoBackPhotoPostsFriends.setVisible(false);
        labelGoNextPhotoPostsFriends.setVisible(false);
        initializeTextPostsFriends();
    }

    /**
     * Method that shows the Text Posts of the Friend User
     */
    private void showTextPaneProfileFriend() {
        pageTextPostProfileFriend.setToFirstPage();
        List<TextPost> listTextPostsFriends = userPage.getTextPostService().getListTextPosts(friendUser.getId(), pageTextPostProfileFriend);
        listCurrentPostsProfileFriend = listTextPostsFriends;
        labelGoBackProfileFriend.setVisible(pageTextPostProfileFriend.getNumberPage() > 1);
        labelGoNextProfileFriend.setVisible(pageTextPostProfileFriend.getSizePage() == listTextPostsFriends.size());
        setButtonsTextPosts(listTextPostsFriends, listButtonsProfileFriend);
        setLabelPostLikes(listTextPostsFriends, listGroupsNbBerriesPostFriendProfile);
        setButtonsBerry(listTextPostsFriends, listGroupsBerryPostFriendProfile);
        paneFriendsProfilePosts.setVisible(true);
        paneFriendsProfilePhotos.setVisible(false);
    }

    /**
     * Method linked to the buttonRemoveFriend's onMouseClicked event
     * It asks the User whether to to remove the Friend or not
     */
    public void eventButtonRemoveFriend() {
        Alert alertAskConfirmationRemoval = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure?", ButtonType.YES, ButtonType.NO);
        alertAskConfirmationRemoval.showAndWait();
        if (alertAskConfirmationRemoval.getResult() == ButtonType.YES) {
            userPage.getFriendshipService().deleteFriendship(new Tuple<>(userPage.getUser().getId(), friendUser.getId()));
            userPage.getFriendshipService().deleteFriendship(new Tuple<>(friendUser.getId(), userPage.getUser().getId()));
            initializeFeedPane(); // Removing the Friend means you can't access his/her profile from Feed
            numberFriends--;
            labelNumberFriends.setText(numberFriends + " Friends");
            Alert alertConfirmationRemoval = new Alert(Alert.AlertType.INFORMATION, "You're no longer friends!");
            alertConfirmationRemoval.show();
        }
    }

    /**
     * Method that likes / unlikes a Post, depending on the State of a Button
     * @param post Post, representing the Post to be liked / unliked
     *             post must be non-null
     * @param button Button, representing the Button whose Text is used to determine whether to like or unlike the Post
     * @param group Group, representing the Group corresponding to the number of Likes the Post has
     */
    private void likeUnlikeSelectedPost(Post post, Button button, Group group) {
        if (post instanceof PhotoPost) {
            if (button.getText().equals(PostLikeType.Berry.toString())) {
                userPage.getPhotoPostLikesService().likePost(post, userPage.getUser());
                button.setText(PostLikeType.Unberry.toString());
            } else if (button.getText().equals(PostLikeType.Unberry.toString())) {
                userPage.getPhotoPostLikesService().unlikePost(post, userPage.getUser());
                button.setText(PostLikeType.Berry.toString());
            }
        } else if (post instanceof TextPost) {
            if (button.getText().equals(PostLikeType.Berry.toString())) {
                userPage.getTextPostLikesService().likePost(post, userPage.getUser());
                button.setText(PostLikeType.Unberry.toString());
            } else if (button.getText().equals(PostLikeType.Unberry.toString())) {
                userPage.getTextPostLikesService().unlikePost(post, userPage.getUser());
                button.setText(PostLikeType.Berry.toString());
            }
        }
        setLabelPostLikes(Collections.singletonList(post), Collections.singletonList(group));
    }

    // PROFILE PANE
    @FXML
    Label labelGoBackProfile;
    @FXML
    Label labelGoNextProfile;
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
            if (pathToPhoto == null) {
                throw new FileNotFoundException();
            }
            FileInputStream fileInputStream = new FileInputStream(pathToPhoto);
            Image image = new Image(fileInputStream, 1000, 1000, true, true);
            shape.setFill(new ImagePattern(image));
            if (shape instanceof Rectangle) {
                ((Rectangle) shape).setArcHeight(45);
                ((Rectangle) shape).setArcWidth(45);
            }
            shape.setVisible(true);
        } catch (FileNotFoundException e) {
           shape.setFill(Paint.valueOf("#FFF"));
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
        if (panePhotoPostsProfile.isVisible()) {
            pagePhotoPostProfile.nextPage();
            List<PhotoPost> listPhotoPostsProfile = userPage.getPhotoPostService().getListPhotoPosts(userPage.getUser().getId(), pagePhotoPostProfile);
            labelGoBackProfile.setVisible(pagePhotoPostProfile.getNumberPage() > 1);
            labelGoNextProfile.setVisible(pagePhotoPostProfile.getSizePage() == listPhotoPostsProfile.size());
            setRectanglesPhoto(listPhotoPostsProfile, listRectanglesProfile);
        } else if (paneTextPostsProfile.isVisible()){
            pageTextPostProfile.nextPage();
            List<TextPost> listTextPostsProfile = userPage.getTextPostService().getListTextPosts(userPage.getUser().getId(), pageTextPostProfile);
            labelGoBackProfile.setVisible(pageTextPostProfile.getNumberPage() > 1);
            labelGoNextProfile.setVisible(pageTextPostProfile.getSizePage() == listTextPostsProfile.size());
            setButtonsTextPosts(listTextPostsProfile, listButtonsProfile);
        }
    }

    /**
     * Method linked to the labelGoBackProfile's onMouseClicked event
     * It shows the Posts of the User on the previous Page
     */
    public void eventGoBackProfile() {
        if (panePhotoPostsProfile.isVisible()) {
            if (pagePhotoPostProfile.getNumberPage() == 1) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "You're already on the first Page!");
                alert.show();
            } else {
                pagePhotoPostProfile.previousPage();
                List<PhotoPost> listPhotoPostsProfile = userPage.getPhotoPostService().getListPhotoPosts(userPage.getUser().getId(), pagePhotoPostProfile);
                labelGoBackProfile.setVisible(pagePhotoPostProfile.getNumberPage() > 1);
                labelGoNextProfile.setVisible(pagePhotoPostProfile.getSizePage() == listPhotoPostsProfile.size());
                setRectanglesPhoto(listPhotoPostsProfile, listRectanglesProfile);
            }
        } else if (paneTextPostsProfile.isVisible()) {
            if (pageTextPostProfile.getNumberPage() == 1) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "You're already on the first Page!");
                alert.show();
            } else {
                pageTextPostProfile.previousPage();
                List<TextPost> listTextPostsProfile = userPage.getTextPostService().getListTextPosts(userPage.getUser().getId(), pageTextPostProfile);
                labelGoBackProfile.setVisible(pageTextPostProfile.getNumberPage() > 1);
                labelGoNextProfile.setVisible(pageTextPostProfile.getSizePage() == listTextPostsProfile.size());
                setButtonsTextPosts(listTextPostsProfile, listButtonsProfile);
            }
        }
    }

    /**
     * Method linked to the buttonsShowProfilePhoto's onMouseClicked event
     * It shows the Photo Posts of the User
     */
    public void eventShowPhotoPaneProfile() {
        pagePhotoPostProfile.setToFirstPage();
        List<PhotoPost> listPhotoPostsProfile = userPage.getPhotoPostService().getListPhotoPosts(userPage.getUser().getId(), pagePhotoPostProfile);
        labelGoBackProfile.setVisible(pagePhotoPostProfile.getNumberPage() > 1);
        labelGoNextProfile.setVisible(pagePhotoPostProfile.getSizePage() == listPhotoPostsProfile.size());
        setRectanglesPhoto(listPhotoPostsProfile, listRectanglesProfile);
        panePhotoPostsProfile.setVisible(true);
        paneTextPostsProfile.setVisible(false);
    }

    /**
     * Method linked to the buttonsShowTextPhoto's onMouseClicked event
     * It shows the Text Posts of the User
     */
    public void eventShowTextPaneProfile() {
        pageTextPostProfile.setToFirstPage();
        List<TextPost> listTextPostsProfile = userPage.getTextPostService().getListTextPosts(userPage.getUser().getId(), pageTextPostProfile);
        labelGoBackProfile.setVisible(pageTextPostProfile.getNumberPage() > 1);
        labelGoNextProfile.setVisible(pageTextPostProfile.getSizePage() == listTextPostsProfile.size());
        setButtonsTextPosts(listTextPostsProfile, listButtonsProfile);
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
     *    its size can't be greater than 6 since a Page contains maximum 6 Photos
     * @param listRectangles List<Rectangle>, representing the Rectangles to be filled
     */
    private void setRectanglesPhoto(List<PhotoPost> listPhotoPosts, List<Rectangle> listRectangles) {
        listRectangles.forEach(shape -> shape.setVisible(false));
        for (int i = 0; i < listPhotoPosts.size(); i++) {
            PhotoPost currentPhotoPost = listPhotoPosts.get(i);
            Rectangle currentRectangle = listRectangles.get(i);
            setImage(currentRectangle, currentPhotoPost.getPhotoURL());
            Tooltip tooltip = new Tooltip("Posted on " +
                    currentPhotoPost.getPostDate().format(Constants.DATE_TIME_FORMATTER_MONTH_NAME)
            );
            tooltip.setShowDelay(Duration.seconds(2));
            Tooltip.install(currentRectangle, tooltip);
        }
    }

    /**
     * Method that sets some Buttons used for Text Posts with some Text
     * @param listTextPosts List<TextPosts>, representing the Text Posts whose Texts are to be set to the Buttons
     *    its size can't be greater than 6 since a Page contains maximum 6 Text Posts
     * @param listButtons List<Button>, representing the Rectangles to be set
     */
    private void setButtonsTextPosts(List<TextPost> listTextPosts, List<Button> listButtons) {
        // First, reset the Buttons
        listButtons.forEach(button -> button.setVisible(false));
        for (int i = 0; i < listTextPosts.size(); i++) {
            TextPost currentTextPost = listTextPosts.get(i);
            Button currentButton = listButtons.get(i);
            currentButton.setVisible(true);
            currentButton.setText(currentTextPost.getText());
            Tooltip tooltip = new Tooltip("Posted on " +
                    currentTextPost.getPostDate().format(Constants.DATE_TIME_FORMATTER_MONTH_NAME)
            );
            tooltip.setShowDelay(Duration.seconds(2));
            Tooltip.install(currentButton, tooltip);
        }
    }

    /**
     * Method that sets some Labels corresponding to the Friends' Posts
     * @param listPosts List<? extends Post>, representing the Friends' Posts whose Users' Usernames are used to set the Labels
     *    its size can't be greater than 6 since a Page contains maximum 6 Posts
     * @param listLabels List<Label>, representing the Labels to be set
     */
    private void setLabelsUserNames(List<? extends Post> listPosts, List<Label> listLabels) {
        // First, reset the Labels
        listLabels.forEach(label -> label.setText(""));
        for (int i = 0; i < listPosts.size(); i++) {
            Long userID = listPosts.get(i).getUserID();
            listLabels.get(i).setText("@" + userPage.getUserCredentialsService().findOne(userID).getUsername());
        }
    }

    /**
     * Method that sets some Labels corresponding to the number of Likes each Post has
     * @param listPosts List<? extends Post>, representing the list of Posts whose numbers of Likes are used to set the Labels
     * @param listGroups List<Group>, representing the Groups to be set - containing a Label & an ImageView
     */
    private void setLabelPostLikes(List<? extends Post> listPosts, List<Group> listGroups) {
        // First, reset the Groups
        listGroups.forEach(group -> group.setVisible(false));
        for (int i = 0; i < listPosts.size(); i++) {
            Post currentPost = listPosts.get(i);
            Group currentGroup = listGroups.get(i);
            currentGroup.setVisible(true);
            Node stNode = currentGroup.getChildren().get(0);
            if (stNode instanceof Label) {
                if (currentPost instanceof PhotoPost) {
                    ((Label) stNode).setText(userPage.getPhotoPostLikesService().getNumberOfLikes(currentPost).toString());
                } else if (currentPost instanceof TextPost){
                    ((Label) stNode).setText(userPage.getTextPostLikesService().getNumberOfLikes(currentPost).toString());
                }
            }
        }
    }

    /**
     * Method that sets some Buttons corresponding to Liking a Post
     * @param listPosts List<? extends Post>, representing the list of Posts
     * @param listGroups List<Group>, representing the Groups to be set - containing a Button & an ImageView
     */
    private void setButtonsBerry(List<? extends Post> listPosts, List<Group> listGroups) {
        listGroups.forEach(group -> group.setVisible(false));
        for (int i = 0; i < listPosts.size(); i++) {
            Post currentPost = listPosts.get(i);
            Node stNode = listGroups.get(i).getChildren().get(0);
            if (stNode instanceof Button) {
                if (currentPost instanceof PhotoPost) {
                    PostLike postLike = userPage.getPhotoPostLikesService().getLikePhotoPost(currentPost, userPage.getUser());
                    if (postLike == null) { // The User hasn't liked the Post yet
                        ((Button) stNode).setText(PostLikeType.Berry.toString());
                    } else {
                        ((Button) stNode).setText(PostLikeType.Unberry.toString());
                    }
                } else if (currentPost instanceof TextPost){
                    PostLike postLike = userPage.getTextPostLikesService().getLikePhotoPost(currentPost, userPage.getUser());
                    if (postLike == null) { // The User hasn't liked the Post yet
                        ((Button) stNode).setText(PostLikeType.Berry.toString());
                    } else {
                        ((Button) stNode).setText(PostLikeType.Unberry.toString());
                    }
                }
            }
            listGroups.get(i).setVisible(true);
        }
    }

    /**
     * Overridden method that updates the Text Posts on the current Page
     * @param textPostEvent TextPostEvent, representing the Event corresponding to the changes in Text Post Data
     */
    @Override
    public void update(TextPostEvent textPostEvent) {
        setButtonsTextPosts(userPage.getTextPostService().getListTextPosts(userPage.getUser().getId(), pageTextPostProfile), listButtonsProfile);
    }

    // DIRECT PANE
    private int MAX_CHARACTERS_MESSAGE = 26;
    private int WIDTH_START_GROUP = 40;
    private int LAYOUT_X_START_GROUP = 160;
    private float SCALE_LABEL_MESSAGE = 5.7f;
    private User currentFriendChat; // The current User the Logged In User chats with
    private List<Circle> listCirclesPhotoFriends;
    private List<Group> listGroupMessagesUser;
    private List<Group> listGroupMessagesFriend;
    private List<Group> listGroupMessagesInbox;
    private List<User> listCurrentFriendsChat = new ArrayList<>();
    private List<Message> listCurrentMessagesInbox = new ArrayList<>();
    private final ContentPage pageFriendConversation = new ContentPage(7, 1);
    private final ContentPage pagePossibleFriendChat = new ContentPage(8, 1);
    private final ContentPage pageMessagesInbox = new ContentPage(11, 1);

    @FXML
    Label labelNameFriendChat;
    @FXML
    Label labelGoBackFriendChat;
    @FXML
    Label labelGoNextFriendChat;
    @FXML
    Label labelGoBackInbox;
    @FXML
    Label labelGoNextInbox;
    @FXML
    Circle circlePhotoFriendChat;
    @FXML
    Circle circleStPhotoFriendChat;
    @FXML
    Circle circleNdPhotoFriendChat;
    @FXML
    Circle circleRdPhotoFriendChat;
    @FXML
    Circle circle4thPhotoFriendChat;
    @FXML
    Circle circle5thPhotoFriendChat;
    @FXML
    Circle circle6thPhotoFriendChat;
    @FXML
    Circle circle7thPhotoFriendChat;
    @FXML
    Circle circle8thPhotoFriendChat;
    @FXML
    TextField textFieldChat;
    @FXML
    TextField textFieldToCompose;
    @FXML
    TextField textFieldSubjectCompose;
    @FXML
    TextArea textAreaCompose;
    @FXML
    Group groupStMessageUser;
    @FXML
    Group groupNdMessageUser;
    @FXML
    Group groupRdMessageUser;
    @FXML
    Group group4thMessageUser;
    @FXML
    Group group5thMessageUser;
    @FXML
    Group group6thMessageUser;
    @FXML
    Group groupHiddenMessageUser;
    @FXML
    Group groupStMessageFriend;
    @FXML
    Group groupNdMessageFriend;
    @FXML
    Group groupRdMessageFriend;
    @FXML
    Group group4thMessageFriend;
    @FXML
    Group group5thMessageFriend;
    @FXML
    Group group6thMessageFriend;
    @FXML
    Group groupHiddenMessageFriend;
    @FXML
    Group groupStMessageInbox;
    @FXML
    Group groupNdMessageInbox;
    @FXML
    Group groupRdMessageInbox;
    @FXML
    Group group4thMessageInbox;
    @FXML
    Group group5thMessageInbox;
    @FXML
    Group group6thMessageInbox;
    @FXML
    Group group7thMessageInbox;
    @FXML
    Group group8thMessageInbox;
    @FXML
    Group group9thMessageInbox;
    @FXML
    Group group10thMessageInbox;
    @FXML
    Group group11thMessageInbox;
    @FXML
    Pane directPane;
    @FXML
    Pane paneChatWrapper;
    @FXML
    Pane panePhotoChatFriends;
    @FXML
    Pane paneChat;
    @FXML
    Pane paneInbox;
    @FXML
    Pane paneCompose;

    /**
     * Method that sets some Circles with the User's Friends' Profile Photos
     * @param listFriendsUser List<User>, representing the User's Friends
     * @param listCircles List<Circle>, representing the Circles to be set
     */
    private void setCirclesPhoto(List<User> listFriendsUser, List<Circle> listCircles) {
        // First, reset the Circles
        listCircles.forEach(circle -> circle.setFill(Paint.valueOf("#40457B")));
        for (int i = 0; i < listFriendsUser.size(); i++) {
            User currentUser = listFriendsUser.get(i);
            setImage(listCircles.get(i),
                    userPage.getProfilePhotoUserService().findOne(currentUser.getId()).getPathProfilePhoto());
            Tooltip tooltip = new Tooltip(currentUser.getFullName());
            Tooltip.install(listCircles.get(i), tooltip);
        }
    }

    /**
     * Method that initializes the components of a Group with the content of a Message
     * It sets the label of the Group (Message Text) with the information of the Message - its text
     * @param message ReplyMessage, representing the Messages whose data is used
     * @param group Group, representing the Group to be set
     * @param senderType SenderType, representing the Type of the Sender - USER or FRIEND
     */
    private void initializeGroup(ReplyMessage message, Group group, SenderType senderType) {
        Node stNode = group.getChildren().get(0);
        Node ndNode = group.getChildren().get(1);
        if (stNode instanceof Rectangle && ndNode instanceof Label) {
            Rectangle rectangle = (Rectangle) stNode;
            Label label = (Label) ndNode;
            int lengthMessage = message.getMessage().length();
            if (lengthMessage > MAX_CHARACTERS_MESSAGE) { // If the message has more characters, the text will Wrap
                lengthMessage = MAX_CHARACTERS_MESSAGE;
            }
            label.setText(message.getMessage());
            rectangle.setWidth(WIDTH_START_GROUP + (lengthMessage - 1) * SCALE_LABEL_MESSAGE);
            if (senderType == SenderType.USER) { // For the User's Messages, have to adjust the LayoutX as well
                rectangle.setLayoutX(LAYOUT_X_START_GROUP - (lengthMessage - 1)* SCALE_LABEL_MESSAGE);
            }
        }
    }

    /**
     * Method that initializes the components of a Group with the content of a Message
     * It sets the labels of the Group - User's Name, Text Message & Date, with the information of the Message
     * @param message Message, representing the Messages whose data is used
     * @param group Group, representing the Group to be set
     */
    private void initializeGroup(Message message, Group group) {
        Node stNode = group.getChildren().get(1);
        Node ndNode = group.getChildren().get(2);
        Node rdNode = group.getChildren().get(3);
        if (stNode instanceof Label && ndNode instanceof Label && rdNode instanceof Label) {
            ((Label) stNode).setText(message.getFrom().getFullName());
            ((Label) ndNode).setText(message.getSubject());
            ((Label) rdNode).setText(message.getDate().format(Constants.DATE_TIME_FORMATTER_MONTH_NAME));
        }
    }

    /**
     * Method that initializes the Conversation between the User and the Friend
     */
    private void initializeConversation() {
        if (currentFriendChat == null) {
            return;
        }
        List<ReplyMessage> conversation = userPage.getReplyMessageService().getListConversationOnPage(
                userPage.getUser().getId(), currentFriendChat.getId(), pageFriendConversation
        );
        listGroupMessagesUser.forEach(group -> group.setVisible(false));
        listGroupMessagesFriend.forEach(group -> group.setVisible(false));
        for (int i = 0; i < conversation.size(); i++) {
            ReplyMessage currentMessage = conversation.get(i);
            Group currentGroup = null;
            if (currentMessage.getFrom().getId().equals(userPage.getUser().getId())) {
                currentGroup = listGroupMessagesUser.get(i);
                currentGroup.setVisible(true);
                initializeGroup(currentMessage, currentGroup, SenderType.USER);
            } else { // The Friend sent the Message
                currentGroup = listGroupMessagesFriend.get(i);
                currentGroup.setVisible(true);
                initializeGroup(currentMessage, currentGroup, SenderType.FRIEND);
            }
        }
    }

    /**
     * Method that initializes the Messages in the User's Inbox
     */
    private void initializeMessagesInbox() {
        listGroupMessagesInbox.forEach(group -> group.setVisible(false));
        List<Message> inbox = userPage.getMessageService().getListAllMessagesToUser(userPage.getUser().getId(), pageMessagesInbox);
        listCurrentMessagesInbox.clear();
        listCurrentMessagesInbox.addAll(inbox);
        labelGoBackInbox.setVisible(pageMessagesInbox.getNumberPage() > 1);
        labelGoNextInbox.setVisible(pageMessagesInbox.getSizePage() == inbox.size());
        for (int i = 0; i < inbox.size(); i++) {
            listGroupMessagesInbox.get(i).setVisible(true);
            initializeGroup(inbox.get(i), listGroupMessagesInbox.get(i));
        }
    }

    // DIRECT BUTTONS EVENTS

    /**
     * Method linked to the buttonChat's onMouseClicked
     * It shows the Chat Pane
     */
    public void eventShowChat() {
        initializePaneChat();
    }

    /**
     * Method linked to the buttonInbox's onMouseClicked
     * It shows the Inbox Pane
     */
    public void eventShowInbox() {
        pageMessagesInbox.setToFirstPage();
        currentDirectPane.setVisible(false);
        currentDirectPane = paneInbox;
        currentDirectPane.setVisible(true);
        initializeMessagesInbox();
    }

    /**
     * Method linked to the buttonComposeMessage's onMouseClicked
     * It shows the Compose Pane
     */
    public void eventShowCompose() {
        currentDirectPane.setVisible(false);
        currentDirectPane = paneCompose;
        currentDirectPane.setVisible(true);
        initializeComposeChat();
    }

    /**
     * Method that initializes the Chat Pane
     */
    private void initializePaneChat() {
        pagePossibleFriendChat.setToFirstPage();
        if (currentDirectPane != null) {
            currentDirectPane.setVisible(false);
        }
        currentDirectPane = paneChatWrapper;
        currentDirectPane.setVisible(true);
        initializeCurrentFriendsChat();
    }

    /**
     * Method linked to the buttonSendCompose' onMouseClicked
     * It sends a Message to some Users
     */
    public void sendMessageCompose() {
        String usernamesTo = textFieldToCompose.getText();
        if (usernamesTo.matches("[ ]*")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The messages has no recipients!");
            alert.show();
            textFieldToCompose.clear();
        }
        else {
            List<User> listUsersTo = new ArrayList<>();
            String invalidUsernames = "";
            for (String userName : usernamesTo.split(" ")) {
                UserCredentials userCredentials = userPage.getUserCredentialsService().findOne(userName);
                if (userCredentials != null) { // The User exists
                    User userTo = userPage.getUserService().getUser(userCredentials.getId());
                    if (!listUsersTo.contains(userTo)) {
                        listUsersTo.add(userTo);
                    }
                } else {
                    invalidUsernames += userName + " ";
                }
            }
            if (invalidUsernames.length() > 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "The following usernames were invalid: " + invalidUsernames);
                alert.show();
                alert.setWidth(alert.getWidth() * 1.2);
                alert.setHeight(alert.getHeight() * 1.2);
                textFieldToCompose.clear();
            }
            if (listUsersTo.size() > 0) {
                String messageSubject = textFieldSubjectCompose.getText();
                if (messageSubject.matches("[ ]*")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "The subject is empty!");
                    alert.show();
                    textFieldSubjectCompose.clear();
                } else {
                    String messageText = textAreaCompose.getText();
                    if (messageText.matches("[ ]*")) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "The message is empty!");
                        alert.show();
                        textAreaCompose.clear();
                    } else {
                        Message message = new Message(
                                userPage.getUser(), listUsersTo, messageSubject, messageText, LocalDateTime.now()
                        );
                        if (userPage.getMessageService().addMessage(message) == null) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The message has been sent!");
                            alert.show();
                            initializeComposeChat();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Error at sending the message!");
                            alert.show();
                            initializeComposeChat();
                        }
                    }
                }
            }
        }
    }

    /**
     * Method that initializes the Compose Chat
     * It clears out the Text Fields & Text Area
     */
    private void initializeComposeChat() {
        textFieldToCompose.clear();
        textFieldSubjectCompose.clear();
        textAreaCompose.clear();
    }

    /**
     * Method that initializes the current Friends the User might chat with
     */
    private void initializeCurrentFriendsChat() {
        listCirclesPhotoFriends.forEach(circle -> circle.setVisible(false));
        listCurrentFriendsChat.clear();
        listCurrentFriendsChat.addAll(userPage.getUserService().getListAllConsecutiveFriends(userPage.getUser().getId(), pagePossibleFriendChat));
        labelGoBackFriendChat.setVisible(pagePossibleFriendChat.getNumberPage() > 1);
        labelGoNextFriendChat.setVisible(pagePossibleFriendChat.getSizePage() == listCurrentFriendsChat.size());
        setCirclesPhoto(listCurrentFriendsChat, listCirclesPhotoFriends);
    }

    /**
     * Method that initializes the Friend the User chats with
     * It sets the Chat Pane - the Friend's Profile Photo and Name and the first messages between the two Users and then
     * It shows the Chat Pane
     * @param friendToChatWith User, representing the Friend the User chats with
     */
    private void initializeFriendChat(User friendToChatWith) {
        textFieldChat.setDisable(false);
        textFieldChat.clear();
        setImage(circlePhotoFriendChat,
                userPage.getProfilePhotoUserService().findOne(friendToChatWith.getId()).getPathProfilePhoto()
        );
        labelNameFriendChat.setText(friendToChatWith.getFullName());
        currentFriendChat = friendToChatWith;
        pageFriendConversation.setToFirstPage();
        initializeConversation();
    }

    /**
     * Method connected to the circleStPhotoFriendChat's onMouseClicked
     * It shows the Chat between the Friend and the User
     */
    public void chatStFriendEvent() {
        if (listCurrentFriendsChat.size() > 0) {
            initializeFriendChat(listCurrentFriendsChat.get(0));
        }
    }

    /**
     * Method connected to the circleNdPhotoFriendChat's onMouseClicked
     * It shows the Chat between the Friend and the User
     */
    public void chatNdFriendEvent() {
        if (listCurrentFriendsChat.size() > 1) {
            initializeFriendChat(listCurrentFriendsChat.get(1));
        }
    }

    /**
     * Method connected to the circleRdPhotoFriendChat's onMouseClicked
     * It shows the Chat between the Friend and the User
     */
    public void chatRdFriendEvent() {
        if (listCurrentFriendsChat.size() > 2) {
            initializeFriendChat(listCurrentFriendsChat.get(2));
        }
    }

    /**
     * Method connected to the circle4thPhotoFriendChat's onMouseClicked
     * It shows the Chat between the Friend and the User
     */
    public void chat4thFriendEvent() {
        if (listCurrentFriendsChat.size() > 3) {
            initializeFriendChat(listCurrentFriendsChat.get(3));
        }
    }

    /**
     * Method connected to the circle5thPhotoFriendChat's onMouseClicked
     * It shows the Chat between the Friend and the User
     */
    public void chat5thFriendEvent() {
        if (listCurrentFriendsChat.size() > 4) {
            initializeFriendChat(listCurrentFriendsChat.get(4));
        }
    }

    /**
     * Method connected to the circle6thPhotoFriendChat's onMouseClicked
     * It shows the Chat between the Friend and the User
     */
    public void chat6thFriendEvent() {
        if (listCurrentFriendsChat.size() > 5) {
            initializeFriendChat(listCurrentFriendsChat.get(5));
        }
    }

    /**
     * Method connected to the circle7thPhotoFriendChat's onMouseClicked
     * It shows the Chat between the Friend and the User
     */
    public void chat7thFriendEvent() {
        if (listCurrentFriendsChat.size() > 6) {
            initializeFriendChat(listCurrentFriendsChat.get(6));
        }
    }

    /**
     * Method connected to the circle8thPhotoFriendChat's onMouseClicked
     * It shows the Chat between the Friend and the User
     */
    public void chat8thFriendEvent() {
        if (listCurrentFriendsChat.size() > 7) {
            initializeFriendChat(listCurrentFriendsChat.get(7));
        }
    }

    /**
     * Method linked to the labelGoBackFriendChat's onMouseClicked
     * It shows the previous Friend the User might chat with
     */
    public void eventGoBackFriendChat() {
        pagePossibleFriendChat.previousPage();
        if (pagePossibleFriendChat.getNumberPage() == 1) {
            labelGoBackFriendChat.setVisible(false);
        } else {
            labelGoBackFriendChat.setVisible(true);
        }
        initializeCurrentFriendsChat();
    }

    /**
     * Method linked to the labelGoNextFriendChat's onMouseClicked
     * It shows the next Friend the User might chat with
     */
    public void eventGoNextFriendChat() {
        pagePossibleFriendChat.nextPage();
        labelGoBackFriendChat.setVisible(true);
        initializeCurrentFriendsChat();
    }

    /**
     * Method linked to the labelGoBackInbox's onMouseClicked
     * It shows the Inbox of the User on the previous page
     */
    public void eventGoBackInbox() {
        if (pageMessagesInbox.getNumberPage() == 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You're already on the first page!");
            alert.show();
        } else {
            pageMessagesInbox.previousPage();
            initializeMessagesInbox();
        }
    }

    /**
     * Method linked to the labelGoNextInbox's onMouseClicked
     * It shows the Inbox of the User on the next page
     */
    public void eventGoNextInbox() {
        pageMessagesInbox.nextPage();
        initializeMessagesInbox();
    }

    private void eventInboxMessageClickedOn(Message message) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/replyInboxView.fxml"));
        Stage replyInboxStage = new Stage();
        replyInboxStage.initModality(Modality.APPLICATION_MODAL);
        ViewClass viewClassInitializer = new ViewClass();
        try {
            viewClassInitializer.initView(replyInboxStage, loader);
        } catch (IOException ignored) {
        }
        ReplyInboxController replyInboxController = loader.getController();
        replyInboxController.setUserPage(userPage);
        replyInboxController.setReceivedMessage(message);
        replyInboxController.setReplyInboxStage(replyInboxStage);
        replyInboxStage.show();
    }

    // EXPLORE PANE
    private List<FriendshipRequest> listCurrentRequestsReceived = new ArrayList<>();
    private List<FriendshipRequest> listCurrentRequestsSent = new ArrayList<>();
    private List<User> listCurrentNonFriends = new ArrayList<>();
    private List<Group> listGroupRequestsReceived;
    private List<Group> listGroupRequestsSent;
    private List<Group> listGroupsNonFriends;
    private final ContentPage pageFriendRequestsReceived = new ContentPage(5, 1);
    private final ContentPage pageFriendRequestsSent = new ContentPage(5, 1);
    private final ContentPage pageNonFriends = new ContentPage(5, 1);

    @FXML
    Group groupRequestReceivedSt;
    @FXML
    Group groupRequestReceivedNd;
    @FXML
    Group groupRequestReceivedRd;
    @FXML
    Group groupRequestReceived4th;
    @FXML
    Group groupRequestReceived5th;
    @FXML
    Group groupRequestSentSt;
    @FXML
    Group groupRequestSentNd;
    @FXML
    Group groupRequestSentRd;
    @FXML
    Group groupRequestSent4th;
    @FXML
    Group groupRequestSent5th;
    @FXML
    Group groupNonFriendSt;
    @FXML
    Group groupNonFriendNd;
    @FXML
    Group groupNonFriendRd;
    @FXML
    Group groupNonFriend4th;
    @FXML
    Group groupNonFriend5th;
    @FXML
    Pane paneExploreReceivedRequests;
    @FXML
    Pane paneExploreSentRequests;
    @FXML
    Label labelFriendshipRequests;
    @FXML
    Label labelNoNewUsers;
    @FXML
    Label labelNoNewRequests;
    @FXML
    Label labelGoBackExploreUsers;
    @FXML
    Label labelGoNextExploreUsers;
    @FXML
    Label labelGoBackExploreRequestsReceived;
    @FXML
    Label labelGoNextExploreRequestsReceived;
    @FXML
    Label labelGoBackExploreRequestsSent;
    @FXML
    Label labelGoNextExploreRequestsSent;

    /**
     * Method that initializes the components of some Groups with the content of some Friendship Requests
     * It sets the Circle of each Group with an Image and the Label of each Group with a String
     * @param listFriendshipRequests List<FriendshipRequest>, representing the list of Friendship Requests
     * @param listGroups List<Group>, representing the list of Groups
     * @param typeFriendshipRequest TypeFriendshipRequest, representing the Type of the Friendship Request - RECEIVED or SENT
     */
    private void initializeGrouping(List<FriendshipRequest> listFriendshipRequests, List<Group> listGroups, TypeFriendshipRequest typeFriendshipRequest) {
        listCurrentRequestsReceived.clear();
        listCurrentRequestsSent.clear();
        listGroups.forEach(group -> group.setVisible(false)); // Reset the groups
        labelNoNewRequests.setVisible(listFriendshipRequests.size() == 0);
        for (int i = 0; i < listFriendshipRequests.size(); i++) {
            FriendshipRequest currentFriendshipRequest = listFriendshipRequests.get(i);
            Group currentGroup = listGroups.get(i);
            if (typeFriendshipRequest == TypeFriendshipRequest.RECEIVED) {
                listCurrentRequestsReceived.add(currentFriendshipRequest);
                initializeGroup(userPage.getUserService().getUser(currentFriendshipRequest.getFrom().getId()), currentGroup);
            } else if (typeFriendshipRequest == TypeFriendshipRequest.SENT) {
                listCurrentRequestsSent.add(currentFriendshipRequest);
                initializeGroup(userPage.getUserService().getUser(currentFriendshipRequest.getTo().get(0).getId()), currentGroup);
            }
        }
    }

    /**
     * Method that initializes the components of some Groups with the content of some Users
     * It sets the Circle of each Group with an Image and the Label of each Group with a String
     * @param listUsers List<User>, representing the list of Users
     * @param listGroups List<Group>, representing the list of Groups
     */
    private void initializeGrouping(List<User> listUsers, List<Group> listGroups) {
        listCurrentNonFriends.clear();
        listGroups.forEach(group -> group.setVisible(false));
        labelNoNewUsers.setVisible(listUsers.size() == 0);
        for (int i = 0; i < listUsers.size(); i++) {
            listCurrentNonFriends.add(listUsers.get(i));
            initializeGroup(listUsers.get(i), listGroups.get(i));
        }
    }

    /**
     * Method that initializes the components of a Group with the content of a User
     * It sets the Circle of the Group with an Profile Photo of the User and
     * the Label of the Group with the Full Name of the User
     * @param user User, representing the User whose data is used
     * @param group Group, representing the Group to be set
     */
    private void initializeGroup(User user, Group group) {
        group.setVisible(true);
        Node ndChild = group.getChildren().get(1);
        Node rdChild = group.getChildren().get(2);
        setImage((Shape) ndChild, userPage.getProfilePhotoUserService().findOne(user.getId()).getPathProfilePhoto());
        ((Label)rdChild).setText(user.getFullName());
    }

    /**
     * Method that initializes the Received Friendship Requests
     */
    private void initializeReceivedRequests() {
        List<FriendshipRequest> listReceivedRequests = userPage.getFriendshipRequestService().getListReceivedPendingRequests(
                userPage.getUser().getId(), pageFriendRequestsReceived
        );
        labelGoBackExploreRequestsReceived.setVisible(pageFriendRequestsReceived.getNumberPage() > 1);
        labelGoNextExploreRequestsReceived.setVisible(pageFriendRequestsReceived.getSizePage() == listReceivedRequests.size());
        initializeGrouping(listReceivedRequests, listGroupRequestsReceived, TypeFriendshipRequest.RECEIVED);
    }

    /**
     * Method that initializes the Sent Friendship Requests
     */
    private void initializeSentRequests() {
        List<FriendshipRequest> listSentRequests = userPage.getFriendshipRequestService().getListSentPendingRequests(
                userPage.getUser().getId(), pageFriendRequestsSent
        );
        labelGoBackExploreRequestsSent.setVisible(pageFriendRequestsSent.getNumberPage() > 1);
        labelGoNextExploreRequestsSent.setVisible(pageFriendRequestsSent.getSizePage() == listSentRequests.size());
        initializeGrouping(listSentRequests, listGroupRequestsSent, TypeFriendshipRequest.SENT);
    }

    /**
     * Method that initializes the Non Friend Users
     */
    private void initializeNonFriends(){
        List<User> listNonFriends = userPage.getUserService().getListAllNonFriends(
                userPage.getUser().getId(), pageNonFriends
        );
        labelGoBackExploreUsers.setVisible(pageNonFriends.getNumberPage() > 1);
        labelGoNextExploreUsers.setVisible(pageNonFriends.getSizePage() == listNonFriends.size());
        initializeGrouping(listNonFriends, listGroupsNonFriends);
    }

    /**
     * Method linked to the buttonShowReceivedRequests onMouseClicked
     * It shows the Received Friendship Requests by the User
     */
    public void eventShowReceivedRequests() {
        pageFriendRequestsReceived.setToFirstPage();
        paneExploreReceivedRequests.setVisible(true);
        paneExploreSentRequests.setVisible(false);
        labelFriendshipRequests.setText("Received Requests");
        initializeReceivedRequests();
    }

    /**
     * Method linked to the buttonShowSentRequests onMouseClicked
     * It shows the Sent Friendship Requests by the User
     */
    public void eventShowSentRequests() {
        pageFriendRequestsSent.setToFirstPage();
        paneExploreSentRequests.setVisible(true);
        paneExploreReceivedRequests.setVisible(false);
        labelFriendshipRequests.setText("Sent Requests");
        initializeSentRequests();
    }

    /**
     * Method linked to the labelGoNextExploreRequestsReceived onMouseClicked
     * It shows the Friendship Requests received by the User on the next Page
     */
    public void eventGoNextReceivedRequests() {
        pageFriendRequestsReceived.nextPage();
        initializeReceivedRequests();
    }

    /**
     * Method linked to the labelGoNextExploreRequestsReceived onMouseClicked
     * It shows the Friendship Requests received by the User on the previous Page
     */
    public void eventGoBackReceivedRequests() {
        if (pageFriendRequestsReceived.getNumberPage() == 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You're already on the first Page!");
            alert.show();
        } else {
            pageFriendRequestsReceived.previousPage();
            initializeReceivedRequests();
        }
    }

    /**
     * Method linked to the labelGoNextExploreRequestsSent onMouseClicked
     * It shows the Friendship Requests sent by the User on the next Page
     */
    public void eventGoNextSentRequests() {
        pageFriendRequestsSent.nextPage();
        initializeSentRequests();
    }

    /**
     * Method linked to the labelGoBackExploreRequestsSent onMouseClicked
     * It shows the Friendship Requests sent by the User on the previous Page
     */
    public void eventGoBackSentRequests() {
        if (pageFriendRequestsSent.getNumberPage() == 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You're already on the first Page!");
            alert.show();
        } else {
            pageFriendRequestsSent.previousPage();
            initializeSentRequests();
        }
    }

    /**
     * Method linked to the labelGoNextExploreUsers onMouseClicked
     * It shows the Users that are not friends with the Logged In User on the next Page
     */
    public void eventGoNextExploreUsers() {
        pageNonFriends.nextPage();
        initializeNonFriends();
    }

    /**
     * Method linked to the labelGoBackExploreUsers onMouseClicked
     * It shows the Users that are not friends with the Logged In User on the previous Page
     */
    public void eventGoBackExploreUsers() {
        if (pageNonFriends.getNumberPage() == 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You're already on the first Page!");
            alert.show();
        } else {
            pageNonFriends.previousPage();
            initializeNonFriends();
        }
    }

    // EXPLORE EVENTS BUTTONS

    /**
     * Method linked to the buttonConfirmFriendSt's onMouseClicked
     * It accepts the first Received Friendship Request
     */
    public void eventButtonConfirmSt() {
        eventAcceptFriendshipRequest(userPage.getFriendshipRequestService().acceptFriendshipRequest(listCurrentRequestsReceived.get(0)));
    }

    /**
     * Method linked to the buttonConfirmFriendSt's onMouseClicked
     * It accepts the second Received Friendship Request
     */
    public void eventButtonConfirmNd() {
        eventAcceptFriendshipRequest(userPage.getFriendshipRequestService().acceptFriendshipRequest(listCurrentRequestsReceived.get(1)));
    }

    /**
     * Method linked to the buttonConfirmFriendRd's onMouseClicked
     * It accepts the third Received Friendship Request
     */
    public void eventButtonConfirmRd() {
        eventAcceptFriendshipRequest(userPage.getFriendshipRequestService().acceptFriendshipRequest(listCurrentRequestsReceived.get(2)));
    }

    /**
     * Method linked to the buttonConfirmFriend4th's onMouseClicked
     * It accepts the 4th Received Friendship Request
     */
    public void eventButtonConfirm4th() {
        eventAcceptFriendshipRequest(userPage.getFriendshipRequestService().acceptFriendshipRequest(listCurrentRequestsReceived.get(3)));
    }

    /**
     * Method linked to the buttonConfirmFriend5th's onMouseClicked
     * It accepts the 5th Received Friendship Request
     */
    public void eventButtonConfirm5th() {
        eventAcceptFriendshipRequest(userPage.getFriendshipRequestService().acceptFriendshipRequest(listCurrentRequestsReceived.get(4)));
    }

    /**
     * Method linked to the buttonConfirmFriend's onMouseClicked
     * It accepts a Friendship Request, resetting the current Received Requests and updating the labelNumberFriends
     * @param friendshipRequest null, if the Friendship Request was accepted successfully
     *           non-null FriendshipRequest, otherwise
     */
    private void eventAcceptFriendshipRequest(FriendshipRequest friendshipRequest) {
        if (friendshipRequest == null) {
            initializeReceivedRequests();
            numberFriends++;
            labelNumberFriends.setText(numberFriends + " Friends");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The Friendship Request has benn accepted!");
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error at accepting the Friendship Request");
            alert.show();
        }
    }

    /**
     Method linked to the buttonDeclineFriendSt's onMouseClicked
     * It decline the first Received Friendship Request
     */
    public void eventButtonDeclineSt() {
        eventDeclineFriendshipRequest(listCurrentRequestsReceived.get(0));
    }

    /**
     Method linked to the buttonDeclineFriendNd's onMouseClicked
     * It decline the second Received Friendship Request
     */
    public void eventButtonDeclineNd() {
        eventDeclineFriendshipRequest(listCurrentRequestsReceived.get(1));
    }

    /**
     Method linked to the buttonDeclineFriendRd's onMouseClicked
     * It decline the third Received Friendship Request
     */
    public void eventButtonDeclineRd() {
        eventDeclineFriendshipRequest(listCurrentRequestsReceived.get(2));
    }

    /**
     Method linked to the buttonDeclineFriend4th's onMouseClicked
     * It decline the 4th Received Friendship Request
     */
    public void eventButtonDecline4th() {
        eventDeclineFriendshipRequest(listCurrentRequestsReceived.get(3));
    }

    /**
     Method linked to the buttonDeclineFriend5th's onMouseClicked
     * It decline the 5th Received Friendship Request
     */
    public void eventButtonDecline5th() {
        eventDeclineFriendshipRequest(listCurrentRequestsReceived.get(4));
    }

    /**
     * Method linked to the buttonDeclineFriend's onMouseClicked
     * It declines a Friendship Request
     * @param friendshipRequest FriendshipRequest, representing the Friendship Request to be declined
     */
    private void eventDeclineFriendshipRequest(FriendshipRequest friendshipRequest) {
        if (userPage.getFriendshipRequestService().declineFriendshipRequest(friendshipRequest) == null) {
            initializeReceivedRequests();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The Friendship Request has been declined!");
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error at declining the Friendship Request");
            alert.show();
        }
    }

    /**
     Method linked to the buttonAddFriendSt's onMouseClicked
     * It sends a Friendship Request so the second User
     */
    public void eventButtonSendRequestNd() {
        eventSendFriendshipRequest(
                new FriendshipRequest(userPage.getUser(), Collections.singletonList(listCurrentNonFriends.get(1)), "Friendship Request", LocalDateTime.now(), "pending")
        );
    }

    /**
     Method linked to the buttonAddFriendSt's onMouseClicked
     * It sends a Friendship Request so the third User
     */
    public void eventButtonSendRequestRd() {
        eventSendFriendshipRequest(
                new FriendshipRequest(userPage.getUser(), Collections.singletonList(listCurrentNonFriends.get(2)), "Friendship Request", LocalDateTime.now(), "pending")
        );
    }

    /**
     Method linked to the buttonAddFriendSt's onMouseClicked
     * It sends a Friendship Request so the 4th User
     */
    public void eventButtonSendRequest4th() {
        eventSendFriendshipRequest(
                new FriendshipRequest(userPage.getUser(), Collections.singletonList(listCurrentNonFriends.get(3)), "Friendship Request", LocalDateTime.now(), "pending")
        );
    }

    /**
     Method linked to the buttonAddFriendSt's onMouseClicked
     * It sends a Friendship Request so the 5th User
     */
    public void eventButtonSendRequest5th() {
        eventSendFriendshipRequest(
                new FriendshipRequest(userPage.getUser(), Collections.singletonList(listCurrentNonFriends.get(4)), "Friendship Request", LocalDateTime.now(), "pending")
        );
    }

    /**
     Method linked to the buttonAddFriendSt's onMouseClicked
     * It sends a Friendship Request so the first User
     */
    public void eventButtonSendRequestSt() {
        eventSendFriendshipRequest(
                new FriendshipRequest(userPage.getUser(), Collections.singletonList(listCurrentNonFriends.get(0)), "Friendship Request", LocalDateTime.now(), "pending")
        );
    }

    /**
     * Method linked to the buttonAddFriend's onMouseClicked
     * It sends a Friendship Request to an User
     * @param friendshipRequest FriendshipRequest, representing the Friendship Request to be sent
     */
    private void eventSendFriendshipRequest(FriendshipRequest friendshipRequest) {
        if (userPage.getFriendshipRequestService().addFriendshipRequest(friendshipRequest) == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The Friendship Request has been sent!");
            alert.show();
            initializeNonFriends();
            if (paneExploreSentRequests.isVisible()) {
                initializeSentRequests();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error at sending the Friendship Request");
            alert.show();
        }
    }

    /**
     * Method linked to the buttonCancelSentSt's onMouseClicked
     * It cancels the first sent Friendship Request by the User
     */
    public void eventButtonCancelRequestSt() {
        eventCancelFriendshipRequest(listCurrentRequestsSent.get(0));
    }

    /**
     * Method linked to the buttonCancelSentSt's onMouseClicked
     * It cancels the second sent Friendship Request by the User
     */
    public void eventButtonCancelRequestNd() {
        eventCancelFriendshipRequest(listCurrentRequestsSent.get(1));
    }

    /**
     * Method linked to the buttonCancelSentSt's onMouseClicked
     * It cancels the third sent Friendship Request by the User
     */
    public void eventButtonCancelRequestRd() {
        eventCancelFriendshipRequest(listCurrentRequestsSent.get(2));
    }

    /**
     * Method linked to the buttonCancelSentSt's onMouseClicked
     * It cancels the 4th sent Friendship Request by the User
     */
    public void eventButtonCancelRequest4th() {
        eventCancelFriendshipRequest(listCurrentRequestsSent.get(3));
    }

    /**
     * Method linked to the buttonCancelSentSt's onMouseClicked
     * It cancels the 5th sent Friendship Request by the User
     */
    public void eventButtonCancelRequest5th() {
        eventCancelFriendshipRequest(listCurrentRequestsSent.get(4));
    }

    /**
     * Method linked to the buttonCancelSent onMouseClicked
     * It retrieves (deletes) a sent Friendship Request by the User
     * @param friendshipRequest FriendshipRequest, representing the Friendship Request sent by the User,
     *                to be canceled (retrieved)
     */
    private void eventCancelFriendshipRequest(FriendshipRequest friendshipRequest) {
        if (userPage.getFriendshipRequestService().deleteFriendshipRequest(friendshipRequest.getId()) != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The sent Friendship Request has been canceled!");
            alert.show();
            initializeSentRequests();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Error at canceling the sent Friendship Request");
            alert.show();
        }
    }

    // EVENTS PANE
    private final String TEXT_PEOPLE = "People Responded";
    private final String TEXT_HOSTED_BY = "Hosted By";
    private String currentNewEventPhotoURL;
    private final ContentPage pageEventsDiscover = new ContentPage(1, 1);
    private final ContentPage pageEventsParticipate = new ContentPage(1, 1);
    private Event currentEvent;
    private Pane currentEventPane;

    @FXML
    Rectangle rectanglePhotoEventAdd;
    @FXML
    Rectangle rectanglePhotoEvent;
    @FXML
    TextField textFieldEventName;
    @FXML
    TextField textFieldEventLocation;
    @FXML
    TextField textFieldEventOrganization;
    @FXML
    TextField textFieldEventCategory;
    @FXML
    TextArea textAreaEventDescription;
    @FXML
    DatePicker datePickerEventStartDate;
    @FXML
    DatePicker datePickerEventEndDate;
    @FXML
    Button buttonParticipate;
    @FXML
    Button buttonSubscribe;
    @FXML
    Button buttonAddEvent;
    @FXML
    Button buttonAddNewEvent;
    @FXML
    Button buttonDeleteEvent;
    @FXML
    Label labelNoEvents;
    @FXML
    Label labelEventName;
    @FXML
    Label labelEventNbPeople;
    @FXML
    Label labelEventDate;
    @FXML
    Label labelEventLocation;
    @FXML
    Label labelEventHosted;
    @FXML
    Label labelEventCategory;
    @FXML
    Label labelEventDescription;
    @FXML
    Label labelRemoveParticipationEvent;
    @FXML
    Label labelGoBackEventDiscover;
    @FXML
    Label labelGoNextEventDiscover;
    @FXML
    Label labelGoNextEventParticipate;
    @FXML
    Label labelGoBackEventParticipate;
    @FXML
    Group groupPhotoEventAdd;
    @FXML
    Group groupEventDetailsParticipate;
    @FXML
    Pane paneEventsParticipate;
    @FXML
    Pane paneEventsParticipateDetails;
    @FXML
    Pane paneEventsAddEvent;

    /**
     * Method that initializes an Event
     * It sets the Image and the Details of the Event
     * @param event Event, representing the Event to be initialized
     * @param group Group, representing the Group corresponding to the Event's details
     * @param rectangle Rectangle, representing the Rectangle corresponding to the Event's Photo
     */
    private void initializeEvent(Event event, Group group, Rectangle rectangle) {
        currentEvent = event;
        setImage(rectangle, event.getPhotoURL());
        initializeGroup(event, group);
    }

    /**
     * Method that initializes the components of a Group with the content of an Event
     * It sets the labels of the Group with the information about the Event
     * @param event Event, representing the Event whose data is used
     * @param group Group, representing the Group to be set
     */
    private void initializeGroup(Event event, Group group) {
        Node stNode = group.getChildren().get(0);
        Node ndNode = group.getChildren().get(1);
        Node rdNode = group.getChildren().get(2);
        Node fourthNode = group.getChildren().get(3);
        Node fifthNode = group.getChildren().get(4);
        Node sixthNode = group.getChildren().get(5);
        Node seventhNode = group.getChildren().get(6);
        if (stNode instanceof Label) {
            ((Label) stNode).setText(event.getName().toUpperCase());
        }
        if (ndNode instanceof Label) {
            ((Label) ndNode).setText(userPage.getEventsService().getNumberParticipants(event) + " " + TEXT_PEOPLE);
        }
        if (rdNode instanceof Label) {
            LocalDate startDate = event.getStartDate();
            LocalDate endDate = event.getEndDate();
            ((Label) rdNode).setText(
                    startDate.getDayOfMonth() + " " + DateConverter.convertMonthIntegerToString(startDate.getMonthValue()) + " " + startDate.getYear() + " - " +
                    endDate.getDayOfMonth() + " " + DateConverter.convertMonthIntegerToString(endDate.getMonthValue()) + " " + endDate.getYear()
            );
        }
        if (fourthNode instanceof Label) {
            ((Label) fourthNode).setText(event.getLocation());
        }
        if (fifthNode instanceof Label) {
            ((Label) fifthNode).setText(TEXT_HOSTED_BY + " " + event.getOrganization());
        }
        if (sixthNode instanceof Label) {
            ((Label) sixthNode).setText(event.getCategory());
        }
        if (seventhNode instanceof Label) {
            ((Label) seventhNode).setText(event.getDescription().replace("\n", " "));
        }
    }

    /**
     * Method that initializes the Event to be Discovered by the User
     */
    private void initializeEventToBeDiscovered() {
        List<Event> listEventsDiscoverOnPage = userPage.getEventsService().getListEventsDoesntParticipate(userPage.getUser().getId(), pageEventsDiscover);
        labelGoBackEventDiscover.setVisible(pageEventsDiscover.getNumberPage() > 1);
        labelGoNextEventDiscover.setVisible(pageEventsDiscover.getSizePage() == listEventsDiscoverOnPage.size());
        if (!listEventsDiscoverOnPage.isEmpty()) {
            paneEventsParticipateDetails.setVisible(true);
            initializeEvent(listEventsDiscoverOnPage.get(0), groupEventDetailsParticipate, rectanglePhotoEvent);
        } else {
            labelNoEvents.setVisible(true);
            paneEventsParticipateDetails.setVisible(false);
            labelNoEvents.setText("There are no new Events");
        }
    }

    /**
     * Method that initializes the Event the User Participates to
     */
    private void initializeEventParticipateTo() {
        List<Event> listEventsParticipateOnPage = userPage.getEventsService().getListEventsParticipate(userPage.getUser().getId(), pageEventsParticipate);
        labelGoBackEventParticipate.setVisible(pageEventsParticipate.getNumberPage() > 1);
        labelGoNextEventParticipate.setVisible(pageEventsParticipate.getSizePage() == listEventsParticipateOnPage.size());
        if (!listEventsParticipateOnPage.isEmpty()) {
            Event eventToParticipateTo = listEventsParticipateOnPage.get(0);
            if (userPage.getEventsService().getParticipant(eventToParticipateTo, userPage.getUser()).getNotified()) {
                buttonSubscribe.setText("Unsubscribe");
            } else {
                buttonSubscribe.setText("Subscribe");
            }
            paneEventsParticipateDetails.setVisible(true);
            initializeEvent(eventToParticipateTo, groupEventDetailsParticipate, rectanglePhotoEvent);
        } else {
            labelNoEvents.setVisible(true);
            paneEventsParticipateDetails.setVisible(false);
            labelNoEvents.setText("No Events to participate to");
        }
    }

    /**
     * Method linked to the buttonDiscover onMouseClicked event
     * It shows the Events the User doesn't Participate to
     */
    public void eventButtonDiscover() {
        labelGoBackEventDiscover.setVisible(true);
        labelGoNextEventDiscover.setVisible(true);
        buttonParticipate.setVisible(true);
        buttonSubscribe.setVisible(false);
        labelNoEvents.setVisible(false);
        labelGoBackEventParticipate.setVisible(false);
        labelGoNextEventParticipate.setVisible(false);
        labelRemoveParticipationEvent.setVisible(false);
        currentEventPane.setVisible(false);
        currentEventPane = paneEventsParticipate;
        currentEventPane.setVisible(true);
        pageEventsDiscover.setToFirstPage();
        initializeEventToBeDiscovered();
    }

    /**
     * Method linked to the buttonYourEvents onMouseClicked event
     * It shows the Events the User Participates to
     */
    public void eventButtonYourEvents() {
        labelGoBackEventParticipate.setVisible(true);
        labelGoNextEventParticipate.setVisible(true);
        labelRemoveParticipationEvent.setVisible(true);
        buttonSubscribe.setVisible(true);
        buttonParticipate.setVisible(false);
        pageEventsParticipate.setToFirstPage();
        labelNoEvents.setVisible(false);
        labelGoBackEventDiscover.setVisible(false);
        labelGoNextEventDiscover.setVisible(false);
        currentEventPane.setVisible(false);
        currentEventPane = paneEventsParticipate;
        currentEventPane.setVisible(true);
        initializeEventParticipateTo();
    }

    /**
     * Method linked to the buttonAddEvent onMouseClicked event
     * It shows the User the Pane to add a new Event
     */
    public void eventButtonAddEvent() {
        currentEventPane.setVisible(false);
        currentEventPane = paneEventsAddEvent;
        currentEventPane.setVisible(true);
        buttonAddNewEvent.setVisible(true);
        buttonDeleteEvent.setVisible(false);
        clearAddEventPane();
    }

    /**
     * Method that clears the components of the Add Event Pane
     */
    private void clearAddEventPane() {
        currentNewEventPhotoURL = "";
        setImage(rectanglePhotoEventAdd, currentNewEventPhotoURL);
        textFieldEventName.clear();
        textFieldEventLocation.clear();
        textFieldEventOrganization.clear();
        textFieldEventCategory.clear();
        datePickerEventStartDate.setValue(null);
        datePickerEventEndDate.setValue(null);
        textAreaEventDescription.clear();
        groupPhotoEventAdd.setVisible(true);
    }

    /**
     * Method linked to the buttonAddNewEvent onMouseClicked event
     * It adds a new Event hosted by the User
     */
    public void eventButtonAddNewEvent() {
        String eventName = textFieldEventName.getText();
        LocalDate eventStartDate = datePickerEventStartDate.getValue();
        LocalDate eventEndDate = datePickerEventEndDate.getValue();
        String eventLocation = textFieldEventLocation.getText();
        String eventOrganization = textFieldEventOrganization.getText();
        String eventCategory = textFieldEventCategory.getText();
        String eventDescription = textAreaEventDescription.getText();
        try {
            currentEvent = userPage.getEventsService().addEvent(new Event(
                    eventName, eventStartDate, eventEndDate, eventOrganization, eventDescription, eventLocation,
                    eventCategory, currentNewEventPhotoURL, userPage.getUser()
            ));
            if (currentEvent != null) {
                buttonDeleteEvent.setVisible(true);
                buttonAddNewEvent.setVisible(false);
                userPage.getEventsService().addParticipant(currentEvent, userPage.getUser());
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "The event has been added successfully!");
                alert.show();
                initializeNumberNotifications();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error at adding the event");
                alert.show();
            }
        } catch (ValidationException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please complete all mandatory fields!");
            alert.show();
        }
    }

    /**
     * Method linked to the buttonDeleteEvent onMouseClicked event
     * It allows the User to delete the newly added Event
     */
    public void eventButtonDeleteNewEvent() {
        if (currentEvent != null) {
            if (userPage.getEventsService().deleteEvent(currentEvent.getId()) != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "The event has been deleted successfully!");
                alert.show();
                initializeNumberNotifications();
                clearAddEventPane();
                return;
            }
        }
        Alert alert = new Alert(Alert.AlertType.ERROR, "Error at deleting the event");
        alert.show();
    }

    /**
     * Method linked to the rectanglePhotoEventAdd onMouseClicked event
     * It allows the User to select an Event Photo
     */
    public void eventGetNewEventPhoto() {
        currentNewEventPhotoURL = getPhotoURL();
        setImage(rectanglePhotoEventAdd, currentNewEventPhotoURL);
        if (currentNewEventPhotoURL != null) {
            groupPhotoEventAdd.setVisible(false);
        }
    }

    /**
     * Method linked to the labelRemoveParticipationEvent's onMouseClicked event
     * It allows the User to Remove Participation from the current Event
     */
    public void eventRemoveParticipationEvent() {
        if (currentEvent != null) {
            Alert alertAskConfirmationRemoval = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES, ButtonType.NO);
            alertAskConfirmationRemoval.showAndWait();
            if (alertAskConfirmationRemoval.getResult() == ButtonType.YES) {
                if (userPage.getEventsService().deleteParticipant(currentEvent, userPage.getUser()) != null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "You participate no longer to the Event!");
                    alert.show();
                    initializeNumberNotifications();
                    initializeEventParticipateTo();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error at removing participation from the Event!");
                    alert.show();
                }
            }
        }
    }

    /**
     * Method linked to labelGoNextEventDiscover onMouseClicked event
     * It shows the next Event to be Discovered by the User
     */
    public void eventGoNextEventDiscover() {
        pageEventsDiscover.nextPage();
        initializeEventToBeDiscovered();
    }

    /**
     * Method linked to the labelGoBackEventDiscover onMouseClicked event
     * It shows the previous Event to be Discovered by the User
     */
    public void eventGoBackEventDiscover() {
        if (pageEventsDiscover.getNumberPage() == 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You're already on the first Page!");
            alert.show();
        } else {
            pageEventsDiscover.previousPage();
            initializeEventToBeDiscovered();
        }
    }

    /**
     * Method linked to the labelGoNextEventParticipate onMouseClicked event
     * It shows the next Event the User Participates to
     */
    public void eventGoNextEventParticipate() {
        pageEventsParticipate.nextPage();
        initializeEventParticipateTo();
    }

    /**
     * Method linked to the labelGoBackEventParticipate onMouseClicked event
     * It shows the previous Event the User Participates to
     */
    public void eventGoBackEventParticipate() {
        if (pageEventsParticipate.getNumberPage() == 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You're already on the first Page!");
            alert.show();
        } else {
            pageEventsParticipate.previousPage();
            initializeEventParticipateTo();
        }
    }

    /**
     * Method linked to the buttonParticipate onMouseClicked event
     * It makes the User a Participant to the current Event
     */
    public void eventButtonParticipate() {
        if (currentEvent != null) {
            userPage.getEventsService().addParticipant(currentEvent, userPage.getUser());
            initializeEventToBeDiscovered();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You are now participating to the event!");
            initializeNumberNotifications();
            alert.show();
        }
    }

    /**
     * Method linked to the buttonSubscribe onMouseClicked event
     * It subscribes/unsubscribes the User from getting notifications about an Event
     */
    public void eventButtonSubscribe() {
        if (currentEvent != null) {
            if (buttonSubscribe.getText().equals("Subscribe")) {
                userPage.getEventsService().subscribe(currentEvent, userPage.getUser());
                buttonSubscribe.setText("Unsubscribe");
                initializeNumberNotifications();
            } else if (buttonSubscribe.getText().equals("Unsubscribe")) {
                userPage.getEventsService().unsubscribe(currentEvent, userPage.getUser());
                buttonSubscribe.setText("Subscribe");
            }
            initializeNumberNotifications();
        }
    }

    // STATISTICS PANE
    @FXML
    DatePicker datePickerStartDate;
    @FXML
    DatePicker datePickerEndDate;
    @FXML
    TextField textFieldYear;
    @FXML
    TextField textFieldSearchFriendStatistics;
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
        String searchedFriendUsername = textFieldSearchFriendStatistics.getText();
        UserCredentials searchedFriendCredentials = userPage.getUserCredentialsService().findOne(searchedFriendUsername);
        User searchedFriend = null;
        if (searchedFriendCredentials != null) {
            searchedFriend = userPage.getUserService().getUser(searchedFriendCredentials.getId());
        }
        List<Message> messageList = new ArrayList<>();
        if (searchedFriend != null) {
            messageList.addAll(userPage.getMessageService().getListAllMessagesToUserTimeInterval(
                    userPage.getUserDTO().getId(), searchedFriend.getId(), dateStart, dateEnd
            ));
        } else if (searchedFriendUsername.matches("[ ]*")) {
            messageList.addAll(userPage.getMessageService().getListAllMessagesToUserTimeInterval(
                    userPage.getUserDTO().getId(), dateStart, dateEnd
            ));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The searched Friend doesn't exist!");
            alert.show();
            textFieldSearchFriendStatistics.clear();
            return;
        }

        List<Friendship> friendshipList = userPage.getFriendshipService().getListAllFriendshipsUserTimeInterval(
                userPage.getUserDTO().getId(), dateStart, dateEnd
        );
        if (messageList.size() == 0) { // No messages in that Date Period
            messageList.add(new Message(new User("No messages", "No messages"), null, "No messages","No messages", LocalDateTime.now()));
        }
        try {
            File file = ResourceUtils.getFile("classpath:report2.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(messageList);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("UserReport", userPage.getUserDTO().getFullName() + "'s Report");
            parameters.put("StatisticsFriendsMessages", "You befriended " + friendshipList.size() +
                    " people and received " + messageList.size() + " messages");
            parameters.put("DatePeriodReport", "Date Period: " + dateStart + " - " + dateEnd);
            if (searchedFriend != null) {
                parameters.put("TranscriptFriendName", "Transcript: " + searchedFriend.getFullName());
            } else {
                parameters.put("TranscriptFriendName", "Transcript: all Friends");
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            String pathToGenerateTo = "C:\\Users\\dasco\\" + userPage.getUserDTO().getFullName().replace(' ', '_');
            if (typeReport == TypeReport.PDF) {
                pathToGenerateTo += "Report.pdf";
                JasperExportManager.exportReportToPdfFile(jasperPrint, pathToGenerateTo);
            } else if (typeReport == TypeReport.HTML) {
                pathToGenerateTo += "Report.html";
                JasperExportManager.exportReportToHtmlFile(jasperPrint, pathToGenerateTo + "Report.html");
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The report has been generated successfully!");
            alert.show();
            if (Desktop.isDesktopSupported()) {
                try {
                    File reportFile = new File(pathToGenerateTo);
                    Desktop.getDesktop().open(reportFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            textFieldSearchFriendStatistics.clear();
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