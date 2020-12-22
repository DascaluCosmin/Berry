package socialnetwork.domain;

import socialnetwork.service.*;

public class Page {
    private UserDTO user;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendshipRequestService friendshipRequestService;
    private ProfilePhotoUserService profilePhotoUserService;
    private UserCredentialsService userCredentialsService;
    private ReplyMessageService replyMessageService;
    private MessageService messageService;

    /**
     * Constructor that creates a new Page
     * @param user UserDTO, representing the User linked to the Page
     * @param userService UserService, representing the Service concerned with the Users data
     * @param friendshipService FriendshipService, representing the Service concerned with the Friendships data
     * @param friendshipRequestService FriendshipRequestService, representing the Service
     *                                 concerned with the Friendship Requests data
     * @param profilePhotoUserService ProfilePhotoUserService, representing the Service concerned with the Profile Photos data
     * @param userCredentialsService UserCredentialsService, representing the Service concerned with the User Credentials data
     * @param replyMessageService ReplyMessageService, representing the Service concerned with the Reply Messages data
     * @param messageService MessageService, representing the Service concerned with the Messages data
     */
    public Page(UserDTO user, UserService userService, FriendshipService friendshipService,
                FriendshipRequestService friendshipRequestService, ProfilePhotoUserService profilePhotoUserService,
                UserCredentialsService userCredentialsService,
                ReplyMessageService replyMessageService, MessageService messageService) {
        this.user = user;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendshipRequestService = friendshipRequestService;
        this.profilePhotoUserService = profilePhotoUserService;
        this.userCredentialsService = userCredentialsService;
        this.replyMessageService = replyMessageService;
        this.messageService = messageService;
    }

    /**
     * @param user UserDTO, representing the new User linked to the Page
     */
    public void setUser(UserDTO user) {
        this.user = user;
    }

    /**
     * @return UserDTO, representing the User linked to the Page
     */
    public UserDTO getUser() {
        return user;
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
}
