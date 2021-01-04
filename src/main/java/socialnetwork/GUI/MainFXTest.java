package socialnetwork.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import socialnetwork.service.ReplyMessageService;
import socialnetwork.service.UserService;

public class MainFXTest extends Application {
    private static ReplyMessageService replyMessageService;
    private static UserService userService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/accountUserV2.fxml"));

        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }

    public static void main(String[] args) {
//        String fileNameFriendships = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.friendships");
//        String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
//        String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
//        String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
//        Repository<Long, User> userRepository = new UserDBRepository(url, username, password);
//        Repository<Tuple<Long, Long>, Friendship> friendshipFileRepository = new FriendshipFileRepository(fileNameFriendships,
//                new FriendshipValidator(userRepository), userRepository);
//        userService = new UserService(userRepository, friendshipFileRepository);
//        ReplyMessageDBRepository replyMessageRepository = new ReplyMessageDBRepository(url, username, password, userRepository);
//        replyMessageService = new ReplyMessageService(replyMessageRepository);
        launch(args);
    }
}
