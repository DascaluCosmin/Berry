package socialnetwork.GUI;

import com.sun.org.apache.bcel.internal.generic.FMUL;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
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
    private double xOffset = 0;
    private double yOffset = 0;

    private static UserService userService;
    private static FriendshipService friendshipService;
    private static MessageService messageService;
    private static ReplyMessageService replyMessageService;
    private static FriendshipRequestService friendshipRequestService;
    private static ProfilePhotoUserService profilePhotoUserService;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/login.fxml"));
        initView(primaryStage, loader);

        LoginController loginController = loader.getController();
        loginController.setFriendshipRequestService(friendshipRequestService);
        loginController.setFriendshipService(friendshipService);
        loginController.setUserService(userService);
        loginController.setProfilePhotoUserService(profilePhotoUserService);
        loginController.setMessageService(messageService);
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

    private void initView(Stage primaryStage, FXMLLoader loader) throws IOException {
        Parent root = loader.load();
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/berryLogo.jpg")));
        primaryStage.setScene(scene);
    }
}
