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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.UserDTO;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.UserService;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AccountUserController implements Observer<FriendshipChangeEvent> {
    ObservableList<UserDTO> model = FXCollections.observableArrayList();
    UserService userService;
    FriendshipService friendshipService;
    FriendshipRequestService friendshipRequestService;
    UserDTO selectedUserDTO;
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
    void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableViewAccountUser.setItems(model);
    }

    public void setAttributes(FriendshipService friendshipService, UserService userService,
                              FriendshipRequestService friendshipRequestService,  UserDTO selectedUserDTO) {
        this.friendshipService = friendshipService;
        this.friendshipService.addObserver(this);
        this.userService = userService;
        this.selectedUserDTO = selectedUserDTO;
        this.friendshipRequestService = friendshipRequestService;
        if (selectedUserDTO != null) {
            labelUserName.setText("Hello, " + selectedUserDTO.getFirstName());
            initModel();
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
            addFriendshipRequestStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/royalLogo.jpg")));

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

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        initModel();
    }
}
