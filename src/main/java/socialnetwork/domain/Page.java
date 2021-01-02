package socialnetwork.domain;

import socialnetwork.service.*;

public class Page {
    private UserDTO userDTO;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendshipRequestService friendshipRequestService;
    private ProfilePhotoUserService profilePhotoUserService;
    private UserCredentialsService userCredentialsService;
    private ReplyMessageService replyMessageService;
    private MessageService messageService;
    private TextPostService textPostService;
    private PhotoPostService photoPostService;
    private EventsService eventsService;

    /**
     * Constructor that creates a new Page
     * @param userDTO UserDTO, representing the User linked to the Page
     * @param userService UserService, representing the Service concerned with the Users data
     * @param friendshipService FriendshipService, representing the Service concerned with the Friendships data
     * @param friendshipRequestService FriendshipRequestService, representing the Service
*                                 concerned with the Friendship Requests data
     * @param profilePhotoUserService ProfilePhotoUserService, representing the Service concerned with the Profile Photos data
     * @param userCredentialsService UserCredentialsService, representing the Service concerned with the User Credentials data
     * @param replyMessageService ReplyMessageService, representing the Service concerned with the Reply Messages data
     * @param messageService MessageService, representing the Service concerned with the Messages data
     * @param textPostService TextPostService, representing the Service concerned with the Text Posts data
     * @param photoPostService PhotoPostService, representing the Service concerned with the Photo Posts data
     * @param eventsService EventsService, representing the Service concerned with the Events data
     */
    public Page(UserDTO userDTO, UserService userService, FriendshipService friendshipService,
                FriendshipRequestService friendshipRequestService, ProfilePhotoUserService profilePhotoUserService,
                UserCredentialsService userCredentialsService, ReplyMessageService replyMessageService,
                MessageService messageService, TextPostService textPostService, PhotoPostService photoPostService,
                EventsService eventsService) {
        this.userDTO = userDTO;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendshipRequestService = friendshipRequestService;
        this.profilePhotoUserService = profilePhotoUserService;
        this.userCredentialsService = userCredentialsService;
        this.replyMessageService = replyMessageService;
        this.messageService = messageService;
        this.textPostService = textPostService;
        this.photoPostService = photoPostService;
        this.eventsService = eventsService;
    }

    /**
     * @param user UserDTO, representing the new User linked to the Page
     */
    public void setUser(UserDTO user) {
        this.userDTO = user;
    }

    /**
     * @return UserDTO, representing the UserDTO linked to the Page
     */
    public UserDTO getUserDTO() {
        return userDTO;
    }

    /**
     * @return User, representing the User linked to the Page
     */
    public User getUser() {
        return userService.getUser(userDTO.getId());
    }

    /**
     * @return UserService, representing the Service concerned with the Users data
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * @return FriendshipService, representing the Service concerned with the Friendships data
     */
    public FriendshipService getFriendshipService() {
        return friendshipService;
    }

    /**
     * @return FriendshipRequestService, representing the Service concerned with the Friendship Requests data
     */
    public FriendshipRequestService getFriendshipRequestService() {
        return friendshipRequestService;
    }

    /**
     * @return ProfilePhotoUserService, representing the Service concerned with the Profile Photos data
     */
    public ProfilePhotoUserService getProfilePhotoUserService() {
        return profilePhotoUserService;
    }

    /**
     * @return UserCredentialsService, representing the Service concerned with the User Credentials data
     */
    public UserCredentialsService getUserCredentialsService() {
        return userCredentialsService;
    }

    /**
     * @return ReplyMessageService, representing the Service concerned with the Reply Messages data
     */
    public ReplyMessageService getReplyMessageService() {
        return replyMessageService;
    }

    /**
     * @return MessageService, representing the Service concerned with the Messages data
     */
    public MessageService getMessageService() {
        return messageService;
    }

    /**
     * @return TextPostService, representing the Service concerned with the Test Posts data
     */
    public TextPostService getTextPostService() {
        return textPostService;
    }

    /**
     * @return PhotoPostService, representing the Service concerned with the Photo Posts data
     */
    public PhotoPostService getPhotoPostService() {
        return photoPostService;
    }

    /**
     * @return EventsService, representing the Service concerned with the Events data
     */
    public EventsService getEventsService() {
        return eventsService;
    }


}
