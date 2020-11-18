package socialnetwork.repository.file;

import socialnetwork.domain.User;
import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FriendshipRequestFileRepository extends AbstractFileRepository<Long, FriendshipRequest> {

    /**
     * Constructor that creates a new FriendshipRequestFileRepository
     * @param fileName String, representing the name of the file where the data is loaded from / stored to
     * @param validator Validator<FriendshipRequest>, representing the validator of the FriendshipRequestFileRepository
     * @param repository Repository<Long, User>, representing a Repository of Users
     */
    public FriendshipRequestFileRepository(String fileName, Validator<FriendshipRequest> validator,
                                           Repository<Long, User> repository) {
        super(fileName, validator, repository);
    }

    /**
     * Method that extracts a FriendshipRequest having a specified list of attributes
     * @param attributes List<String>, representing the attributes of the FriendshipRequest to be extracted
     * @return FriendshipRequest, representing the extracted FriendshipRequest based on the given attributes
     */
    @Override
    public FriendshipRequest extractEntity(List<String> attributes) {
        Long idFriendshipRequest = Long.parseLong(attributes.get(0));
        Long idSender = Long.parseLong(attributes.get(1));
        Long idReceiver = Long.parseLong(attributes.get(2));
        String textMessage = attributes.get(3);
        LocalDateTime data = LocalDateTime.parse(attributes.get(4), Constants.DATE_TIME_FORMATTER);
        String status = attributes.get(5);
        FriendshipRequest friendshipRequest = new FriendshipRequest(userRepository.findOne(idSender), Arrays.asList(userRepository.findOne(idReceiver)),
                textMessage, data, status);
        friendshipRequest.setId(idFriendshipRequest);
        return friendshipRequest;
    }

    /**
     * Method that gets the serialization of a FriendshipRequest
     * @param entity FriendshipRequest, representing the entity whose serialization is being determined
     * @return String, representing the serialization of the FriendshipRequest
     */
    @Override
    protected String createEntityAsString(FriendshipRequest entity) {
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
                entity.getDate().format(Constants.DATE_TIME_FORMATTER) + ";" +
                entity.getStatusRequest();
        return messageAttributes;
    }
}
