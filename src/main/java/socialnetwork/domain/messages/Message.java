package socialnetwork.domain.messages;

import socialnetwork.domain.Entity;
import socialnetwork.domain.User;
import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long> {
    private static long idMaxMessage = 0;
    private static long idMaxReplyMessage = 0;
    private static long idMaxFriendshipRequest = 0;
    private User from;
    private List<User> to;
    private String subject;
    private String message;
    private LocalDateTime date;

    /**
     * Constructor that creates a new Message
     * @param from User, representing the User that sends the Message
     * @param to List<User>, representing the list of Users that receive the Message
     * @param message String, representing the text of the Message
     * @param date LocalDateTime, representing the date the Message was sent on
     * @param trash String, representing a placeholder variable
     */
    public Message(User from, List<User> to, String message, LocalDateTime date, String trash) {
        this.from = from;
        this.to = to;
        this.subject = "";
        this.message = message;
        this.date = date;
        idMaxFriendshipRequest++;
        setId(idMaxFriendshipRequest);
    }

    /**
     * Constructor that creates a new Message
     * @param from User, representing the User that sends the Message
     * @param to List<User>, representing the list of Users that receive the Message
     * @param message String, representing the text of the message
     * @param date LocalDateTime, representing the date the Message was sent on
     * @param trash boolean, representing a placeholder variable
     */
    public Message(User from, List<User> to, String message, LocalDateTime date, boolean trash) {
        this.from = from;
        this.to = to;
        this.subject = "";
        this.message = message;
        this.date = date;
        idMaxReplyMessage++;
        setId(idMaxReplyMessage);
    }

    /**
     * Constructor that creates a new Message
     * @param from User, representing the User that sends the Message
     * @param to List<User>, representing the list of Users that receive the Message
     * @param subject String, representing the text of the Message
     * @param message String, representing the text of the message
     * @param date LocalDateTime, representing the date the Message was sent on
     */
    public Message(User from, List<User> to, String subject, String message, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.message = message;
        this.date = date;
        idMaxMessage++;
        setId(idMaxMessage);
    }

    /**
     * @return User, representing the User that sends the Message
     */
    public User getFrom() {
        return from;
    }

    /**
     * @return List<User>, representing the list of Users that receive the Message
     */
    public List<User> getTo() {
        return to;
    }

    /**
     * @return String, representing the Subject of the Message
     */
    public String getSubject() {
       return subject;
    }

    /**
     * @return String, representing the text of the Message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return LocalDateTime, representing the date the Message was sent on
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * @return String, representing the Date in String format
     */
    public String getFormattedDate() {
        return getDate().format(Constants.DATE_TIME_FORMATTER_SHORTER);
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
     * @return String, representing the Full Name of the User who sends the message
     */
    public String getNameFrom() {
        return getFirstNameFrom() + " " + getLastNameFrom();
    }

    /**
     * @param from User, representing the new User that sends the Message
     */
    public void setFrom(User from) {
        this.from = from;
    }

    /**
     * @param to List<User>, representing the new list of Users that receive the Message
     */
    public void setTo(List<User> to) {
        this.to = to;
    }

    /**
     * @param subject String, representing the Subject of the Message
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @param message String, representing the new text of the Message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param date LocalDateTime, representing the new date the Message was sent on
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * Method that serializes a Message
     * @return String, representing the serialization of the Message
     */
    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + to +
                ", message='" + message + '\'' +
                ", date=" + date +
                '}';
    }

    /**
     * Method that minimally serializes a Friendship Request
     * @return String, representing the serialization of the Friendship Request
     */
    public String toStringFewer() {
        return "ID: " + getId() + " From: " + getFrom().getFirstName() + " " + getFrom().getLastName() +
                " Message: " + getMessage() + " Date: " + getDate().format(Constants.DATE_TIME_FORMATTER);
    }
}
