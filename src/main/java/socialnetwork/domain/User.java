package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class User extends Entity<Long>{
    private String firstName;
    private String lastName;
    private List<User> friends;

    /**
     * Constructor that creates a new User
     * @param firstName String, representing the first name of the User
     * @param lastName String, representing the last name of the User
     */
    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        friends = new ArrayList<>();
    }

    /**
     * @return String, representing the first name of the User
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName String, representing the new first name of the User
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return String, representing the last name of the User
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName String, representing the new last name of the User
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return List<User>, representing the list of friends the User has
     */
    public List<User> getFriends() {
        return friends;
    }

    /** Method that serializes an User
     * @return String, representing the serialization of the User
     */
    @Override
    public String toString() {
        AtomicReference<String> friendsString = new AtomicReference<>("");
        int numberOfFriends = friends.size();
        AtomicInteger contor = new AtomicInteger();
        friends.forEach(friend -> {
            if (contor.get() < numberOfFriends - 1) {
                friendsString.set(friendsString + friend.getFirstName() + " " + friend.getLastName() + ", ");
            } else {
                friendsString.set(friendsString + friend.getFirstName() + " " + friend.getLastName());
            }
            contor.getAndIncrement();
        });
        return "Utilizator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends='" + friendsString + '\'' +
                '}';
    }

    /**
     * Method that minimally serializes an User
     * @return String, representing the serialization of the User
     */
    public String toStringFewer() {
        return firstName + " " + lastName;
    }

    /**
     * Method that verifies if two User objects are equal
     * @param obj Object, representing the Object to be verified
     * @return true, if the first name of the User is equal to the first name of the Object
     *               and the last name of the User is equal to the last name of the Object
     *         false, otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        User that = (User) obj;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    /**
     * Method that gets the hashCode of the User
     * @return int, representing the hashCode of the User
     */
    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}