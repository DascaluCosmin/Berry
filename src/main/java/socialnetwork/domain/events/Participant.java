package socialnetwork.domain.events;

import socialnetwork.domain.Entity;
import socialnetwork.domain.Tuple;

public class Participant extends Entity<Tuple<Long, Long>> {
    Boolean isNotified;

    /**
     * Method that creates a new Participant, by default notified to the Event
     * @param idEvent Long, representing the ID of the Event the User participates to
     * @param idUser Long, representing the ID of the User that participates to the Event
     */
    public Participant(Long idEvent, Long idUser) {
        this.isNotified = true;
        setId(new Tuple<>(idEvent, idUser));
    }

    /**
     * Method that creates a new Participant
     * @param idEvent Long, representing the ID of the Event the User participates to
     * @param idUser Long, representing the ID of the User that participates to the Event
     * @param isNotified Boolean, representing whether the Participant is notified about the Event or not
     */
    public Participant(Long idEvent, Long idUser, Boolean isNotified) {
        this.isNotified = isNotified;
        setId(new Tuple<>(idEvent, idUser));
    }

    /**
     * @return isNotified Boolean, representing whether the Participant is notified about the Event or not
     */
    public Boolean getNotified() {
        return isNotified;
    }

    /**
     * @param notified isNotified Boolean, representing whether the Participant is notified about the Event or not
     */
    public void setNotified(Boolean notified) {
        isNotified = notified;
    }

    /**
     * @return String, representing the serialization of the Participant
     */
    @Override
    public String toString() {
        return  "EventID = " + getId().getLeft() + " " +
                "UserID = " + getId().getRight() + " " +
                "isNotified = " + isNotified;
    }
}
