package socialnetwork.domain.messages;

import socialnetwork.domain.User;
import socialnetwork.utils.Constants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FriendshipRequest extends Message {
    private String statusRequest;

    /**
     * Constructor that creates a new Friendship Request
     * @param from User, representing the User that sends the Friendship Request
     * @param to List<User>, representing the list of Users that receive the Friendship Request
     * @param message String, representing the text of the Friendship Request
     * @param date LocalDateTime, representing the date the Friendship Request was sent on
     * @param status String, representing the status of the Friendship Request
     */
    public FriendshipRequest(User from, List<User> to, String message, LocalDateTime date, String status) {
        super(from, to, message, date, "");
        this.statusRequest = status;
    }

    /**
     * @return String, representing the status of the Friendship Request
     */
    public String getStatusRequest() {
        return statusRequest;
    }

    /**
     * @param statusRequest String, representing the new status of the Friendship Request
     */
    public void setStatusRequest(String statusRequest) {
        this.statusRequest = statusRequest;
    }

    /**
     * @return String, representing the First Name of the User who sends the message
     */
    public String getFirstNameFrom() {
        return getFrom().getFirstName();
    }

    /**
     * @return String, representing the Last Name of the User who sends the message
     */
    public String getLastNameFrom() {
        return getFrom().getLastName();
    }

    /**
     * @return String, representing the First Name of the User who receives the message
     */
    public String getFirstNameTo() {
        return getTo().get(0).getFirstName();
    }

    /**
     * @return String, representing the Last Name of the User who receives the message
     */
    public String getLastNameTo() {
        return getTo().get(0).getLastName();
    }

    /**
     * @return String, representing the Date in String format
     */
    public String getFormatedDate() {
        return getDate().format(Constants.DATE_TIME_FORMATTER);
    }

    /**
     * Method that serializes a Friendship Request
     * @return String, representing the serialization of the Friendship Request
     */
    @Override
    public String toString() {
        return "FriendshipRequestMessage{" +
                "From= " + getFrom().getFirstName() + " " + getFrom().getLastName() + " " +
                "To= " + getTo().get(0).getFirstName() + " " + getTo().get(0).getLastName() + " " +
                "TextMessage= " + getMessage() + " " +
                "Data= " + getDate().format(Constants.DATE_TIME_FORMATTER) + " " +
                "Status= " + getStatusRequest() + "}";
    }
}
