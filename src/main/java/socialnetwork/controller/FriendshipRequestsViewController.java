package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.UserDTO;
import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.FriendshipService;
import socialnetwork.utils.events.FriendshipRequestChangeEvent;
import socialnetwork.utils.observer.Observer;

public class FriendshipRequestsViewController implements Observer<FriendshipRequestChangeEvent> {
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
        this.friendshipRequestService.addObserver(this);
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

    @Override
    public void update(FriendshipRequestChangeEvent friendshipRequestChangeEvent) {
        initModel();
    }
}
