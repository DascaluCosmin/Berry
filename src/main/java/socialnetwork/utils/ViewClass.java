package socialnetwork.utils;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ViewClass {
    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * Method that initializes a View.
     * It makes the Stage movable and transparent
     * @param primaryStage Stage, representing the Stage to be initialized
     * @param loader FXMLLoader, representing the Loader of the View
     * @throws IOException
     */
    public void initView(Stage primaryStage, FXMLLoader loader) throws IOException {
        Parent root = loader.load();
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/berryLogo2.png")));
        primaryStage.setScene(scene);
    }
}
