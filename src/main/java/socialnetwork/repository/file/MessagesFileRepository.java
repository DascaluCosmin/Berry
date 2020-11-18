package socialnetwork.repository.file;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;
import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.domain.messages.Message;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessagesFileRepository extends AbstractFileRepository<Long, Message> {

    /**
     * Constructor that creates a new MessagesFileRepository
     * @param fileName  String, representing the name of the file where the data is loaded from / stored to
     * @param validator ValidatorService<Message>, representing the validator of the MessageFileRepository
     * @param repository Repository<Long, User>, representing a Repository of Users
     */
    public MessagesFileRepository(String fileName, Validator<Message> validator,
        Repository<Long, User> repository) {
        super(fileName, validator, repository);
    }

    /**
     * Method that extracts a Message having a specified list of attributes
     * @param attributes List<String>, representing the attributes of the Message to be extracted
     * @return Message, representing the extracted Message based on the given attributes
     */
    @Override
    public Message extractEntity(List<String> attributes) {
        Long idMessage = Long.parseLong(attributes.get(0));
        Long idSender = Long.parseLong(attributes.get(1));
        String[] listIDSReceivers =  attributes.get(2).split(",");
        List<User> listReceivers = new ArrayList<>();

        // TODO: verifica in UI daca userii pe care ii adauga exista
        for(int i = 0; i < listIDSReceivers.length; i++) {
            listReceivers.add(userRepository.findOne(Long.parseLong(listIDSReceivers[i])));
        }
        String textMessage = attributes.get(3);
        LocalDateTime data = LocalDateTime.parse(attributes.get(4), Constants.DATE_TIME_FORMATTER);
        Message message = new Message(userRepository.findOne(idSender), listReceivers,
                textMessage, data);
        message.setId(idMessage);
        return message;
    }

    /**
     * Method that gets the serialization of a Message
     * @param entity Message, representing the entity whose serialization is being determined
     * @return String, representing the serialization of the Message
     */
    @Override
    protected String createEntityAsString(Message entity) {
        String stringToIds = "";
        List<User> listUsers = entity.getTo();
        for (User friend : listUsers) {
            stringToIds += friend.getId() + ",";
        }
        if (stringToIds.length() >= 1)
            stringToIds = stringToIds.substring(0, stringToIds.length() - 1);
        String messageAttributes = "";
        messageAttributes += entity.getId() + ";" +
                entity.getFrom().getId() + ";" +
                stringToIds + ";" +
                entity.getMessage() + ";" +
                entity.getDate().format(Constants.DATE_TIME_FORMATTER);
        return messageAttributes;
    }
}
