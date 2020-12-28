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
import socialnetwork.utils.ViewClass;

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
    private Stage loginStage;

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

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    public void setFriendshipRequestService(FriendshipRequestService friendshipRequestService) {
        this.friendshipRequestService = friendshipRequestService;
    }

    public void setProfilePhotoUserService(ProfilePhotoUserService profilePhotoUserService) {
        this.profilePhotoUserService = profilePhotoUserService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void setUserCredentialsService(UserCredentialsService userCredentialsService) {
        this.userCredentialsService = userCredentialsService;
    }

    public void setReplyMessageService(ReplyMessageService replyMessageService) {
        this.replyMessageService = replyMessageService;
    }

    public void setTextPostService(TextPostService textPostService) {
        this.textPostService = textPostService;
    }

    public void setPhotoPostService(PhotoPostService photoPostService) {
        this.photoPostService = photoPostService;
    }

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
            if (userCredentials.getPassword().equals(password)) {
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

    public void exit() {
        System.exit(0);
    }

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
                    photoPostService);
            accountUserControllerV2.setUserPage(loggedInUserPage);
            accountUserControllerV2.setAccountUserStage(accountUserStage);
            accountUserControllerV2.setLoginStage(loginStage);
//            AccountUserController accountUserController = loader.getController();
//            accountUserController.setAttributes(friendshipService, userService, friendshipRequestService, profilePhotoUserService,
//                    loggedInUser, accountUserStage, messageService, replyMessageService);
            loginStage.hide();
            accountUserStage.show();
            textFieldUsername.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void eventCreateAccount() {
        anchorPaneLogin.setVisible(false);
        anchorPaneSignup.setVisible(true);
        textFieldUsername.clear();
        passwordField.clear();
    }

    public void exitSignup() {
        anchorPaneSignup.setVisible(false);
        anchorPaneLogin.setVisible(true);
        textFieldFirstname.clear();
        textFieldLastname.clear();
        textFieldUsernameSignup.clear();
        passwordFieldSignup.clear();
    }

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
            UserCredentials userCredentialsToBeAdded = new UserCredentials(username, password);
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

    private void escapeKeyPressed(KeyCode keyCode) throws IOException {
        if (keyCode == KeyCode.ENTER) {
            loginEvent();
        }
    }
}
