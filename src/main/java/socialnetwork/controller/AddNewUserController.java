package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.domain.ProfilePhotoUser;
import socialnetwork.domain.User;
import socialnetwork.service.ProfilePhotoUserService;
import socialnetwork.service.UserService;


public class AddNewUserController {
    private UserService userService;
    private ProfilePhotoUserService profilePhotoUserService;
    private Stage addNewUserStage;
    private TextField textFieldSearchIntroduction;

    @FXML
    TextField textFieldFirstName;
    @FXML
    TextField textFieldLastName;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setProfilePhotoUserService(ProfilePhotoUserService profilePhotoUserService) {
        this.profilePhotoUserService = profilePhotoUserService;
    }

    public void setAddNewUserStage(Stage addNewUserStage) {
        this.addNewUserStage = addNewUserStage;
    }

    public void setTextFieldSearchIntroduction(TextField textFieldSearchIntroduction) {
        this.textFieldSearchIntroduction = textFieldSearchIntroduction;
    }

    public void addNewUser() {
        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();
        if (firstName.matches("[ ]*") || firstName.matches(".*\\d.*")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The first name is not valid!");
            alert.show();
        } else if (lastName.matches("[ ]*") || lastName.matches(".*\\d.*")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The last name is not valid!");
            alert.show();
        } else {
            User userToBeAdded = new User(firstName, lastName);
            userToBeAdded.setId(userService.getMaximumUserID() + 1);
            userService.addUser(userToBeAdded);
            ProfilePhotoUser profilePhotoUser = new ProfilePhotoUser();
            profilePhotoUser.setId(userToBeAdded.getId());
            profilePhotoUserService.addProfilePhotoUser(profilePhotoUser);
            textFieldFirstName.clear();
            textFieldLastName.clear();
            textFieldSearchIntroduction.clear();
            addNewUserStage.close();
        }
    }
}
