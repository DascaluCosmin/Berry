package socialnetwork.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.domain.User;
import socialnetwork.domain.UserDTO;
import socialnetwork.domain.messages.ReplyMessage;
import socialnetwork.service.ReplyMessageService;
import socialnetwork.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatViewController {
    @FXML
    private ListView<String> listViewConversation;
    @FXML
    private TextField textFieldMessage;
    @FXML
    private ObservableList<String> model = FXCollections.observableArrayList();
    private ReplyMessageService replyMessageService;
    private UserService userService;
    private UserDTO loggedInUserDTO;
    private UserDTO selectedUserForConversationDTO;
    private Thread thread;
    private Stage conversationStage;
    private Stage accountUserViewStage;

    @FXML
    public void initialize() {
        listViewConversation.setItems(model);
    }

    public void setLoggedInUser(UserDTO loggedInUserDTO) {
        this.loggedInUserDTO = loggedInUserDTO;
    }

    public void setSelectedUserForConversation(UserDTO selectedUserForConversationDTO) {
        this.selectedUserForConversationDTO = selectedUserForConversationDTO;
    }

    public void setStages(Stage chatViewStage, Stage accountUserViewStage) {
        this.conversationStage = chatViewStage;
        this.accountUserViewStage = accountUserViewStage;
        chatViewStage.setOnCloseRequest(event -> {
            accountUserViewStage.show();
            thread.interrupt();
        });
    }

    public void setReplyMessageService(ReplyMessageService replyMessageService) {
        this.replyMessageService = replyMessageService;
        initModel();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    initModel();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        thread.start();
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void sendMessage() {
        String messageText = textFieldMessage.getText();
        User loggedInUser = userService.getUser(loggedInUserDTO.getId());
        User selectedUserForConversation = userService.getUser(selectedUserForConversationDTO.getId());
        ReplyMessage replyMessage = new ReplyMessage(loggedInUser, Arrays.asList(selectedUserForConversation), messageText, LocalDateTime.now(), null);
        replyMessageService.addMessage(replyMessage);
        textFieldMessage.clear();
    }

    private void initModel() {
        List<String> conversation = new ArrayList<>();
        replyMessageService.getConversation(loggedInUserDTO.getId(), selectedUserForConversationDTO.getId()).forEach(replyMessage -> {
            conversation.add(replyMessage.toStringFormatted());
        });
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                model.setAll(conversation);
            }
        });
    }
}
