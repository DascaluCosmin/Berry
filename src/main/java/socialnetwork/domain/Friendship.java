package socialnetwork.domain;

import socialnetwork.utils.Constants;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Friendship extends Entity<Tuple<Long,Long>> {
    LocalDate date;

    /**
     * Constructor that creates a new Friendship with a given date
     * @param date LocalDate, representing the date the Friendship was created on
     */
    public Friendship(LocalDate date) {
        this.date = date; // Used when we load the friendships from the friendship file
    }

    /**
     * Constructor that creates a new Friendship with a given Tuple pair of ids with the current date
     * @param ids Tuple<Long, Long>, representing the Tuple pair of ids of the new Friendship
     */
    public Friendship(Tuple<Long, Long> ids) {
        setId(ids);
        this.date = LocalDate.now(); // Used when we add a new friendship at run-time
    }

    /**
     * @return LocalDate, representing the date when the friendship was created on
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Method that serializes the Entity
     * @return String, representing the serialization of the Entity
     */
    @Override
    public String toString() {
        return "Friendship{" +
                "date=" + date + " " +
                "idLeft=" + super.getId().getLeft() + " " +
                "idRight=" + super.getId().getRight() +
                '}';
    }
}
