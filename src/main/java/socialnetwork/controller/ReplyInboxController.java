package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import socialnetwork.domain.Page;
import socialnetwork.domain.User;
import socialnetwork.domain.messages.Message;
import socialnetwork.utils.Constants;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;

public class ReplyInboxController {
    private Page userPage;
    private Message receivedMessage;
    private User userFrom;
    private Stage replyInboxStage;

    @FXML
    Label labelSubject;
    @FXML
    Label labelNameSender;
    @FXML
    Label labelDate;
    @FXML
    Label labelMessageText;
    @FXML
    Circle circleProfilePhotoSender;
    @FXML
    TextArea textAreaMessage;

    /**
     * @param userPage Page, representing the Page of the logged in User
     */
    public void setUserPage(Page userPage) {
        this.userPage = userPage;
    }

    /**
     * @param receivedMessage Message, representing the received Message to reply to
     */
    public void setReceivedMessage(Message receivedMessage) {
        this.receivedMessage = receivedMessage;
        userFrom = receivedMessage.getFrom();
        initializeMessage();
    }

    /**
     * @param replyInboxStage Stage, representing the current Stage
     */
    public void setReplyInboxStage(Stage replyInboxStage) {
        this.replyInboxStage = replyInboxStage;
    }

    /**
     * Method that initializes the View with the Message's content - Sender, Date & Text
     */
    private void initializeMessage() {
        labelSubject.setText(receivedMessage.getSubject());
        labelNameSender.setText(receivedMessage.getNameFrom());
        labelDate.setText(receivedMessage.getDate().format(Constants.DATE_TIME_FORMATTER_MONTH_NAME));
        labelMessageText.setText(receivedMessage.getMessage());
        try {
            Image photoProfileImage = new Image(
                    new FileInputStream(userPage.getProfilePhotoUserService().findOne(userFrom.getId()).getPathProfilePhoto()),
                    500, 500, false, true
            );
            circleProfilePhotoSender.setFill(new ImagePattern(photoProfileImage));
        } catch (FileNotFoundException ignored) {
        }
    }

    /**
     * Method linked to the labelCloseReceivedMessage's onMouseClicked
     * It closes the Window
     */
    public void eventCloseInbox() {
        replyInboxStage.close();
    }

    /**
     * Method linked to the buttonSendReplyInbox's onMouseClicked
     * It sends a Message, replying to the Received Message
     */
    public void eventSendReplyMessage() {
        String textMessage = textAreaMessage.getText();
        if (textMessage.matches("[ ]*")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The message can't contain only blank spaces!");
            alert.show();
        } else {
            Message message = new Message(
                    userPage.getUser(), Collections.singletonList(userFrom),
                    "Reply to: " + receivedMessage.getSubject(), textMessage,LocalDateTime.now()
            );
            if (userPage.getMessageService().addMessage(message) == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "The message has been sent successfully!");
                alert.show();
                replyInboxStage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error at sending the message!");
                alert.show();
            }
        }
    }
}
