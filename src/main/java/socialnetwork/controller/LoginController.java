package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.ProfilePhotoUser;
import socialnetwork.domain.User;
import socialnetwork.domain.UserCredentials;
import socialnetwork.domain.UserDTO;
import socialnetwork.service.*;

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
    private Stage loginStage;

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
            loader.setLocation(getClass().getResource("/views/accountUser.fxml"));
            AnchorPane root = loader.load();

            Stage accountUserStage = new Stage();
            accountUserStage.setOnCloseRequest(event -> {
                loginStage.show();
            });
            accountUserStage.setScene(new Scene(root));
            accountUserStage.setTitle(loggedInUser.getFirstName() + " " + loggedInUser.getLastName() + "'s account");
            accountUserStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/berryLogo.jpg")));
            accountUserStage.setResizable(false);
            AccountUserController accountUserController = loader.getController();
            accountUserController.setAttributes(friendshipService, userService, friendshipRequestService, profilePhotoUserService,
                    loggedInUser, accountUserStage, messageService, replyMessageService);
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
        } else {
            User userToBeAdded = userService.addUser(new User(firstName, lastName));
            Long idUser = userToBeAdded.getId();
            UserCredentials userCredentialsToBeAdded = new UserCredentials(username, password);
            userCredentialsToBeAdded.setId(idUser);
            userCredentialsService.addUserCredentials(userCredentialsToBeAdded);
            ProfilePhotoUser profilePhotoUser = new ProfilePhotoUser();
            profilePhotoUser.setId(idUser);
            profilePhotoUserService.addProfilePhotoUser(profilePhotoUser);
            anchorPaneLogin.setVisible(true);
            anchorPaneSignup.setVisible(false);
        }
        textFieldFirstname.clear();
        textFieldLastname.clear();
        textFieldUsernameSignup.clear();
        passwordFieldSignup.clear();
    }
}
