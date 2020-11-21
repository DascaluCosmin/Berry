package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
    Stage introductionStage;

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("lastName"));
        tableViewUserDTO.setItems(modelUserDTO);
    }

    public void setUserService(UserService userService, Stage introductionStage) {
        this.userService = userService;
        this.introductionStage = introductionStage;
        modelUserDTO.setAll(this.userService.getAllUserDTO());
        if (modelUserDTO.size() == 0) {
            tableViewUserDTO.setPlaceholder(new Label("There are no users in the social network"));
        }
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
            accountUserController.setAttributes(friendshipService, userService, selectedUserDTO);
            introductionStage.hide();
            accountUserStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
