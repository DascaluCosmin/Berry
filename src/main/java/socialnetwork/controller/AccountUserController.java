package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;
import socialnetwork.domain.UserDTO;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class AccountUserController {
    ObservableList<UserDTO> model = FXCollections.observableArrayList();
    UserService userService;
    FriendshipService friendshipService;
    UserDTO selectedUserDTO;

    @FXML
    TableColumn<UserDTO, String> tableColumnFirstName;

    @FXML
    TableColumn<UserDTO, String> tableColumnLastName;

    @FXML
    TableView<UserDTO> tableViewAccountUser;

    @FXML
    void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("lastName"));
        tableViewAccountUser.setItems(model);
    }

    void setAttributes(FriendshipService friendshipService, UserService userService, UserDTO selectedUserDTO) {
        this.friendshipService = friendshipService;
        this.userService = userService;
        this.selectedUserDTO = selectedUserDTO;
        if (selectedUserDTO != null) {
            Iterable<Friendship> friendships = this.friendshipService.getAllFriendshipsUser(selectedUserDTO.getId());
            List<UserDTO> listFriends = new ArrayList();
            friendships.forEach(friendship -> {
                listFriends.add(userService.getUserDTO(friendship.getId().getRight()));
            });
            model.setAll(listFriends);
        }
    }
}
