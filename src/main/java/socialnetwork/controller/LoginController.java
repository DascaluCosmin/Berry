package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.service.*;
import socialnetwork.service.postsServices.PhotoPostService;
import socialnetwork.service.postsServices.PostLikesService;
import socialnetwork.service.postsServices.TextPostService;
import socialnetwork.utils.ViewClass;
import socialnetwork.utils.passwordEncryption.PasswordCrypt;

import java.io.IOException;

public class LoginController {
    @FXML
    TextField textFieldUsername;
    @FXML
    PasswordField passwordField;
    @FXML
    TextField textFieldFirstname;
    @FXML
    TextField textFieldLastname;
    @FXML
    TextField textFieldUsernameSignup;
    @FXML
    PasswordField passwordFieldSignup;
    @FXML
    AnchorPane anchorPaneSignup;
    @FXML
    AnchorPane anchorPaneLogin;

    private UserService userService;
    private FriendshipService friendshipService;
    private FriendshipRequestService friendshipRequestService;
    private ProfilePhotoUserService profilePhotoUserService;
    private MessageService messageService;
    private UserCredentialsService userCredentialsService;
    private ReplyMessageService replyMessageService;
    private TextPostService textPostService;
    private PhotoPostService photoPostService;
    private EventsService eventsService;
    private PostLikesService photoPostLikesService;
    private PostLikesService textPostLikesService;
    private Stage loginStage;

    /**
     * Method that initializes the View
     */
    @FXML
    public void initialize() {
        passwordField.setOnKeyPressed(event -> {
            try {
                escapeKeyPressed(event.getCode());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @param loginStage Stage, representing the Login Stage
     */
    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    /**
     * @param userService UserService, representing the Service handling the User Data
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * @param friendshipService FriendshipService, representing the Service handling the Friendships Data
     */
    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    /**
     * @param friendshipRequestService FriendshipRequestService, representing the Service handling the Friendship
     *                                 Requests data
     */
    public void setFriendshipRequestService(FriendshipRequestService friendshipRequestService) {
        this.friendshipRequestService = friendshipRequestService;
    }

    /**
     * @param profilePhotoUserService ProfilePhotoUserService, representing the Service handling the Profile Photo User data
     */
    public void setProfilePhotoUserService(ProfilePhotoUserService profilePhotoUserService) {
        this.profilePhotoUserService = profilePhotoUserService;
    }

    /**
     * @param messageService MessageService, representing the Service handling the Message data
     */
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * @param userCredentialsService UserCredentialsService, representing the Service handling the User Credentials data
     */
    public void setUserCredentialsService(UserCredentialsService userCredentialsService) {
        this.userCredentialsService = userCredentialsService;
    }

    /**
     * @param replyMessageService ReplyMessageService, representing the Service handling the Reply Message data
     */
    public void setReplyMessageService(ReplyMessageService replyMessageService) {
        this.replyMessageService = replyMessageService;
    }

    /**
     * @param textPostService TextPostService, representing the Service handling the Text Posts data
     */
    public void setTextPostService(TextPostService textPostService) {
        this.textPostService = textPostService;
    }

    /**
     * @param photoPostService PhotoPostService, representing the Service handling the Photo Posts data
     */
    public void setPhotoPostService(PhotoPostService photoPostService) {
        this.photoPostService = photoPostService;
    }

    /**
     * @param eventsService EventsService, representing the Service handling the Events data
     */
    public void setEventsService(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    /**
     * @param photoPostLikesService PostLikesService, representing the Service handling the Photo Post Likes data
     */
    public void setPhotoPostLikesService(PostLikesService photoPostLikesService) {
        this.photoPostLikesService = photoPostLikesService;
    }

    /**
     * @param textPostLikesService PostLikesService, representing the Service handling the Text Post Likes data
     */
    public void setTextPostLikesService(PostLikesService textPostLikesService) {
        this.textPostLikesService = textPostLikesService;
    }

    /**
     * Event handler - tries to log in the User
     * @throws IOException
     */
    public void loginEvent() throws IOException {
        String username = textFieldUsername.getText();
        String password = passwordField.getText();
        if (username.matches("[ ]*") || password.matches("[ ]*")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please introduce the credentials!");
            alert.show();
            return;
        }
        UserCredentials userCredentials = userCredentialsService.findOne(username);
        if (userCredentials != null) {
            if (PasswordCrypt.checkPassword(password, userCredentials.getPassword())) {
                UserDTO loggedInUser = userService.getUserDTO(userCredentials.getId());
                initializeAccountUserView(loggedInUser);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid password!");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid username!");
            alert.show();
            textFieldUsername.clear();
        }
        passwordField.clear();
    }

    /**
     * Method that exits the Application
     */
    public void exit() {
        System.exit(0);
    }

    /**
     * Method that initializes the Account User View
     * @param loggedInUser UserDTO, representing the logged in User
     */
    private void initializeAccountUserView(UserDTO loggedInUser) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/accountUserV2.fxml"));
            Stage accountUserStage = new Stage();
            ViewClass viewClass = new ViewClass();
            viewClass.initView(accountUserStage, loader);
            accountUserStage.setTitle(loggedInUser.getFirstName() + " " + loggedInUser.getLastName() + "'s account");
            AccountUserControllerV2 accountUserControllerV2 = loader.getController();
            Page loggedInUserPage = new Page(loggedInUser, userService, friendshipService, friendshipRequestService,
                    profilePhotoUserService, userCredentialsService, replyMessageService, messageService, textPostService,
                    photoPostService, eventsService, photoPostLikesService, textPostLikesService);
            accountUserControllerV2.setUserPage(loggedInUserPage);
            accountUserControllerV2.setAccountUserStage(accountUserStage);
            accountUserControllerV2.setLoginStage(loginStage);
            loginStage.hide();
            accountUserStage.show();
            textFieldUsername.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Event handler - creates a new Account
     */
    public void eventCreateAccount() {
        anchorPaneLogin.setVisible(false);
        anchorPaneSignup.setVisible(true);
        textFieldUsername.clear();
        passwordField.clear();
    }

    /**
     * Event handler - exits the Sign Up Form
     */
    public void exitSignup() {
        anchorPaneSignup.setVisible(false);
        anchorPaneLogin.setVisible(true);
        textFieldFirstname.clear();
        textFieldLastname.clear();
        textFieldUsernameSignup.clear();
        passwordFieldSignup.clear();
    }

    /**
     * Event handler - signs up a new User
     */
    public void signupEvent() {
        String firstName = textFieldFirstname.getText();
        String lastName = textFieldLastname.getText();
        String username = textFieldUsernameSignup.getText();
        String password = passwordFieldSignup.getText();
        if (firstName.equals("") || lastName.equals("") || username.equals("") || password.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please complete all fields!");
            alert.show();
        } else if (firstName.matches(".*\\d.*") || firstName.contains(" ")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The first name can't contain blank spaces or digits!");
            alert.show();
        } else if (lastName.matches(".*\\d.*") || lastName.contains(" ")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The last name can't contain blank spaces or digits!");
            alert.show();
        } else if (username.contains(" ")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The username can't contain blank spaces!");
            alert.show();
        } else if (password.contains(" ")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The password can't contain blank spaces!");
            alert.show();
        } else if (username.length() < 4) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The username has to be at least 4 characters long!");
            alert.show();
        } else if (password.length() < 4) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The password has to be at least 4 characters long!");
            alert.show();
        } else {
            User userToBeAdded = userService.addUser(new User(firstName, lastName));
            Long idUser = userToBeAdded.getId();
            UserCredentials userCredentialsToBeAdded = new UserCredentials(
                    username, PasswordCrypt.encryptPassword(password)
            );
            userCredentialsToBeAdded.setId(idUser);
            if (userCredentialsService.addUserCredentials(userCredentialsToBeAdded) != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "The username is already taken! Please choose another one!");
                alert.show();
                userService.deleteUser(idUser);
            } else {
                ProfilePhotoUser profilePhotoUser = new ProfilePhotoUser();
                profilePhotoUser.setId(idUser);
                profilePhotoUserService.addProfilePhotoUser(profilePhotoUser);
                anchorPaneLogin.setVisible(true);
                anchorPaneSignup.setVisible(false);
            }
        }
        textFieldFirstname.clear();
        textFieldLastname.clear();
        textFieldUsernameSignup.clear();
        passwordFieldSignup.clear();
    }

    /**
     * Event handler - logs in the User
     * @param keyCode KeyCode, representing the pressed Key
     * @throws IOException
     */
    private void escapeKeyPressed(KeyCode keyCode) throws IOException {
        if (keyCode == KeyCode.ENTER) {
            loginEvent();
        }
    }
}
