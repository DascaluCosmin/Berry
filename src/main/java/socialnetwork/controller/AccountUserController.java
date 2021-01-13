/**
 *
 *
 *  THIS CONTROLLER IS DEPRECATED. CHECK THE VERSION 2 OF THE ACCOUNT USER CONTROLLER
 *
 *
 */


package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.controller.imageViewController.ImageViewAccountUserController;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.ProfilePhotoUser;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.UserDTO;
import socialnetwork.service.*;
import socialnetwork.utils.ChangeProfilePhoto;
import socialnetwork.utils.ChangeProfilePhotoRectangle;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  THIS CONTROLLER IS DEPRECATED. CHECK THE VERSION 2 OF THE ACCOUNT USER CONTROLLER
 */
public class AccountUserController implements Observer<FriendshipChangeEvent>{
    private ObservableList<UserDTO> model = FXCollections.observableArrayList();
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendshipRequestService friendshipRequestService;
    private ProfilePhotoUserService profilePhotoUserService;
    private ReplyMessageService replyMessageService;
    private MessageService messageService;
    private UserDTO selectedUserDTO;
    private Stage accountUserStage;
    private ImageViewAccountUserController imageViewAccountUserController = new ImageViewAccountUserController();
    @FXML
    Button buttonAddFriendship;
    @FXML
    Button buttonDeleteFriendship;
    @FXML
    Label labelUserName;
    @FXML
    TableColumn<UserDTO, String> tableColumnFirstName;
    @FXML
    TableColumn<UserDTO, String> tableColumnLastName;
    @FXML
    TableView<UserDTO> tableViewAccountUser;
    @FXML
    Button buttonChangeProfilePhoto;
    @FXML
    ImageView profilePhotoImageView;

    @FXML
    void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableViewAccountUser.setItems(model);
    }

    /**
     * Method that sets the attributes of the Account User Controller
     * @param friendshipService
     * @param userService
     * @param friendshipRequestService
     * @param profilePhotoUserService
     * @param selectedUserDTO
     * @param accountUserStage
     * @param messageService
     */
    public void setAttributes(FriendshipService friendshipService, UserService userService,
                              FriendshipRequestService friendshipRequestService, ProfilePhotoUserService profilePhotoUserService,
                              UserDTO selectedUserDTO, Stage accountUserStage, MessageService messageService,
                              ReplyMessageService replyMessageService) {
        this.friendshipService = friendshipService;
        this.friendshipService.addObserver(this);
        this.userService = userService;
        this.friendshipRequestService = friendshipRequestService;
        this.profilePhotoUserService = profilePhotoUserService;
        this.messageService = messageService;
        this.selectedUserDTO = selectedUserDTO;
        this.accountUserStage = accountUserStage;
        this.replyMessageService = replyMessageService;

        // Set the ImageViewUserProfileController attributes
        imageViewAccountUserController.setProfilePhotoUserService(profilePhotoUserService);
        imageViewAccountUserController.setProfilePhotoImageView(profilePhotoImageView);
        imageViewAccountUserController.setUser(userService.getUser(selectedUserDTO.getId()));
        this.profilePhotoUserService.addObserver(imageViewAccountUserController);
        if (selectedUserDTO != null) {
            labelUserName.setText("Hello, " + selectedUserDTO.getFirstName());
            initModel();
            ChangeProfilePhoto changeProfilePhoto = new ChangeProfilePhotoRectangle();
            changeProfilePhoto.changeProfilePhoto(
                    profilePhotoUserService, profilePhotoImageView, userService.getUser(selectedUserDTO.getId())
            );
        }
    }

    private void initModel() {
        Iterable<Friendship> friendships = this.friendshipService.getAllFriendshipsUser(selectedUserDTO.getId());
        List<UserDTO> listFriends = new ArrayList();
        friendships.forEach(friendship -> listFriends.add(userService.getUserDTO(friendship.getId().getRight())));
        if (!friendships.iterator().hasNext()) {
            // Need model.setAll() because the setPlaceholder can't write over a model that has data
            model.setAll(listFriends);
            tableViewAccountUser.setPlaceholder(new Label("You have no added friends"));
        } else {
            model.setAll(listFriends);
        }
    }

    public void deleteFriendship() {
        UserDTO userDTO = tableViewAccountUser.getSelectionModel().getSelectedItem();
        if (userDTO != null) {
            Long selectedUserID = selectedUserDTO.getId();
            Long userID = userDTO.getId();
            friendshipService.deleteFriendship(new Tuple<>(selectedUserID, userID));
            friendshipService.deleteFriendship(new Tuple<>(userID, selectedUserID));
            tableViewAccountUser.getSelectionModel().clearSelection();
        }
    }

    public void addFriendshipRequest() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/addFriendshipView.fxml"));
            AnchorPane root = loader.load();

            Stage addFriendshipRequestStage = new Stage();
            addFriendshipRequestStage.setTitle("Send friendship requests");
            addFriendshipRequestStage.setResizable(false);
            addFriendshipRequestStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/berryLogo.jpg")));
            addFriendshipRequestStage.setOnCloseRequest(event -> accountUserStage.show());

            Scene scene = new Scene(root);
            addFriendshipRequestStage.setScene(scene);
            AddFriendshipViewController addFriendshipViewController = loader.getController();
            addFriendshipViewController.setFriendshipService(friendshipService);
            addFriendshipViewController.setUserService(userService, selectedUserDTO);
            addFriendshipViewController.setFriendshipRequestService(friendshipRequestService);

            accountUserStage.hide();
            addFriendshipRequestStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeProfilePhotoEvent() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\Users\\dasco\\OneDrive\\Pictures\\ProfilePhotos"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                new FileChooser.ExtensionFilter("JPG Files", "*.jpg"));
        fileChooser.setTitle("Choose Profile Photo");
        File file = fileChooser.showOpenDialog(accountUserStage);
        if (file != null) {
            String pathProfilePhoto = file.toString();
            ProfilePhotoUser newProfilePhotoUser = new ProfilePhotoUser(pathProfilePhoto);
            newProfilePhotoUser.setId(selectedUserDTO.getId());
            profilePhotoUserService.updateProfilePhotoUser(newProfilePhotoUser);
            ChangeProfilePhoto changeProfilePhoto = new ChangeProfilePhotoRectangle();
            changeProfilePhoto.changeProfilePhoto(
                    profilePhotoUserService, profilePhotoImageView, userService.getUser(selectedUserDTO.getId())
            );
        }
    }

    public void viewFriendshipRequests() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/friendshipRequestsView.fxml"));
            AnchorPane root = loader.load();

            Stage friendshipRequestsViewStage = new Stage();
            friendshipRequestsViewStage.setScene(new Scene(root));
            friendshipRequestsViewStage.setTitle("Friendship Requests");
            friendshipRequestsViewStage.setResizable(false);
            friendshipRequestsViewStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/berryLogo.jpg")));
            friendshipRequestsViewStage.setOnCloseRequest(event -> accountUserStage.show());

            FriendshipRequestsViewController friendshipRequestsViewController = loader.getController();
            friendshipRequestsViewController.setSelectedUser(selectedUserDTO);
            friendshipRequestsViewController.setFriendshipRequestService(friendshipRequestService);
            friendshipRequestsViewController.setFriendshipService(friendshipService);
            friendshipRequestsViewController.setUserService(userService);
            friendshipRequestsViewController.setProfilePhotoUserService(profilePhotoUserService);

            accountUserStage.hide();
            friendshipRequestsViewStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showUserProfile() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/userProfileView.fxml"));
            AnchorPane root = loader.load();

            Stage userProfileStage = new Stage();
            userProfileStage.setScene(new Scene(root));
            userProfileStage.setResizable(false);
            userProfileStage.setTitle(selectedUserDTO.getFirstName() + " " + selectedUserDTO.getLastName() + " profile");
            userProfileStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/berryLogo.jpg")));
            userProfileStage.setOnCloseRequest(event -> accountUserStage.show());

            UserProfileController userProfileController = loader.getController();
            userProfileController.setUser(userService.getUser(selectedUserDTO.getId()));
            userProfileController.setProfilePhotoUserService(profilePhotoUserService);
            userProfileController.setUserService(userService);
            userProfileController.setUserProfileStage(userProfileStage);
            userProfileController.initializeUserProfile();

            accountUserStage.hide();
            userProfileStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void eventShowMessages() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/messageView.fxml"));

            AnchorPane root = loader.load();
            Stage messageViewStage = new Stage();
            messageViewStage.setScene(new Scene(root));
            messageViewStage.setTitle("Your messages");
            messageViewStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/berryLogo.jpg")));
            messageViewStage.setResizable(false);
            messageViewStage.setOnCloseRequest(event -> accountUserStage.show());
            MessageController messageController = loader.getController();
            messageController.setSelectedUserDTO(selectedUserDTO);
            messageController.setFriendshipService(friendshipService);
            messageController.setUserService(userService);
            messageController.setMessageService(messageService);
            accountUserStage.hide();
            messageViewStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void eventConversation() {
        UserDTO conversationUserDTO = tableViewAccountUser.getSelectionModel().getSelectedItem();
        if (conversationUserDTO != null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/chatView.fxml"));
                AnchorPane root = loader.load();
                Stage conversationStage = new Stage();
                conversationStage.setScene(new Scene(root));
                conversationStage.setResizable(false);
                conversationStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/berryLogo.jpg")));
                conversationStage.setTitle("Chat with " + conversationUserDTO.getFirstName() + " " + conversationUserDTO.getLastName());
                conversationStage.setOnCloseRequest(event -> {
                    accountUserStage.show();
                    tableViewAccountUser.getSelectionModel().clearSelection();
                });
                ChatViewController chatViewController = loader.getController();
                chatViewController.setStages(conversationStage, accountUserStage);
                chatViewController.setLoggedInUser(selectedUserDTO);
                chatViewController.setSelectedUserForConversation(conversationUserDTO);
                chatViewController.setUserService(userService);
                chatViewController.setReplyMessageService(replyMessageService);
                accountUserStage.hide();
                conversationStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select an User to start a conversation with!");
            alert.show();
        }
    }

    public void viewStatistics() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/statsView.fxml"));
            AnchorPane root = loader.load();
            Stage statisticsStage = new Stage();
            statisticsStage.setScene(new Scene(root));
            statisticsStage.setResizable(false);
            statisticsStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/berryLogo.jpg")));
            statisticsStage.setTitle(selectedUserDTO.getFirstName() + " " + selectedUserDTO.getLastName() + "'s statistics");
            statisticsStage.setOnCloseRequest(event -> {
                accountUserStage.show();
            });
            StatsController statsController = loader.getController();
            statsController.setSelectedUserDTO(selectedUserDTO);
            statsController.setMessageService(messageService);
            statsController.setFriendshipService(friendshipService);
            accountUserStage.hide();
            statisticsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        initModel();
    }
}
