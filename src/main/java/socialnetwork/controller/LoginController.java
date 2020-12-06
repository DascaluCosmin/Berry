package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.UserCredentials;
import socialnetwork.domain.UserDTO;
import socialnetwork.service.*;

import java.io.IOException;

public class LoginController {
    @FXML
    TextField textFieldUsername;
    @FXML
    PasswordField passwordField;

    private UserService userService;
    private FriendshipService friendshipService;
    private FriendshipRequestService friendshipRequestService;
    private ProfilePhotoUserService profilePhotoUserService;
    private MessageService messageService;
    private UserCredentialsService userCredentialsService;
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

    public void loginEvent() throws IOException {
        String username = textFieldUsername.getText();
        String password = passwordField.getText();
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
            AccountUserController accountUserController = loader.getController();
            accountUserController.setAttributes(friendshipService, userService, friendshipRequestService, profilePhotoUserService,
                    loggedInUser, accountUserStage, messageService);
            loginStage.hide();
            accountUserStage.show();
            textFieldUsername.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
