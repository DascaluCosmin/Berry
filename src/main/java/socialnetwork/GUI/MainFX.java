package socialnetwork.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.LoginController;
import socialnetwork.domain.*;
import socialnetwork.domain.posts.Post;
import socialnetwork.domain.posts.PostType;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.repository.database.event.EventDBRepository;
import socialnetwork.repository.database.event.ParticipantDBRepository;
import socialnetwork.repository.database.friendshipRequests.FriendshipRequestsDBRepository;
import socialnetwork.repository.database.messages.MessagesDBRepository;
import socialnetwork.repository.database.messages.ReplyMessageDBRepository;
import socialnetwork.repository.database.userPosts.PhotoPostDBRepository;
import socialnetwork.repository.database.userPosts.TextPostDBRepository;
import socialnetwork.service.*;
import socialnetwork.service.postsServices.PostLikesService;
import socialnetwork.service.postsServices.PhotoPostService;
import socialnetwork.service.postsServices.TextPostService;
import socialnetwork.utils.ViewClass;

import java.io.IOException;

public class MainFX extends Application {
    private static UserService userService;
    private static FriendshipService friendshipService;
    private static MessageService messageService;
    private static ReplyMessageService replyMessageService;
    private static FriendshipRequestService friendshipRequestService;
    private static ProfilePhotoUserService profilePhotoUserService;
    private static UserCredentialsService userCredentialsService;
    private static TextPostService textPostService;
    private static PhotoPostService photoPostService;
    private static EventsService eventsService;
    private static PostLikesService photoPostLikesService;
    private static PostLikesService textPostLikesService;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/login.fxml"));

        ViewClass viewClass = new ViewClass();
        viewClass.initView(primaryStage, loader);

        LoginController loginController = loader.getController();
        loginController.setFriendshipRequestService(friendshipRequestService);
        loginController.setFriendshipService(friendshipService);
        loginController.setUserService(userService);
        loginController.setProfilePhotoUserService(profilePhotoUserService);
        loginController.setMessageService(messageService);
        loginController.setLoginStage(primaryStage);
        loginController.setUserCredentialsService(userCredentialsService);
        loginController.setReplyMessageService(replyMessageService);
        loginController.setTextPostService(textPostService);
        loginController.setPhotoPostService(photoPostService);
        loginController.setEventsService(eventsService);
        loginController.setPhotoPostLikesService(photoPostLikesService);
        loginController.setTextPostLikesService(textPostLikesService);
        primaryStage.show();
    }

    public static void main(String[] args) {
        //Configuration
        String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
        String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
        String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
        // Repositories
        UserDBRepository userRepository = new UserDBRepository(url, username, password);
        FriendshipDBRepository friendshipRepository = new FriendshipDBRepository(url, username, password);
        MessagesDBRepository messageRepository = new MessagesDBRepository(url, username, password, userRepository);
        ReplyMessageDBRepository replyMessageRepository = new ReplyMessageDBRepository(url, username, password, userRepository);
        FriendshipRequestsDBRepository friendshipRequestDBRepository = new FriendshipRequestsDBRepository(url, username,
                password, userRepository);
        Repository<Long, ProfilePhotoUser> profilePhotoUserRepository = new ProfilePhotoUserDBRepository(url, username, password);
        UserCredentialsDBRepository userCredentialsRepository = new UserCredentialsDBRepository(url, username, password);
        TextPostDBRepository textPostDBRepository = new TextPostDBRepository(url, username, password);
        PhotoPostDBRepository photoPostDBRepository = new PhotoPostDBRepository(url, username, password);
        EventDBRepository eventDBRepository = new EventDBRepository(url, username, password, userRepository);
        ParticipantDBRepository participantDBRepository = new ParticipantDBRepository(url, username, password);
        PostLikesDBRepository photoPostsLikesRepository = new PostLikesDBRepository(url, username, password, PostType.PHOTO);
        PostLikesDBRepository textPostsLikesRepository = new PostLikesDBRepository(url, username, password, PostType.TEXT);

        // Services
        userService = new UserService(userRepository, friendshipRepository);
        friendshipService = new FriendshipService(friendshipRepository, userRepository);
        messageService = new MessageService(messageRepository);
        replyMessageService = new ReplyMessageService(replyMessageRepository);
        friendshipRequestService = new FriendshipRequestService(friendshipRequestDBRepository,
                friendshipRepository);
        profilePhotoUserService = new ProfilePhotoUserService(profilePhotoUserRepository);
        userCredentialsService = new UserCredentialsService(userCredentialsRepository);
        textPostService = new TextPostService(textPostDBRepository);
        photoPostService = new PhotoPostService(photoPostDBRepository);
        eventsService = new EventsService(eventDBRepository, participantDBRepository);
        photoPostLikesService = new PostLikesService(photoPostsLikesRepository);
        textPostLikesService = new PostLikesService(textPostsLikesRepository);
        launch(args);
    }
}
