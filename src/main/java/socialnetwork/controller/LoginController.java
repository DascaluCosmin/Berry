package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.service.*;

import java.io.IOException;

public class LoginController {

    @FXML
    Button buttonLogin;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendshipRequestService friendshipRequestService;
    private ProfilePhotoUserService profilePhotoUserService;
    private MessageService messageService;
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

    public void loginEvent() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/introduction.fxml"));
        AnchorPane layout = loader.load();
        Stage introductionStage = new Stage();
        introductionStage.setScene(new Scene(layout));
        introductionStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/berryLogo.jpg")));
        introductionStage.setTitle("Berry!");
        introductionStage.setOnCloseRequest(event -> {
            loginStage.show();
        });
        IntroductionController introductionController = loader.getController();
        introductionController.setUserService(userService, introductionStage);
        introductionController.setFriendshipService(friendshipService);
        introductionController.setFriendshipRequestService(friendshipRequestService);
        introductionController.setProfilePhotoUserService(profilePhotoUserService);
        introductionController.setMessageService(messageService);
        loginStage.hide();
        introductionStage.show();
    }

    public void exit() {
        System.exit(0);
    }
}
