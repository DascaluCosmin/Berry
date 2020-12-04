package socialnetwork.GUI;

import com.sun.org.apache.bcel.internal.generic.FMUL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jdk.internal.loader.Loader;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.IntroductionController;
import socialnetwork.controller.LoginController;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.ProfilePhotoUser;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.domain.messages.Message;
import socialnetwork.domain.messages.ReplyMessage;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.UserDBRepository;
import socialnetwork.repository.file.*;
import socialnetwork.service.*;

import java.io.IOException;

public class MainFX extends Application {
    private static UserService userService;
    private static FriendshipService friendshipService;
    private static MessageService messageService;
    private static ReplyMessageService replyMessageService;
    private static FriendshipRequestService friendshipRequestService;
    private static ProfilePhotoUserService profilePhotoUserService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/login.fxml"));
        Parent root = loader.load();
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        LoginController loginController = loader.getController();
        loginController.setFriendshipRequestService(friendshipRequestService);
        loginController.setFriendshipService(friendshipService);
        loginController.setUserService(userService);
        loginController.setProfilePhotoUserService(profilePhotoUserService);
        loginController.setLoginStage(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        //Configuration
//        String fileNameUsers = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
        String fileNameFriendships = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.friendships");
        String fileNameMessage = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.messages");
        String fileNameConversation = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.conversation");
        String fileNameFriendshipRequests = ApplicationContext.getPROPERTIES()
                .getProperty("data.socialnetwork.friendshipRequests");
        String fileNameUserProfilePhotos = ApplicationContext.getPROPERTIES().
                getProperty("data.socialnetwork.userProfilePhotos");
        String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
        String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
        String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
        // Repositories
//        Repository<Long, User> userFileRepository = new UserFileRepository(fileNameUsers, new UserValidator());
        Repository<Long, User> userRepository = new UserDBRepository(url, username, password, new UserValidator());
        Repository<Tuple<Long, Long>, Friendship> friendshipFileRepository = new FriendshipFileRepository(fileNameFriendships,
                new FriendshipValidator(userRepository), userRepository);
        Repository<Long, Message> messageFileRepository = new MessagesFileRepository(fileNameMessage,
                new MessageValidator(), userRepository);
        Repository<Long, ReplyMessage> replyMessageFileRepository = new ReplyMessageFileRepository(fileNameConversation,
                new ValidatorReplyMessage(), userRepository);
        Repository<Long, FriendshipRequest> friendshipRequestFileRepository = new FriendshipRequestFileRepository(
                fileNameFriendshipRequests, new FriendshipRequestValidator(), userRepository);
        Repository<Long, ProfilePhotoUser> profilePhotoUserFileRepository = new ProfilePhotoUserFileRepository(
                fileNameUserProfilePhotos, new ValidatorProfilePhotoUser());

        // Services
        userService = new UserService(userRepository, friendshipFileRepository);
        friendshipService = new FriendshipService(friendshipFileRepository, userRepository);
        messageService = new MessageService(messageFileRepository);
        replyMessageService = new ReplyMessageService(replyMessageFileRepository);
        friendshipRequestService = new FriendshipRequestService(friendshipRequestFileRepository,
                friendshipFileRepository);
        profilePhotoUserService = new ProfilePhotoUserService(profilePhotoUserFileRepository);
        launch(args);
    }
}
