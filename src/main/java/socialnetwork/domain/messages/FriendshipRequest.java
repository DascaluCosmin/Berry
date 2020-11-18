package socialnetwork.domain.messages;

import socialnetwork.domain.User;
import socialnetwork.utils.Constants;

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
