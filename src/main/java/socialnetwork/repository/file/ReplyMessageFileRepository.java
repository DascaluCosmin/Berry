package socialnetwork.repository.file;

import socialnetwork.domain.User;
import socialnetwork.domain.messages.ReplyMessage;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ReplyMessageFileRepository extends AbstractFileRepository<Long, ReplyMessage> {

    /**
     * Constructor that creates a new ReplyMessageFileRepository
     * @param fileName  String, representing the name of the file where the data is loaded from / stored to
     * @param validator ValidatorService<ReplyMessage>, representing the validator of the ReplyMessageFileRepository
     * @param userRepository Repositort<Long, User>, represnting the Repository handling the User data
     */
    public ReplyMessageFileRepository(String fileName, Validator<ReplyMessage> validator,
                                      Repository<Long, User> userRepository) {
        super(fileName, validator, userRepository);
    }

    /**
     * Method that extracts a ReplyMessage having a specified list of attributes
     * @param attributes List<String>, representing the attributes of the ReplyMessage to be extracted
     * @return ReplyMessage, representing the extracted ReplyMessage based on the given attributes
     */
    @Override
    public ReplyMessage extractEntity(List<String> attributes) {
        Long idSender = Long.parseLong(attributes.get(1));
        Long idReceiver = Long.parseLong(attributes.get(2));
        String textMessage = attributes.get(3);
        LocalDateTime data = LocalDateTime.parse(attributes.get(4), Constants.DATE_TIME_FORMATTER);
        Long idReplyMessage = Long.parseLong(attributes.get(5));
        return new ReplyMessage(userRepository.findOne(idSender), Arrays.asList(userRepository.findOne(idReceiver)),
                textMessage, data, findOne(idReplyMessage));
    }

    /**
     * Method that gets the serialization of a ReplyMessage
     * @param entity ReplyMessage, representing the entity whose serialization is being determined
     * @return String, representing the serialization of the ReplyMessage
     */
    @Override
    protected String createEntityAsString(ReplyMessage entity) {
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
                entity.getDate().format(Constants.DATE_TIME_FORMATTER) + ";";
        if (entity.getMessageToReplyTo() == null)  messageAttributes += "0";
            else messageAttributes += entity.getMessageToReplyTo().getId();
        return messageAttributes;
    }
}
