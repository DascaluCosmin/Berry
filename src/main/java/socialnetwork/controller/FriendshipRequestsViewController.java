package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.UserDTO;
import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.ProfilePhotoUserService;
import socialnetwork.service.UserService;
import socialnetwork.utils.events.FriendshipRequestChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.util.ArrayList;

public class FriendshipRequestsViewController implements Observer<FriendshipRequestChangeEvent> {
    UserDTO selectedUserDTO;
    FriendshipRequestService friendshipRequestService;
    FriendshipService friendshipService;
    UserService userService;
    ProfilePhotoUserService profilePhotoUserService;
    ObservableList<FriendshipRequest> model = FXCollections.observableArrayList();

    @FXML
    Button buttonViewUserProfile;
    @FXML
    Button buttonAcceptFriendshipRequest;
    @FXML
    Button buttonDeclineFriendshipRequest;
    @FXML
    Button buttonUnsend;
    @FXML
    TableView<FriendshipRequest> tableViewFriendshipRequests;
    @FXML
    TableColumn<FriendshipRequest, String> tableColumnFirstName;
    @FXML
    TableColumn<FriendshipRequest, String> tableColumnLastName;
    @FXML
    TableColumn<FriendshipRequest, String> tableColumnMessage;
    @FXML
    TableColumn<FriendshipRequest, String> tableColumnSentDate;
    @FXML
    TableColumn<FriendshipRequest, String> tableColumnStatus;
    @FXML
    TableColumn<TableColumn<Friendship, String>, TableColumn<Friendship, String>> tableColumnHeadFromTo;

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory("firstNameFrom"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory("lastNameFrom"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory("message"));
        tableColumnSentDate.setCellValueFactory(new PropertyValueFactory("formatedDate"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory("statusRequest"));
        tableViewFriendshipRequests.setItems(model);
    }

    public void setFriendshipRequestService(FriendshipRequestService friendshipRequestService) {
        this.friendshipRequestService = friendshipRequestService;
        this.friendshipRequestService.addObserver(this);
        initModel();
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    public void setProfilePhotoUserService(ProfilePhotoUserService profilePhotoUserService) {
        this.profilePhotoUserService = profilePhotoUserService;
    }

    private void initModel() {
        if (tableColumnHeadFromTo.getText().equals("From")) {
            model.setAll(friendshipRequestService.getFriendshipRequestsUser(selectedUserDTO.getId()));
        } else if (tableColumnHeadFromTo.getText().equals("To")) {
            model.setAll(friendshipRequestService.getFriendshipRequestsUserFrom(selectedUserDTO.getId()));
        }
    }

    public void setSelectedUser(UserDTO selectedUserDTO) {
        this.selectedUserDTO = selectedUserDTO;
    }

    public void acceptPendingFriendshipRequest() {
        FriendshipRequest selectedFriendshipRequest = tableViewFriendshipRequests.getSelectionModel().getSelectedItem();
        if (selectedFriendshipRequest != null) {
            if (selectedFriendshipRequest.getStatusRequest().equals("pending")) {
                friendshipRequestService.updateFriendshipRequest(selectedFriendshipRequest, "accepted");
                friendshipService.addFriendship(new Friendship(new Tuple(selectedFriendshipRequest.getFrom().getId(), selectedUserDTO.getId())));
                friendshipService.addFriendship(new Friendship(new Tuple(selectedUserDTO.getId(), selectedFriendshipRequest.getFrom().getId())));
            } else if (selectedFriendshipRequest.getStatusRequest().equals("accepted")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "The friendship request has already been accepted");
                alert.show();
            } else if (selectedFriendshipRequest.getStatusRequest().equals("declined")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "The friendship request has already been declined");
                alert.show();
            }
        }
    }

    public void declinePendingFriendshipRequest() {
        FriendshipRequest selectedFriendshipRequest = tableViewFriendshipRequests.getSelectionModel().getSelectedItem();
        if (selectedFriendshipRequest != null) {
            if (selectedFriendshipRequest.getStatusRequest().equals("pending")) {
                friendshipRequestService.updateFriendshipRequest(selectedFriendshipRequest, "declined");
            } else if (selectedFriendshipRequest.getStatusRequest().equals("accepted")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "The friendship request has already been accepted");
                alert.show();
            } else if (selectedFriendshipRequest.getStatusRequest().equals("declined")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "The friendship request has already been declined");
                alert.show();
            }
        }
    }

    public void showUserProfile() {
        FriendshipRequest selectedFriendshipRequest = tableViewFriendshipRequests.getSelectionModel().getSelectedItem();
        if (selectedFriendshipRequest != null) {
            User userFrom = selectedFriendshipRequest.getFrom();
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/userProfileView.fxml"));
                AnchorPane root = loader.load();

                Stage userProfileStage = new Stage();
                userProfileStage.setScene(new Scene(root));
                userProfileStage.setResizable(false);
                userProfileStage.setTitle(userFrom.getFirstName() + " " + userFrom.getLastName() + " profile");
                userProfileStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/berryLogo.jpg")));

                UserProfileController userProfileController = loader.getController();
                userProfileController.setUser(userFrom);
                userProfileController.setProfilePhotoUserService(profilePhotoUserService);
                userProfileController.setUserService(userService);
                userProfileController.setUserProfileStage(userProfileStage);
                userProfileController.initializeUserProfile();
                userProfileStage.show();
                buttonViewUserProfile.setDisable(true);
                userProfileController.getImageViewUserProfile().setDisable(true);

                userProfileStage.setOnCloseRequest(event -> {
                    buttonViewUserProfile.setDisable(false);
                    userProfileController.getImageViewUserProfile().setDisable(false);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void pressButtonFrom() {
        buttonUnsend.setVisible(false);
        buttonAcceptFriendshipRequest.setVisible(true);
        buttonDeclineFriendshipRequest.setVisible(true);
        tableColumnHeadFromTo.setText("From");
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory("firstNameFrom"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory("lastNameFrom"));
        initModel();
    }

    public void pressButtonTo() {
        buttonUnsend.setVisible(true);
        buttonAcceptFriendshipRequest.setVisible(false);
        buttonDeclineFriendshipRequest.setVisible(false);
        tableColumnHeadFromTo.setText("To");
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory("firstNameTo"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory("lastNameTo"));
        initModel();
    }

    public void pressButtonUnsend() {
        FriendshipRequest friendshipRequest = tableViewFriendshipRequests.getSelectionModel().getSelectedItem();
        if (friendshipRequest != null) {
            if (friendshipRequest.getStatusRequest().equals("pending")) {
                friendshipRequestService.deleteFriendshipRequest(friendshipRequest.getId());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a pending friendship request to unsend!");
                alert.show();
            }
        }
    }

    @Override
    public void update(FriendshipRequestChangeEvent friendshipRequestChangeEvent) {
        initModel();
    }
}
