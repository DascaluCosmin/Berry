package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.domain.UserDTO;
import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.FriendshipService;

public class FriendshipRequestsViewController {
    UserDTO selectedUserDTO;
    FriendshipRequestService friendshipRequestService;
    FriendshipService friendshipService;
    ObservableList<FriendshipRequest> model = FXCollections.observableArrayList();

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
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory("lastName"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory("message"));
        tableColumnSentDate.setCellValueFactory(new PropertyValueFactory("formatedDate"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory("statusRequest"));
        tableViewFriendshipRequests.setItems(model);
    }

    public void setFriendshipRequestService(FriendshipRequestService friendshipRequestService) {
        this.friendshipRequestService = friendshipRequestService;
        initModel();
    }

    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    private void initModel() {
        model.setAll(friendshipRequestService.getFriendshipRequestsUser(selectedUserDTO.getId()));
    }

    public void setSelectedUser(UserDTO selectedUserDTO) {
        this.selectedUserDTO = selectedUserDTO;
    }
}
