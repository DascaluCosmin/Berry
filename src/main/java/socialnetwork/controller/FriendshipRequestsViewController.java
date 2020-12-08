package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

public class FriendshipRequestsViewController implements Observer<FriendshipRequestChangeEvent> {
    UserDTO selectedUserDTO;
    FriendshipRequestService friendshipRequestService;
    FriendshipService friendshipService;
    UserService userService;
    ProfilePhotoUserService profilePhotoUserService;
    ObservableList<FriendshipRequest> modelFrom = FXCollections.observableArrayList();
    ObservableList<FriendshipRequest> modelTo = FXCollections.observableArrayList();

    @FXML
    Button buttonViewUserProfileFrom;
    @FXML
    Button buttonAcceptFriendshipRequest;
    @FXML
    Button buttonDeclineFriendshipRequest;
    @FXML
    Button buttonUnsend;
    @FXML
    TableView<FriendshipRequest> tableViewFriendshipRequestsFrom;
    @FXML
    TableView<FriendshipRequest> tableViewFriendshipRequestsTo;
    @FXML
    TableColumn<FriendshipRequest, String> tableColumnFirstNameFrom;
    @FXML
    TableColumn<FriendshipRequest, String> tableColumnLastNameFrom;
    @FXML
    TableColumn<FriendshipRequest, String> tableColumnMessageFrom;
    @FXML
    TableColumn<FriendshipRequest, String> tableColumnSentDateFrom;
    @FXML
    TableColumn<FriendshipRequest, String> tableColumnStatusFrom;
    @FXML
    TableColumn<FriendshipRequest, String> tableColumnFirstNameTo;
    @FXML
    TableColumn<FriendshipRequest, String> tableColumnLastNameTo;
    @FXML
    TableColumn<FriendshipRequest, String> tableColumnMessageTo;
    @FXML
    TableColumn<FriendshipRequest, String> tableColumnSentDateTo;
    @FXML
    TableColumn<FriendshipRequest, String> tableColumnStatusTo;
    @FXML
    TableColumn<TableColumn<Friendship, String>, TableColumn<Friendship, String>> tableColumnHeadFromTo;

    @FXML
    public void initialize() {
        tableColumnFirstNameFrom.setCellValueFactory(new PropertyValueFactory<>("firstNameFrom"));
        tableColumnLastNameFrom.setCellValueFactory(new PropertyValueFactory<>("lastNameFrom"));
        tableColumnMessageFrom.setCellValueFactory(new PropertyValueFactory<>("message"));
        tableColumnSentDateFrom.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        tableColumnStatusFrom.setCellValueFactory(new PropertyValueFactory<>("statusRequest"));
        tableViewFriendshipRequestsFrom.setItems(modelFrom);

        tableColumnFirstNameTo.setCellValueFactory(new PropertyValueFactory<>("firstNameTo"));
        tableColumnLastNameTo.setCellValueFactory(new PropertyValueFactory<>("lastNameTo"));
        tableColumnMessageTo.setCellValueFactory(new PropertyValueFactory<>("message"));
        tableColumnSentDateTo.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        tableColumnStatusTo.setCellValueFactory(new PropertyValueFactory<>("statusRequest"));
        tableViewFriendshipRequestsTo.setItems(modelTo);
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
        modelFrom.setAll(friendshipRequestService.getFriendshipRequestsUser(selectedUserDTO.getId()));
        modelTo.setAll(friendshipRequestService.getFriendshipRequestsUserFrom(selectedUserDTO.getId()));
        if (modelFrom.size() == 0) {
            tableViewFriendshipRequestsFrom.setPlaceholder(new Label("There are no friendship requests sent to you!"));
        }
        if (modelTo.size() == 0) {
            tableViewFriendshipRequestsTo.setPlaceholder(new Label("There are no friendship requests sent by you!"));
        }
    }

    public void setSelectedUser(UserDTO selectedUserDTO) {
        this.selectedUserDTO = selectedUserDTO;
    }

    public void acceptPendingFriendshipRequest() {
        FriendshipRequest selectedFriendshipRequest = tableViewFriendshipRequestsFrom.getSelectionModel().getSelectedItem();
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
        FriendshipRequest selectedFriendshipRequest = tableViewFriendshipRequestsFrom.getSelectionModel().getSelectedItem();
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
        FriendshipRequest selectedFriendshipRequest = tableViewFriendshipRequestsFrom.getSelectionModel().getSelectedItem();
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
                buttonViewUserProfileFrom.setDisable(true);
                userProfileController.getImageViewUserProfile().setDisable(true);

                userProfileStage.setOnCloseRequest(event -> {
                    buttonViewUserProfileFrom.setDisable(false);
                    userProfileController.getImageViewUserProfile().setDisable(false);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void pressButtonUnsend() {
        FriendshipRequest friendshipRequest = tableViewFriendshipRequestsTo.getSelectionModel().getSelectedItem();
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
