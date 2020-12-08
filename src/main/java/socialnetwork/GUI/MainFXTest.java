package socialnetwork.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.ChatViewController;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.UserDTO;
import socialnetwork.domain.messages.ReplyMessage;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.ValidatorReplyMessage;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.ReplyMessageDBRepository;
import socialnetwork.repository.database.UserDBRepository;
import socialnetwork.repository.file.FriendshipFileRepository;
import socialnetwork.repository.file.ReplyMessageFileRepository;
import socialnetwork.service.ReplyMessageService;
import socialnetwork.service.UserService;

public class MainFXTest extends Application {
    private static ReplyMessageService replyMessageService;
    private static UserService userService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/chatView.fxml"));

        AnchorPane root = loader.load();
        primaryStage.setScene(new Scene(root));
        ChatViewController chatViewController = loader.getController();
        UserDTO loggedInUserDTO = new UserDTO("Sergiu", "Breaban");
        loggedInUserDTO.setId(1L);
        chatViewController.setLoggedInUser(loggedInUserDTO);

        UserDTO selectedUserForConversationDTO = new UserDTO("Cosmin", "Dascalu");
        selectedUserForConversationDTO.setId(3L);
        chatViewController.setSelectedUserForConversation(selectedUserForConversationDTO);
        chatViewController.setUserService(userService);
        chatViewController.setReplyMessageService(replyMessageService);
        primaryStage.show();
    }

    public static void main(String[] args) {
        String fileNameFriendships = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.friendships");
        String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
        String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
        String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
        Repository<Long, User> userRepository = new UserDBRepository(url, username, password);
        Repository<Tuple<Long, Long>, Friendship> friendshipFileRepository = new FriendshipFileRepository(fileNameFriendships,
                new FriendshipValidator(userRepository), userRepository);
        userService = new UserService(userRepository, friendshipFileRepository);
        Repository<Long, ReplyMessage> replyMessageRepository = new ReplyMessageDBRepository(url, username, password, userRepository);
        replyMessageService = new ReplyMessageService(replyMessageRepository);
        launch(args);
    }
}
