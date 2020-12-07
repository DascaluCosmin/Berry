package socialnetwork.domain.messages;

import com.sun.org.apache.bcel.internal.Const;
import socialnetwork.domain.User;
import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ReplyMessage extends Message {
    private Message messageToReplyTo;

    /**
     * Constructor that creates a new ReplyMessage
     * @param from User, representing the User that sends the ReplyMessage
     * @param to List<User>, representing the list of Users that receive the ReplyMessage
     * @param message String, representing the text of the message
     * @param date LocalDateTime, representing the date the ReplyMessage was sent on
     * @param messageToReplyTo Message, representing the message to reply to
     */
    public ReplyMessage(User from, List<User> to, String message, LocalDateTime date, Message messageToReplyTo) {
        super(from, to, message, date, false);
        this.messageToReplyTo = messageToReplyTo;
    }

    /**
     * @return Message, representing the message to reply to
     */
    public Message getMessageToReplyTo() {
        return messageToReplyTo;
    }

    /**
     * @param messageToReplyTo, representing the new message to reply to
     */
    public void setMessageToReplyTo(Message messageToReplyTo) {
        this.messageToReplyTo = messageToReplyTo;
    }

    /**
     * Method that serializes a Reply Message
     * @return String, representing the serialization of the Reply Message
     */
    @Override
    public String toString() {
        String messageString =
                "ReplyMessage{" +
                "From= " + getFrom().getFirstName() + " " + getFrom().getLastName() + " " +
                "To= " + getTo().get(0).getFirstName() + " " + getTo().get(0).getLastName() + " " +
                "TextMessage= " + getMessage() + " " +
                "Data= " + getDate().format(Constants.DATE_TIME_FORMATTER);
        if (getMessageToReplyTo() != null)
            messageString += " Reply to= " + getMessageToReplyTo().getMessage();
        messageString += "}";
        return messageString;
    }

    public String toStringFormatted() {
        return getFormattedDate() + "\t|\t" + getFirstNameFrom() + " " + getLastNameFrom() + ": " + getMessage();
    }
}
