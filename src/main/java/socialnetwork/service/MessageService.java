package socialnetwork.service;

import socialnetwork.domain.ContentPage;
import socialnetwork.domain.messages.Message;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.database.messages.MessagesDBRepository;
import socialnetwork.service.validators.ValidatorMessageService;
import socialnetwork.utils.Constants;
import socialnetwork.utils.DateConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        //validatorMessageService.validateAdd(message);
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

    /**
     * Method that gets the list Messages sent to a User
     * @param idUser Long, representing the ID of the User
     * @return List<Message>, representing the list of Messages sent to the User
     */
    public List<Message> getListAllMessagesToUser(Long idUser) {
        List<Message> messageList = new ArrayList<>();
        getAllMessagesToUser(idUser).forEach(messageList::add);
        return messageList;
    }

    /**
     * Method that gets the list Messages sent to a User
     * @param idUser Long, representing the ID of the User
     * @param page ContentPage, representing the Page containing the Messages
     * @return List<Message>, representing the list of Messages sent to the User, on that Page
     */
    public List<Message> getListAllMessagesToUser(Long idUser, ContentPage page) {
        List<Message> messageList = new ArrayList<>();
        messagesRepository.findAll(idUser, page).forEach(messageList::add);
        return messageList;
    }

    /**
     * Method that gets the list of messages sent to a specific User, in an interval of time
     * @param idUser Long, representing the ID of the User
     * @param startDate LocalDate, representing the start date of the interval
     * @param endDate LocalDate, representing the end date of the interval
     * @return List<Message>, representing the list of messages
     */
    public List<Message> getListAllMessagesToUserTimeInterval(Long idUser, LocalDate startDate, LocalDate endDate) {
        List<Message> messageList = getListAllMessagesToUser(idUser);
        return messageList
                .stream()
                .filter(message -> (startDate.compareTo(message.getDate().toLocalDate()) <= 0) &&
                        (message.getDate().toLocalDate().compareTo(endDate) <= 0))
                .collect(Collectors.toList());
    }

    /**
     * Method that gets the list of messages sent to a specific User by a Friend, in an interval of time
     * @param idUser Long, representing the ID of the User
     * @param idFriend Long, representing the ID of the Friend
     * @param startDate LocalDate, representing the start date of the interval
     * @param endDate LocalDate, representing the end date of the interval
     * @return List<Message>, representing the list of messages
     */
    public List<Message> getListAllMessagesToUserTimeInterval(Long idUser, Long idFriend, LocalDate startDate, LocalDate endDate) {
        List<Message> messageList = getListAllMessagesToUser(idUser);
        return messageList
                .stream()
                .filter(message -> message.getFrom().getId() == idFriend)
                .filter(message -> (startDate.compareTo(message.getDate().toLocalDate()) <= 0) &&
                        (message.getDate().toLocalDate().compareTo(endDate) <= 0))
                .collect(Collectors.toList());
    }

    /**
     * Method that gets the number of messages sent to a User for each month in a specific Year
     * @param idUser Long, representing the ID of the User
     * @param year Integer, representing the year to determine the number of messages for
     * @return Map<String, Integer>, representing the Map containing pairs of (Month, # messages in that month)
     */
    public Map<String, Integer> getMessagesToUserYear(Long idUser, Integer year) {
        List<Message> messageList = getListAllMessagesToUser(idUser);
        Map<String, Integer> mapMessages = new HashMap<>();
        Constants.months.forEach(month -> {
            long numberMessages = messageList.stream()
                    .filter(message -> message.getDate().getYear() == year &&
                            message.getDate().getMonthValue() == DateConverter.convertMonthStringToInteger(month))
                    .count();
            mapMessages.put(month, (int) numberMessages);
        });
        return mapMessages;
    }
}
