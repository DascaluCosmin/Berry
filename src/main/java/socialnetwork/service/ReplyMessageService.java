package socialnetwork.service;

import socialnetwork.domain.messages.ReplyMessage;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.service.validators.ValidatorReplyMessageService;
import socialnetwork.service.validators.ValidatorService;

import java.util.ArrayList;
import java.util.List;

public class ReplyMessageService {
    private final Repository<Long, ReplyMessage> replyMessageRepository;
    private final ValidatorService<ReplyMessage> validatorReplyMessageService = new ValidatorReplyMessageService();

    /**
     * Constructor that creates a new ReplyMessageService
     * @param replyMessageRepository Repository<Long, ReplyMessage>, representing the Repository that handles
     *                               that ReplyMessage data
     */
    public ReplyMessageService(Repository<Long, ReplyMessage> replyMessageRepository) {
        this.replyMessageRepository = replyMessageRepository;
    }

    /**
     * Method that adds a new ReplyMessage
     * @param replyMessageParam ReplyMessage, representing the ReplyMessage to be added
     * @return null, if the ReplyMessage was added successfully
     *         non-null ReplyMessage, otherwise (the ReplyMessage already exists)
     * @throws ValidationException, if the ReplyMessage already exists
     */
    public ReplyMessage addMessage(ReplyMessage replyMessageParam) throws ValidationException {
        ReplyMessage replyMessage = replyMessageRepository.save(replyMessageParam);
        validatorReplyMessageService.validateAdd(replyMessage);
        return replyMessage;
    }

    /**
     * Method that gets one specific Reply Message
     * @param idReplyMessage Long, representing the ID of the Reply Message to be selected
     * @return non-null ReplyMessage, representing
     *       the selected ReplyMessage (if the ID of the Reply Message exists)
     *         null, otherwise
     */
    public ReplyMessage getReplyMessage(Long idReplyMessage) {
        return replyMessageRepository.findOne(idReplyMessage);
    }

    /**
     * Method that gets the conversion between two Users
     * @param idLeftUser Long, representing the ID of the User that initiated the conversion
     * @param idRightUser Long, representing the ID of the User that received the first message
     * @return Iterable<ReplyMessage>, representing the list containing the Reply Messages between the two users
     */
    public Iterable<ReplyMessage> getConversation(Long idLeftUser, Long idRightUser) {
        Iterable<ReplyMessage> listReplyMessages = replyMessageRepository.findAll();
        List<ReplyMessage> conversation = new ArrayList<>();
        listReplyMessages.forEach(replyMessage -> {
            if (replyMessage.getFrom().getId().equals(idLeftUser) &&
                    replyMessage.getTo().get(0).getId().equals(idRightUser)) conversation.add(replyMessage);
            else if (replyMessage.getFrom().getId().equals(idRightUser) &&
                    replyMessage.getTo().get(0).getId().equals(idLeftUser)) conversation.add(replyMessage);
        });
        return conversation;
    }
}
