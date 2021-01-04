package socialnetwork.service;

import socialnetwork.domain.ContentPage;
import socialnetwork.domain.messages.ReplyMessage;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.database.messages.ReplyMessageDBRepository;
import socialnetwork.service.validators.ValidatorReplyMessageService;
import socialnetwork.service.validators.ValidatorService;

import java.util.ArrayList;
import java.util.List;

public class ReplyMessageService {
    private final ReplyMessageDBRepository replyMessageRepository;
    private final ValidatorService<ReplyMessage> validatorReplyMessageService = new ValidatorReplyMessageService();

    /**
     * Constructor that creates a new ReplyMessageService
     * @param replyMessageRepository Repository<Long, ReplyMessage>, representing the Repository that handles
     *                               that ReplyMessage data
     */
    public ReplyMessageService(ReplyMessageDBRepository replyMessageRepository) {
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
        return replyMessageRepository.findAll(idLeftUser, idRightUser, null, false);
    }

    /**
     * Method that gets the conversion between two Users on a specific Page
     * @param idLeftUser Long, representing the ID of the User that initiated the conversion
     * @param idRightUser Long, representing the ID of the User that received the first message
     * @param page ContentPage, representing the Page containing the Conversation
     * @return List<ReplyMessage>, representing the list containing the Reply Messages between the two users,
     *                             ordered by the most recent
     */
    public List<ReplyMessage> getListConversationOnPage(Long idLeftUser, Long idRightUser, ContentPage page) {
        List<ReplyMessage> conversation = new ArrayList<>();
        replyMessageRepository.findAll(idLeftUser, idRightUser, page, true).forEach(conversation::add);
        return conversation;
    }
}
