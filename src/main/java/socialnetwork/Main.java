package socialnetwork;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.domain.messages.Message;
import socialnetwork.domain.messages.ReplyMessage;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.file.*;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.ReplyMessageService;
import socialnetwork.service.UserService;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.ui.UI;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
          //Configuration
        String fileNameUsers = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
        String fileNameFriendships = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.friendships");
        String fileNameMessage = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.messages");
        String fileNameConversation = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.conversation");
        String fileNameFriendshipRequests = ApplicationContext.getPROPERTIES()
                .getProperty("data.socialnetwork.friendshipRequests");

        // Repositories
        Repository<Long, User> userFileRepository = new UserFileRepository(fileNameUsers, new UserValidator());
        Repository<Tuple<Long, Long>, Friendship> friendshipFileRepository = new FriendshipFileRepository(fileNameFriendships,
                new FriendshipValidator(userFileRepository), userFileRepository);
        Repository<Long, Message> messageFileRepository = new MessagesFileRepository(fileNameMessage,
                new MessageValidator(), userFileRepository);
        Repository<Long, ReplyMessage> replyMessageFileRepository = new ReplyMessageFileRepository(fileNameConversation,
                new ValidatorReplyMessage(), userFileRepository);
        Repository<Long, FriendshipRequest> friendshipRequestFileRepository = new FriendshipRequestFileRepository(
                fileNameFriendshipRequests, new FriendshipRequestValidator(), userFileRepository);

        // Services
        UserService userService = new UserService(userFileRepository, friendshipFileRepository);
        FriendshipService friendshipService = new FriendshipService(friendshipFileRepository, userFileRepository);
        MessageService messageService = new MessageService(messageFileRepository);
        ReplyMessageService replyMessageService = new ReplyMessageService(replyMessageFileRepository);
        FriendshipRequestService friendshipRequestService = new FriendshipRequestService(friendshipRequestFileRepository,
                friendshipFileRepository);

        // UI
        UI ui = new UI(userService, friendshipService, messageService, replyMessageService, friendshipRequestService);
        ui.run();

//        // Final output
        messageFileRepository.findAll().forEach(System.out::println);
//        userFileRepository.findAll().forEach(System.out::println);
//        System.out.println();
//        friendshipFileRepository.findAll().forEach(System.out::println);
//        System.out.println();
//        replyMessageFileRepository.findAll().forEach(System.out::println);
//        System.out.println();
//        friendshipRequestFileRepository.findAll().forEach(System.out::println);
//        System.out.println();
    }
}


