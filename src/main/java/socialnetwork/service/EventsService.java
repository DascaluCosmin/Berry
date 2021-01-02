package socialnetwork.service;

import socialnetwork.domain.ContentPage;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.events.Event;
import socialnetwork.domain.events.Participant;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.domain.validators.ValidatorEvent;
import socialnetwork.repository.database.event.EventDBRepository;
import socialnetwork.repository.database.event.EventParticipationType;
import socialnetwork.repository.database.event.ParticipantDBRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class EventsService {
    private EventDBRepository eventRepository;
    private ParticipantDBRepository participantDBRepository;

    /**
     * Constructor that creates a new EventsService
     * @param eventRepository EventDBRepository, representing the Repository handling the Events data
     * @param participantDBRepository ParticipantDBRepository, representing the Repository handling the Participants data
     */
    public EventsService(EventDBRepository eventRepository, ParticipantDBRepository participantDBRepository) {
        this.eventRepository = eventRepository;
        this.participantDBRepository = participantDBRepository;
    }

    /**
     * Method that adds a new Event
     * @param event Event, representing the Event to be added
     * @return non-null Event, if the Event was added successfully
     *      null, otherwise
     * @throws ValidationException, if the Event has empty fields
     */
    public Event addEvent(Event event) throws ValidationException {
        Validator<Event> validatorEvent = new ValidatorEvent();
        validatorEvent.validate(event);
        return eventRepository.save(event);
    }

    /**
     * Method that adds a new Participant to an Event
     * @param event Event, representing the Event to participate to
     * @param user User, representing the User participating
     * @return non-null Participant, if the Participant was added successfully
     *      null, otherwise
     */
    public Participant addParticipant(Event event, User user) {
        if (!validateParticipant(event, user)) {
            return null;
        }
        return participantDBRepository.save(new Participant(event.getId(), user.getId()));
    }

    /**
     * Method that deletes an Event
     * @param idEvent Long, representing the ID of the Event to be deleted
     * @return non-null Event, if the Event was deleted successfully
     *      null, otherwise
     */
    public Event deleteEvent(Long idEvent) {
        return eventRepository.delete(idEvent);
    }

    /**
     * Method that deletes a Participant from an Event
     * @param event Event, representing the Event to remove participation from
     * @param user User, representing the User to be removed from Event
     * @return non-null Participant, if the Participant was deleted successfully
     *      null, otherwise
     */
    public Participant deleteParticipant(Event event, User user) {
        if (!validateParticipant(event, user)) {
            return null;
        }
        return participantDBRepository.delete(new Tuple<>(event.getId(), user.getId()));
    }

    /**
     * Method that subscribes a User to receive notifications about an Event
     * @param event Event, representing the Event to get notifications about
     * @param user User, representing the User to be subscribed
     * @return non-null Participant, if the User was subscribed successfully
     *      null, otherwise
     */
    public Participant subscribe(Event event, User user) {
        if (!validateParticipant(event, user)) {
            return null;
        }
        return participantDBRepository.update(new Participant(event.getId(), user.getId(), true));
    }

    /**
     * Method that unsubscribes a User from receiving notifications about an Event
     * @param event Event, representing the Event to stop getting notifications about
     * @param user User, representing the User to be unsubscribed
     * @return non-null Participant, if the User was unsubscribed successfully
     *      null, otherwise
     */
    public Participant unsubscribe(Event event, User user) {
        if (!validateParticipant(event, user)) {
            return null;
        }
        return participantDBRepository.update(new Participant(event.getId(), user.getId(), false));
    }

    /**
     * Method that gets the number of Participants to an Event
     * @param event Event, representing the Event to get the number of Participants of
     * @return Long, representing the number of Participants to the Event
     */
    public Long getNumberParticipants(Event event) {
        if (event == null) return 0L;
        AtomicReference<Long> numberParticipants = new AtomicReference<>(0L);
        participantDBRepository.findAll(event.getId()).forEach(participant -> numberParticipants.getAndSet(numberParticipants.get() + 1));
        return numberParticipants.get();
    }

    /**
     * Method that gets the number of Events (Notifications) a User participates to, is Notified and
     * where the Current Date and the Start Date of the Event have a given Day Gap
     * @param idUser Long, representing the ID of the User that participates to the Events
     * @param idUser Long, representing the ID of the User that participates to the Events
     * @return Long, representing the number of Events (Notifications)
     */
    public Long getNumberOfNotificationsEvents(Long idUser, Integer dayDifference) {
        AtomicReference<Long> numberNotifications = new AtomicReference<>(0L);
        eventRepository.findAll(idUser, null, dayDifference).forEach(event -> numberNotifications.getAndSet(numberNotifications.get() + 1));
        return numberNotifications.get();
    }

    /**
     * Method that updates an Event
     * @param event Event, representing the updated Event
     * @return null, if the Event was updated successfully
     *      non-null Event, otherwise
     */
    public Event updateEvent(Event event) {
        return eventRepository.update(event);
    }

    /**
     * Method that gets a specific Event
     * @param idEvent Long, representing the ID of the Event to be selected
     * @return non-null Event, representing the selected Event
     *      null, if the Event doesn't exist
     */
    public Event getEvent(Long idEvent) {
        return eventRepository.findOne(idEvent);
    }

    /**
     * Method that gets one specific Participant
     * @param event Event, representing the Event the Participant attends to
     * @param user User, representing the User being the Participant
     * @return non-null Participant, if the User participates to the Event
     *      null, otherwise
     */
    public Participant getParticipant(Event event, User user) {
        return participantDBRepository.findOne(new Tuple<>(event.getId(), user.getId()));
    }

    /**
     * Method that gets the list of all Events
     * @return List<Event>, representing the list of Events
     */
    public List<Event> getListEvents() {
        List<Event> listEvents = new ArrayList<>();
        eventRepository.findAll().forEach(listEvents::add);
        return listEvents;
    }

    /**
     * Method that gets the list of all Events Hosted by a User, on a specific Page
     * @param idUser Long, representing the ID of the User hosting the Events
     * @param page ContentPage, representing the Page containing the Events
     * @return List<Event>, representing the list of Events hosted by the User, on that Page
     */
    public List<Event> getListEventsHosted(Long idUser, ContentPage page) {
        List<Event> listHostedEvents = new ArrayList<>();
        eventRepository.findAll(idUser, page, EventParticipationType.HOST).forEach(listHostedEvents::add);
        return listHostedEvents;
    }

    /**
     * Method that gets the list of all Events that a User participates to, is Notified and
     * where the Current Date and the Start Date of the Event have a given Day Gap, on a Specific Page
     * @param idUser Long, representing the ID of the User that participates to the Events
     * @param page ContentPage, representing the Page containing the Events
     * @paramidUser Long, representing the ID of the User that participates to the Events
     * @return List<Event>, representing the list of Events
     */
    public List<Event> getListEventsToNotify(Long idUser, ContentPage page, Integer dayDifference) {
        List<Event> listEvents = new ArrayList<>();
        eventRepository.findAll(idUser, page, dayDifference).forEach(listEvents::add);
        return listEvents;
    }

    /**
     * Method that gets the list of all Events that a User participates to, on a specific Page
     * @param idUser Long, representing the ID of the User participating to the Events
     * @param page ContentPage, representing the Page containing the Events
     * @return List<Event>, representing the list of Events that the User participates to, on that Page
     */
    public List<Event> getListEventsParticipate(Long idUser, ContentPage page) {
        List<Event> listParticipateEvents = new ArrayList<>();
        eventRepository.findAll(idUser, page, EventParticipationType.PARTICIPATE).forEach(listParticipateEvents::add);
        return listParticipateEvents;
    }

    /**
     * Method that gets the list of all Events that a User doesn't participate to, on a specific Page
     * @param idUser Long, representing the ID of the User
     * @param page ContentPage, representing the Page containing the Events
     * @return List<Event>, representing the list of Events that the User doesn't participate to, on that Page
     */
    public List<Event> getListEventsDoesntParticipate(Long idUser, ContentPage page) {
        List<Event> listDoesntParticipateEvents = new ArrayList<>();
        eventRepository.findAll(idUser, page, EventParticipationType.NO_PARTICIPATE).forEach(listDoesntParticipateEvents::add);
        return listDoesntParticipateEvents;
    }

    /**
     * Method that validates a Participant
     * @param event Event, representing the Event the Participant participates to
     * @param user User, representing the User (Participant)
     * @return true, if the Participant is Valid - both Event and User are non-null
     *      false, otherwise
     */
    private Boolean validateParticipant(Event event, User user) {
        return event != null && user != null;
    }
}
