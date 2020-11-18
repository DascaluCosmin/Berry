package socialnetwork.ui;

import socialnetwork.community.Communities;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.domain.messages.Message;
import socialnetwork.domain.messages.ReplyMessage;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.reader.*;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.ReplyMessageService;
import socialnetwork.service.UserService;
import socialnetwork.service.FriendshipRequestService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class UI {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final MessageService messageService;
    private final ReplyMessageService replyMessageService;
    private final FriendshipRequestService friendshipRequestService;

    /**
     * Constructor that creates a new UI
     * @param userService UserService, representing the Service that handles the User data
     * @param friendshipService FriendshipService, representing the Serivice that handles the Friendship data
     * @param messageService MessageService, representing the Service that handles the Message data
     * @param replyMessageService ReplyMessage, representing the Service that handles the ReplyMessage data
     * @param friendshipRequestService
     */
    public UI(UserService userService, FriendshipService friendshipService, MessageService messageService, ReplyMessageService replyMessageService, FriendshipRequestService friendshipRequestService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.replyMessageService = replyMessageService;
        this.friendshipRequestService = friendshipRequestService;
    }

    /**
     * Method that runs the User Interface
     */
    public void run(){
        int command;
        while(true) {
            try {
                System.out.println("0 - close the application");
                System.out.println("1 - add a new User");
                System.out.println("2 - delete User");
                System.out.println("3 - add a new Friendship");
                System.out.println("4 - delete Friendship");
                System.out.println("5 - print the number of communities");
                System.out.println("6 - print the most sociable community");
                System.out.println("7 - print the list of friends of some user");
                System.out.println("8 - print the list of friends of some user and the date they become friends");
                System.out.println("9 - print the list of friends of some user on some specific date");
                System.out.println("10 - send Message to many");
                System.out.println("11 - respond to messages");
                System.out.println("12 - add Conversation");
                System.out.println("13 - print Conversation");
                System.out.println("14 - send a new Friendship request");
                System.out.println("15 - respond to a friendship request");
                System.out.print("Introduce a command: ");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                command = Integer.parseInt(bufferedReader.readLine());
                switch (command) {
                    case 0:
                        return;
                    case 1:
                        this.userService.addUser(new ReaderUser().read());
                        System.out.println("Successful add!\n");
                        break;
                    case 2:
                        this.userService.deleteUser(new ReaderID().read());
                        System.out.println("Successful delete!\n");
                        break;
                    case 3:
                        this.friendshipService.addFriendship(new ReaderFriendship().read());
                        System.out.println("Successful add!\n");
                        break;
                    case 4:
                        this.friendshipService.deleteFriendship(new ReaderIDSFriendship().read());
                        System.out.println("Successful delete!\n");
                        break;
                    case 5:
                        Communities communitiesA = new Communities(friendshipService.getAll());
                        communitiesA.printNumberOfCommunities();
                        break;
                    case 6:
                        Communities communitiesB = new Communities(friendshipService.getAll());
                        communitiesB.printTheMostSociableCommunity();
                        break;
                    case 7:
                        printListOfFriends(new ReaderID().read());
                        break;
                    case 8:
                        printListOfFriendsWithDate(new ReaderID().read());
                        break;
                    case 9:
                        printListOfFriendsWithSpecificMonthDate(new ReaderID().read(), new ReaderMonth().read());
                        break;
                    case 10:
                        sendMessageToMany(new ReaderID().read(),
                                new ReaderIDSRecipients().read(), new ReaderTextMessage().read());
                        break;
                    case 11:
                        respondToMessages(new ReaderID().read());
                        break;
                    case 12:
                        addConversation(new ReaderID().read(), new ReaderID().read(), new ReaderTextMessage().read());
                        break;
                    case 13:
                        printConversation(new ReaderID().read(), new ReaderID().read());
                        break;
                    case 14:
                        sendFriendshipRequest(new ReaderID().read(), new ReaderID().read(), new ReaderTextMessage().read());
                        break;
                    case 15:
                        respondToFriendshipRequest(new ReaderID().read());
                        break;
                }
            } catch (ValidationException validationException) {
                System.err.println(validationException.getMessage());
            } catch (IllegalArgumentException illegalArgumentException) {
                System.err.println(illegalArgumentException);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method that allows the User to respond to its Messages
     * @param idUserReceiver Long, representing the ID of the User
     * @throws IOException
     */
    private void respondToMessages(Long idUserReceiver) throws IOException {
        User userReceiver = userService.getUser(idUserReceiver);
        if (userReceiver == null) {
            System.err.println("The id of the user is incorrect!");
            return;
        }
        Iterable<Message> listMessagesUser = messageService.getAllMessagesToUser(idUserReceiver);
        if (!listMessagesUser.iterator().hasNext()) {
            System.out.println("You have no received messages!");
            return;
        }
        System.out.println(userReceiver.getFirstName() + " " + userReceiver.getLastName() +
                " your received messages are:");
        listMessagesUser.forEach(user -> System.out.println(user.toStringFewer()));
        while(true) {
            Long idMessage = new ReaderIDMessage().read();
            Message messageToRespondTo = null;
            for (Message message : listMessagesUser) {
                if (message.getId().equals(idMessage)) {
                    messageToRespondTo = message;
                }
            }
            if (messageToRespondTo != null) {
                String textMessage = new ReaderTextMessage().read();
                User userSender = userReceiver; // The sender is now the receiver
                userReceiver = messageToRespondTo.getFrom(); // The receiver is now the sender
                messageService.addMessage(new Message(userSender, Arrays.asList(userReceiver), textMessage, LocalDateTime.now()));
                System.out.println("The message has been sent successfully!");
                break;
            }
            System.out.println("The message is not valid!");
        }
    }

    /**
     * Method that allows the User to respond to a Friendship Request
     * @param idUserResponds Long, representing the ID of the User that can respond to a Friendship Request
     * @throws IOException
     */
    private void respondToFriendshipRequest(Long idUserResponds) throws IOException {
        Iterable<FriendshipRequest> listPendingRequests = friendshipRequestService.getPendingRequests(idUserResponds);
        if (!listPendingRequests.iterator().hasNext()) {
            System.out.println("You have no pending friendship requests!");
            return;
        }
        User userThatResponds = userService.getUser(idUserResponds);
        System.out.println(userThatResponds.getFirstName() + " " + userThatResponds.getLastName() +
                ", your pending friendship requests are:");
        listPendingRequests.forEach(friendshipRequest -> {
            System.out.println(friendshipRequest.toStringFewer());
        });
        FriendshipRequest friendshipRequestToRespondTo;
        Long idFriendshipRequest;
        while(true) {
            idFriendshipRequest = new ReaderIDFriendshipRequest().read();
            friendshipRequestToRespondTo = friendshipRequestService.
                    getPendingFriendshipRequest(idUserResponds, idFriendshipRequest);
            if (friendshipRequestToRespondTo != null)
                break;
            System.out.println("The friendship request is not valid!");
        }
        String response = new ReaderResponseFriendshipRequest().read();
        if (response.equals("accept")) {
            Long idSender = friendshipRequestToRespondTo.getFrom().getId();
            Long idReceiver = friendshipRequestToRespondTo.getTo().get(0).getId();
            Friendship friendshipToBeAdded = new Friendship(new Tuple<>(idSender, idReceiver));
            friendshipService.addFriendship(friendshipToBeAdded);
            friendshipToBeAdded = new Friendship(new Tuple<>(idReceiver, idSender));
            friendshipService.addFriendship(friendshipToBeAdded);
            friendshipRequestService.deleteFriendshipRequest(idFriendshipRequest);
            friendshipRequestToRespondTo.setStatusRequest("accepted");
            friendshipRequestService.addFriendshipRequest(friendshipRequestToRespondTo);
        } else if (response.equals("decline")) {
            friendshipRequestService.deleteFriendshipRequest(idFriendshipRequest);
            friendshipRequestToRespondTo.setStatusRequest("declined");
            friendshipRequestService.addFriendshipRequest(friendshipRequestToRespondTo);
        }
    }

    /**
     * Method that sends a Friendship Request
     * @param idSender Long, representing the ID of the User who sends the Friendship Request
     * @param idReceiver Long, representing the ID of the User that gets the Friendship Requests
     * @param messageText String, representing the text message that comes with the Friendship Request
     */
    private void sendFriendshipRequest(Long idSender, Long idReceiver, String messageText) {
        User sender = userService.getUser(idSender);
        if (sender == null) {
            System.err.println("The id of the sender is incorrect!");
            return;
        }
        User receiver = userService.getUser(idReceiver);
        if (receiver == null) {
            System.err.println("The id of the receiver is incorrect!");
            return;
        }
        friendshipRequestService.addFriendshipRequest(new FriendshipRequest(sender, Arrays.asList(receiver),
                messageText, LocalDateTime.now(), "pending"));
        System.out.println("The friendship has been sent!");
    }

    /**
     * Method that prints all the conversions between two Users
     * @param idLeftUser Long, representing the ID of the User that initiated the conversion
     * @param idRightUser Long, representing the ID of the User that received the first message
     */
    private void printConversation(Long idLeftUser, Long idRightUser) {
        Iterable<ReplyMessage> conversation = replyMessageService.getConversation(idLeftUser, idRightUser);
        if (!conversation.iterator().hasNext())
            System.out.println("There's no conversation between the two!");
        conversation.forEach(System.out::println);
    }

    /**
     * Method that simulates a conversion between two Users
     * @param idSender Long, representing the ID of the User that initiates the conversion
     * @param idReceiver Long, representing the ID of the User that receives the message
     * @param messageText String, representing the sent message
     * @throws IOException
     */
    private void addConversation(Long idSender, Long idReceiver, String messageText) throws IOException {
        User sender = userService.getUser(idSender);
        if (sender == null) {
            System.err.println("The id of the sender is incorrect!");
            return;
        }
        User receiver = userService.getUser(idReceiver);
        if (receiver == null) {
            System.err.println("The id of the receiver is incorrect!");
            return;
        }
        ReplyMessage replyMessage = new ReplyMessage(sender, Arrays.asList(receiver), messageText,
                LocalDateTime.now(), replyMessageService.getReplyMessage(0L));
        replyMessageService.addMessage(replyMessage);
        while(true) {
            System.out.println("Do you want to continue the conversation? [Y/N]");
            String response = new ReaderResponse().read();
            if (response.matches("[yY]")) {
                User auxiliaryUser = sender;
                sender = receiver;
                receiver = auxiliaryUser;
                messageText = new ReaderTextMessage().read();
                replyMessage = new ReplyMessage(sender, Arrays.asList(receiver), messageText,
                        LocalDateTime.now(), replyMessageService.getReplyMessage(replyMessage.getId()));
                replyMessageService.addMessage(replyMessage);
            } else if (response.matches("[nN]"))
                break;
        }
    }

    /**
     * Method that sends a Message to multiple User recipients
     * @param idUserFrom Long, representing the ID of the sender
     * @param recipientsID List<Long>, representing the ids of the recipients
     * @param textMessage String, representing the text of the message
     */
    private void sendMessageToMany(Long idUserFrom,  List<Long> recipientsID, String textMessage){
        List<User> listTo = new ArrayList<>();
        recipientsID.stream()
                .forEach(id -> listTo.add(userService.getUser(id)));
        messageService.addMessage(new Message(userService.getUser(idUserFrom),listTo, textMessage, LocalDateTime.now()));
    }

    /**
     * Method that prints the list of all friends of some User
     * @param idUser Long, representing the id of the User whose list of friends is to be printed
     */
    private void printListOfFriends(Long idUser) {
        if (userService.getUser(idUser) == null) {
            System.err.println("The ID of the User is not valid");
            return;
        }
        Iterable<Friendship> friendshipsUser = friendshipService.getAllFriendshipsUser(idUser);
        System.out.print(userService.getUser(idUser).toStringFewer());
        if (!friendshipsUser.iterator().hasNext()) {
            System.out.println(", you have no added friends!");
            return;
        }
        System.out.println(", your added friends are:");
        friendshipsUser.forEach(friendship -> {
            System.out.println(userService.getUser(friendship.getId().getRight()).toStringFewer());
        });
    }

    /**
     * Method that prints the list of all friends of some User, along with the date they became friends
     * @param idUser Long, representing the id of the User whose list of friends is to be printed
     */
    private void printListOfFriendsWithDate(Long idUser) {
        List<Friendship> friendshipList = new ArrayList<>();
        Iterable<Friendship> friendshipIterable = friendshipService.getAll();
        friendshipIterable.forEach(friendshipList::add);
        AtomicBoolean exists = new AtomicBoolean(false);
        friendshipList.stream()
                .filter(friendship -> friendship.getId().getLeft().equals(idUser)
                        || friendship.getId().getRight().equals(idUser))
                .forEach(friendship -> {
                    exists.set(true);
                    User friendUser;
                    if (!idUser.equals(friendship.getId().getLeft()))
                        friendUser = userService.getUser(friendship.getId().getLeft());
                    else
                        friendUser = userService.getUser(friendship.getId().getRight());
                    System.out.println(friendUser.getFirstName() + "|" + friendUser.getLastName() + "|" + friendship.getDate());
                });
        if (!exists.get()) {
            System.out.println("There are no friendships!");
        }
    }

    /**
     * Method that prints the list of all friends of some User, along with the a specific month they became friends
     * @param idUser Long, representing the id of User whose list of friends is to be printe
     * @param month String, representing the month of the friendship
     */
    private void printListOfFriendsWithSpecificMonthDate(Long idUser, String month){
        List<Friendship> friendshipList = new ArrayList<>();
        Iterable<Friendship> friendshipIterable = friendshipService.getAll();
        friendshipIterable.forEach(friendshipList::add);
        AtomicBoolean exists = new AtomicBoolean(false);
        friendshipList.stream()
                .filter(friendship -> friendship.getId().getLeft().equals(idUser)
                        || friendship.getId().getRight().equals(idUser))
                .filter(friendship -> {
                    return friendship.getDate().toString().substring(5, 7).equals(month);
                })
                .forEach(friendship -> {
                    exists.set(true);
                    User friendUser;
                    if (!idUser.equals(friendship.getId().getLeft()))
                        friendUser = userService.getUser(friendship.getId().getLeft());
                    else
                        friendUser = userService.getUser(friendship.getId().getRight());
                    System.out.println(friendUser.getFirstName() + "|" + friendUser.getLastName() + "|" + friendship.getDate());
                });
        if (!exists.get()) {
            System.out.println("There are no friendships on that month!");
        }
    }
}