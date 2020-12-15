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
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.util.ResourceUtils;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.IntroductionController;
import socialnetwork.controller.LoginController;
import socialnetwork.domain.*;
import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.domain.messages.Message;
import socialnetwork.domain.messages.ReplyMessage;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.repository.file.*;
import socialnetwork.service.*;
import socialnetwork.utils.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainFX extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

    private static UserService userService;
    private static FriendshipService friendshipService;
    private static MessageService messageService;
    private static ReplyMessageService replyMessageService;
    private static FriendshipRequestService friendshipRequestService;
    private static ProfilePhotoUserService profilePhotoUserService;
    private static UserCredentialsService userCredentialsService;

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
        loginController.setUserCredentialsService(userCredentialsService);
        loginController.setReplyMessageService(replyMessageService);
        primaryStage.show();
    }

    public static void main(String[] args) {
        //Configuration
        String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
        String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
        String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
        // Repositories
        Repository<Long, User> userRepository = new UserDBRepository(url, username, password);
        Repository<Tuple<Long, Long>, Friendship> friendshipRepository = new FriendshipDBRepository(url, username, password);
        MessagesDBRepository messageRepository = new MessagesDBRepository(url, username, password, userRepository);
        ReplyMessageDBRepository replyMessageRepository = new ReplyMessageDBRepository(url, username, password, userRepository);
        Repository<Long, FriendshipRequest> friendshipRequestDBRepository = new FriendshipRequestsDBRepository(url, username,
                password, userRepository);
        Repository<Long, ProfilePhotoUser> profilePhotoUserRepository = new ProfilePhotoUserDBRepository(url, username, password);
        UserCredentialsDBRepository userCredentialsRepository = new UserCredentialsDBRepository(url, username, password);

        // Services
        userService = new UserService(userRepository, friendshipRepository);
        friendshipService = new FriendshipService(friendshipRepository, userRepository);
        messageService = new MessageService(messageRepository);
        replyMessageService = new ReplyMessageService(replyMessageRepository);
        friendshipRequestService = new FriendshipRequestService(friendshipRequestDBRepository,
                friendshipRepository);
        profilePhotoUserService = new ProfilePhotoUserService(profilePhotoUserRepository);
        userCredentialsService = new UserCredentialsService(userCredentialsRepository);
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
