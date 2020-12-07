package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;
import socialnetwork.domain.UserDTO;
import socialnetwork.domain.messages.Message;
import socialnetwork.domain.messages.ReplyMessage;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageController {
    private ObservableList<UserDTO> modelSelected = FXCollections.observableArrayList();
    private ObservableList<UserDTO> modelUnselected = FXCollections.observableArrayList();
    private ObservableList<Message> modelMessages = FXCollections.observableArrayList();
    private UserService userService;
    private MessageService messageService;
    private FriendshipService friendshipService;
    private UserDTO selectedUserDTO;
    private List<UserDTO> listUsersSelected = new ArrayList();
    private List<UserDTO> listUsersUnselected = new ArrayList();

    @FXML
    TableView<UserDTO> tableViewUnselected;
    @FXML
    TableView<Message> tableViewMessage;
    @FXML
    TableView<UserDTO> tableViewSelected;
    @FXML
    TableColumn<UserDTO, String> tableColumnFirstNameUnselected;
    @FXML
    TableColumn<UserDTO, String> tableColumnLastNameUnselected;
    @FXML
    TableColumn<UserDTO, String> tableColumnFirstNameSelected;
    @FXML
    TableColumn<UserDTO, String> tableColumnLastNameSelected;
    @FXML
    TableColumn<Message, String> tableColumnDate;
    @FXML
    TableColumn<Message, String> tableColumnFirstName;
    @FXML
    TableColumn<Message, String> tableColumnLastName;
    @FXML
    TableColumn<Message, String> tableColumnMessage;
    @FXML
    TextField textFieldMessageCompose;
    @FXML
    TextField textFieldMessageReply;

    @FXML
    public void initialize() {
        tableColumnFirstNameSelected.setCellValueFactory(new PropertyValueFactory("firstName"));
        tableColumnLastNameSelected.setCellValueFactory(new PropertyValueFactory("lastName"));
        tableColumnFirstNameUnselected.setCellValueFactory(new PropertyValueFactory("firstName"));
        tableColumnLastNameUnselected.setCellValueFactory(new PropertyValueFactory("lastName"));
        tableViewSelected.setItems(modelSelected);
        tableViewUnselected.setItems(modelUnselected);

        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstNameFrom"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory("lastNameFrom"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory("message"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory("formattedDate"));
        tableViewMessage.setItems(modelMessages);
    }

    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    public void setSelectedUserDTO(UserDTO selectedUserDTO) {
        this.selectedUserDTO = selectedUserDTO;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
        initModel();
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
        initModelMessages();
    }

    private void initModel() {
        Iterable<Friendship> friendships = this.friendshipService.getAllFriendshipsUser(selectedUserDTO.getId());
        List<UserDTO> listFriends = new ArrayList();
        friendships.forEach(friendship -> listFriends.add(userService.getUserDTO(friendship.getId().getRight())));
        if (!friendships.iterator().hasNext()) {
            // Need model.setAll() because the setPlaceholder can't write over a model that has data
            modelUnselected.setAll(listFriends);
            tableViewUnselected.setPlaceholder(new Label("You have no added friends"));
        } else {
            listUsersUnselected.clear();
            listUsersSelected.clear();
            listUsersUnselected.addAll(listFriends);
            modelUnselected.setAll(listUsersUnselected);
            modelSelected.setAll(listUsersSelected);
        }
    }

    private void initModelMessages() {
        Iterable<Message> messages = messageService.getAllMessagesToUser(selectedUserDTO.getId());
        List<Message> listMessages = new ArrayList<>();
        messages.forEach(listMessages::add);
        if (messages.iterator().hasNext()) {
            modelMessages.setAll(listMessages);
            tableViewMessage.setPlaceholder(new Label("Your inbox is empty"));
        } else {
            modelMessages.setAll(listMessages);
        }
    }

    public void eventButtonSelect() {
        UserDTO userDTO = tableViewUnselected.getSelectionModel().getSelectedItem();
        if (userDTO != null) {
            listUsersSelected.add(userDTO);
            listUsersUnselected.remove(userDTO);
            modelSelected.setAll(listUsersSelected);
            modelUnselected.setAll(listUsersUnselected);
            tableViewUnselected.getSelectionModel().clearSelection();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select an user!");
            alert.show();
        }
    }

    public void eventButtonUnselect() {
        UserDTO userDTO = tableViewSelected.getSelectionModel().getSelectedItem();
        if (userDTO != null) {
            listUsersUnselected.add(userDTO);
            listUsersSelected.remove(userDTO);
            modelSelected.setAll(listUsersSelected);
            modelUnselected.setAll(listUsersUnselected);
            tableViewSelected.getSelectionModel().clearSelection();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select an user!");
            alert.show();
        }
    }

    public void sendMessage() {
        String textMessage = textFieldMessageCompose.getText();
        textFieldMessageCompose.clear();
        if (!textMessage.matches("[ ]*")) {
            if (listUsersSelected.size() != 0) {
                User userFrom = userService.getUser(selectedUserDTO.getId());
                List<User> usersTo = new ArrayList<>();
                listUsersSelected.forEach(userDTO -> usersTo.add(userService.getUser(userDTO.getId())));
                Message message = new Message(userFrom, usersTo,textMessage, LocalDateTime.now());
                messageService.addMessage(message);
                initModel();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please select users to send a message to!");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a text message!");
            alert.show();
        }
    }

    public void eventReply() {
        Message messageToReplyTo = tableViewMessage.getSelectionModel().getSelectedItem();
        String textMessage = textFieldMessageReply.getText();
        textFieldMessageReply.clear();
        if (messageToReplyTo != null) {
            if (!textMessage.matches("[ ]*")) {
                User userFrom = userService.getUser(selectedUserDTO.getId());
                User userTo = messageToReplyTo.getFrom();
                Message replyMessage = new Message(userFrom, Arrays.asList(userTo), textMessage, LocalDateTime.now());
                messageService.addMessage(replyMessage);
                tableViewMessage.getSelectionModel().clearSelection();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a text message!");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a message to reply to!");
            alert.show();
        }
    }
}
