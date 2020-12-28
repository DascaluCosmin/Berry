package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import socialnetwork.domain.Page;
import socialnetwork.domain.posts.TextPost;
import socialnetwork.utils.events.TextPostEvent;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDate;

public class WritePostController {
    private Integer numberPosts;

    @FXML
    TextArea textAreaPost;
    Label labelNumberPosts;
    Stage writePostStage;
    Page userPage;

    /**
     * @param labelNumberPosts Label, representing the label containing the number of Text Pots the User has
     */
    public void setLabelNumberPosts(Label labelNumberPosts) {
        this.labelNumberPosts = labelNumberPosts;
    }

    /**
     * @param numberPosts Integer, representing the number of Text Posts the User has
     */
    public void setNumberPosts(Integer numberPosts) {
        this.numberPosts = numberPosts;
    }

    /**
     * @param userPage Page, representing the Page of the logged in User.
     */
    public void setUserPage(Page userPage) {
        this.userPage = userPage;
    }

    /**
     * @param writePostStage Stage, representing the current Stage
     */
    public void setWritePostStage(Stage writePostStage) {
        this.writePostStage = writePostStage;
    }

    /**
     * Method linked to the labelExit's onMouseClicked event
     * It closes the current WritePostView
     */
    public void eventExitView() {
        writePostStage.close();
    }

    /**
     * Method linked to the buttonPost's onMouseClicked event
     * It allows the User to make a new Text Post
     */
    public void eventPost() {
        String text = textAreaPost.getText();
        if (text.matches("[ ]*")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The text of the post can't contain only blank spaces!");
            alert.show();
        } else {
            TextPost textPost = new TextPost(userPage.getUser().getId(), LocalDate.now(), text);
            userPage.getTextPostService().addTextPost(textPost);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The post was made successfully!");
            alert.show();
            textAreaPost.clear();
            numberPosts++;
            labelNumberPosts.setText(numberPosts + " Posts");
        }
    }
}
