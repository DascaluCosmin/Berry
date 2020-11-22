package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import socialnetwork.domain.UserDTO;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.ProfilePhotoUserService;
import socialnetwork.service.UserService;
import javafx.collections.ObservableList;
import socialnetwork.utils.events.UserChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;

public class IntroductionController implements Observer<UserChangeEvent> {
    UserService userService;
    FriendshipService friendshipService;
    FriendshipRequestService friendshipRequestService;
    ProfilePhotoUserService profilePhotoUserService;

    ObservableList<UserDTO> modelUserDTO = FXCollections.observableArrayList();
    @FXML
    TableColumn<UserDTO, String> tableColumnFirstName;
    @FXML
    TableColumn<UserDTO, String> tableColumnLastName;
    @FXML
    TableView<UserDTO> tableViewUserDTO;
    Stage introductionStage;

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableViewUserDTO.setItems(modelUserDTO);
    }

    private void initModel() {
        modelUserDTO.setAll(this.userService.getAllUserDTO());
        if (modelUserDTO.size() == 0) {
            tableViewUserDTO.setPlaceholder(new Label("There are no users in the social network"));
        }
    }

    public void setUserService(UserService userService, Stage introductionStage) {
        this.userService = userService;
        this.userService.addObserver(this);
        this.introductionStage = introductionStage;
        initModel();
    }

    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    public void selectFriendsUser() {
        UserDTO selectedUserDTO = tableViewUserDTO.getSelectionModel().getSelectedItem();
        if (selectedUserDTO != null) {
            showAccountUserStage(selectedUserDTO);
        }
    }

    public void setProfilePhotoUserService(ProfilePhotoUserService profilePhotoUserService) {
        this.profilePhotoUserService = profilePhotoUserService;
    }

    private void showAccountUserStage(UserDTO selectedUserDTO) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/accountUser.fxml"));
            AnchorPane root = loader.load();

            Stage accountUserStage = new Stage();
            accountUserStage.setResizable(false);
            accountUserStage.hide();
            accountUserStage.setTitle("Your account");
            accountUserStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/royalLogo.jpg")));
            accountUserStage.initModality(Modality.APPLICATION_MODAL);
            accountUserStage.setOnCloseRequest(event -> {
                introductionStage.show();
                tableViewUserDTO.getSelectionModel().clearSelection();
            });
            Scene scene = new Scene(root);
            accountUserStage.setScene(scene);
            AccountUserController accountUserController = loader.getController();
            accountUserController.setAttributes(friendshipService, userService, friendshipRequestService, profilePhotoUserService,
                    selectedUserDTO, accountUserStage);
            introductionStage.hide();
            accountUserStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNewUser() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/addNewUserView.fxml"));
            AnchorPane root = loader.load();
            Stage addNewUserStage = new Stage();
            addNewUserStage.setTitle("Add new User");
            addNewUserStage.setResizable(false);
            addNewUserStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/royalLogo.jpg")));
            addNewUserStage.setScene(new Scene(root));
            AddNewUserController addNewUserController = loader.getController();
            addNewUserController.setUserService(userService);
            addNewUserController.setProfilePhotoUserService(profilePhotoUserService);
            addNewUserController.setAddNewUserStage(addNewUserStage);
            addNewUserStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFriendshipRequestService(FriendshipRequestService friendshipRequestService) {
        this.friendshipRequestService = friendshipRequestService;
    }

    @Override
    public void update(UserChangeEvent userChangeEvent) {
        initModel();
    }
}
