package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.ProfilePhotoUserService;
import socialnetwork.service.UserService;
import socialnetwork.utils.ChangeProfilePhoto;
import socialnetwork.utils.ChangeProfilePhotoRectangle;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AccountUserController implements Observer<FriendshipChangeEvent>{
    ObservableList<UserDTO> model = FXCollections.observableArrayList();
    UserService userService;
    FriendshipService friendshipService;
    FriendshipRequestService friendshipRequestService;
    ProfilePhotoUserService profilePhotoUserService;
    UserDTO selectedUserDTO;
    Stage accountUserStage;
    ImageViewAccountUserController imageViewAccountUserController = new ImageViewAccountUserController();
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

    public void setAttributes(FriendshipService friendshipService, UserService userService,
                              FriendshipRequestService friendshipRequestService, ProfilePhotoUserService profilePhotoUserService,
                              UserDTO selectedUserDTO, Stage accountUserStage) {
        this.friendshipService = friendshipService;
        this.friendshipService.addObserver(this);
        this.userService = userService;
        this.friendshipRequestService = friendshipRequestService;
        this.profilePhotoUserService = profilePhotoUserService;
        this.selectedUserDTO = selectedUserDTO;
        this.accountUserStage = accountUserStage;

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
            addFriendshipRequestStage.initModality(Modality.APPLICATION_MODAL);
            addFriendshipRequestStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/berryLogo.jpg")));

            Scene scene = new Scene(root);
            addFriendshipRequestStage.setScene(scene);
            AddFriendshipViewController addFriendshipViewController = loader.getController();
            addFriendshipViewController.setFriendshipService(friendshipService);
            addFriendshipViewController.setUserService(userService, selectedUserDTO);
            addFriendshipViewController.setFriendshipRequestService(friendshipRequestService);

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
            friendshipRequestsViewStage.initModality(Modality.APPLICATION_MODAL);
            friendshipRequestsViewStage.setResizable(false);
            friendshipRequestsViewStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/berryLogo.jpg")));
            FriendshipRequestsViewController friendshipRequestsViewController = loader.getController();
            friendshipRequestsViewController.setSelectedUser(selectedUserDTO);
            friendshipRequestsViewController.setFriendshipRequestService(friendshipRequestService);
            friendshipRequestsViewController.setFriendshipService(friendshipService);
            friendshipRequestsViewController.setUserService(userService);
            friendshipRequestsViewController.setProfilePhotoUserService(profilePhotoUserService);

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

            UserProfileController userProfileController = loader.getController();
            userProfileController.setUser(userService.getUser(selectedUserDTO.getId()));
            userProfileController.setProfilePhotoUserService(profilePhotoUserService);
            userProfileController.setUserService(userService);
            userProfileController.setUserProfileStage(userProfileStage);
            userProfileController.initializeUserProfile();

            userProfileStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        initModel();
    }
}
