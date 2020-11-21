package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.UserDTO;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.UserService;
import javafx.collections.ObservableList;

import java.io.IOException;

public class IntroductionController {
    UserService userService;
    FriendshipService friendshipService;
    ObservableList<UserDTO> modelUserDTO = FXCollections.observableArrayList();

    @FXML
    TableColumn<UserDTO, String> tableColumnFirstName;

    @FXML
    TableColumn<UserDTO, String> tableColumnLastName;

    @FXML
    TableView<UserDTO> tableViewUserDTO;

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("lastName"));
        tableViewUserDTO.setItems(modelUserDTO);
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
        modelUserDTO.setAll(this.userService.getAllUserDTO());
    }

    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    public void selectFriendsUser() {
        UserDTO selectedUserDTO = tableViewUserDTO.getSelectionModel().getSelectedItem();
        showAccountUserStage(selectedUserDTO);
    }

    private void showAccountUserStage(UserDTO selectedUserDTO) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/accountUser.fxml"));
            AnchorPane root = loader.load();

            Stage accountUserStage = new Stage();
            accountUserStage.setTitle("User account");
            accountUserStage.initModality(Modality.APPLICATION_MODAL);

            Scene scene = new Scene(root);
            accountUserStage.setScene(scene);
            AccountUserController accountUserController = loader.getController();
            accountUserController.setAttributes(friendshipService, userService, selectedUserDTO);

            // TODO: Set service
            accountUserStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
