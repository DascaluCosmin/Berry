package socialnetwork.service;

import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.domain.messages.Message;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.MessagesDBRepository;
import socialnetwork.service.validators.ValidatorMessageService;
import socialnetwork.service.validators.ValidatorService;

import java.util.ArrayList;
import java.util.List;

public class MessageService {
    private final MessagesDBRepository messagesRepository;
    private final ValidatorMessageService validatorMessageService = new ValidatorMessageService();

    /**
     * Constructor that creates a new MessageService
     * @param messagesRepository Repository<Long, Message>, representing the Repository that handles
     *                               the Message data
     */
    public MessageService(MessagesDBRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    /**
     * Method that adds a new Message
     * @param messageParam Message, representing the Message to be added
     * @return null, if the Message was added successfully
     *         non-null Message, otherwise (the Message already exists)
     * @throws ValidationException, if the Message already exists
     */
    public Message addMessage(Message messageParam) throws ValidationException {
        validatorMessageService.validateBeforeAdding(messageParam);
        Message message = messagesRepository.save(messageParam);
        validatorMessageService.validateAdd(message);
        return message;
    }

    /**
     * Method that gets all the Messages that are sent to a User
     * @param idUser Long, representing the ID of the User
     * @return Iterable<Message>, representing the list of messages
     */
    public Iterable<Message> getAllMessagesToUser(Long idUser) {
        return messagesRepository.findAll(idUser);
    }
}
