package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;
import socialnetwork.domain.UserDTO;
import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddFriendshipViewController {
    ObservableList<UserDTO> modelUserDTO = FXCollections.observableArrayList();
    FriendshipService friendshipService;
    FriendshipRequestService friendshipRequestService;
    UserService userService;
    UserDTO selectedUserDTO;

    @FXML
    TableView<UserDTO> tableViewStrangers;
    @FXML
    TableColumn<UserDTO, String> tableColumnFirstName;
    @FXML
    TableColumn<UserDTO, String> tableColumnLastName;
    @FXML
    TextField textFieldMessage;

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableViewStrangers.setItems(modelUserDTO);
    }

    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    public void setFriendshipRequestService(FriendshipRequestService friendshipRequestService) {
        this.friendshipRequestService = friendshipRequestService;
    }

    public void setUserService(UserService userService, UserDTO selectedUserDTO) {
        this.userService = userService;
        this.selectedUserDTO = selectedUserDTO;
        initModel();
    }

    private void initModel() {
        Iterable<User> users = userService.getAll();
        List<UserDTO> nonFriends = new ArrayList<>();
        users.forEach(user -> {
            if (friendshipService.findOne(selectedUserDTO.getId(), user.getId()) == null && !user.equals(selectedUserDTO)) {
                UserDTO userDTO = new UserDTO(user.getFirstName(), user.getLastName());
                userDTO.setId(user.getId());
                nonFriends.add(userDTO);
            }
        });
        if (nonFriends.size() == 0) {
            modelUserDTO.setAll(nonFriends);
            tableViewStrangers.setPlaceholder(new Label("You are a friend of all users!"));
        } else {
            modelUserDTO.setAll(nonFriends);
        }
    }

    public void sendFriendshipRequest() {
        UserDTO userToDTO = tableViewStrangers.getSelectionModel().getSelectedItem();
        if (userToDTO != null) {
            User userFrom = userService.getUser(selectedUserDTO.getId());
            User userTo = userService.getUser(userToDTO.getId());
            String message = textFieldMessage.getText();
            if (message.matches("[ ]*")) {
                message = userFrom.getFirstName() + " " + userFrom.getLastName() + " has sent you a friendship request";
            }
            FriendshipRequest friendshipRequest = new FriendshipRequest(userFrom, Arrays.asList(userTo), message,
                    LocalDateTime.now(), "pending");
            try {
                friendshipRequestService.addFriendshipRequest(friendshipRequest);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "The friendship request has been sent!");
                alert.show();
            } catch (ValidationException validationException) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "You have already sent a friendship request");
                alert.show();
            }
        }
    }
}
